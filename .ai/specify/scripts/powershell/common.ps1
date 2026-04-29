#!/usr/bin/env pwsh
# Common PowerShell functions analogous to common.sh

function Get-RepoRoot {
    try {
        $result = git rev-parse --show-toplevel 2>$null
        if ($LASTEXITCODE -eq 0) {
            return $result
        }
    } catch {
        # Git command failed
    }
    
    # Fall back to script location for non-git repos
    return (Resolve-Path (Join-Path $PSScriptRoot "../../..")).Path
}

function Get-CurrentBranch {
    # First check if SPECIFY_FEATURE environment variable is set
    if ($env:SPECIFY_FEATURE) {
        return $env:SPECIFY_FEATURE
    }
    
    # Then check git if available
    try {
        $result = git rev-parse --abbrev-ref HEAD 2>$null
        if ($LASTEXITCODE -eq 0) {
            return $result
        }
    } catch {
        # Git command failed
    }
    
    # For non-git repos, try to find the latest feature directory
    $repoRoot = Get-RepoRoot
    $specsDir = Join-Path $repoRoot "specs"
    
    if (Test-Path $specsDir) {
        $latestFeature = ""
        $highest = 0
        $latestTimestamp = ""

        Get-ChildItem -Path $specsDir -Directory | ForEach-Object {
            if ($_.Name -match '^(\d{8}-\d{6})-') {
                # Timestamp-based branch: compare lexicographically
                $ts = $matches[1]
                if ($ts -gt $latestTimestamp) {
                    $latestTimestamp = $ts
                    $latestFeature = $_.Name
                }
            } elseif ($_.Name -match '^(\d+)-') {
                $num = [int]$matches[1]
                if ($num -gt $highest) {
                    $highest = $num
                    if (-not $latestTimestamp) {
                        $latestFeature = $_.Name
                    }
                }
            }
        }

        if ($latestFeature) {
            return $latestFeature
        }
    }
    
    # Final fallback
    return "main"
}

function Test-HasGit {
    try {
        git rev-parse --show-toplevel 2>$null | Out-Null
        return ($LASTEXITCODE -eq 0)
    } catch {
        return $false
    }
}

function Read-FeatureResolutionFromInitOptions {
    param([string]$RepoRoot)
    $defaults = [PSCustomObject]@{
        ValidateGitBranch = $true
        ExtraBranchRegex  = ''
        FixedSpecsSubdir  = ''
    }
    $path = Join-Path $RepoRoot '.ai/specify/init-options.json'
    if (-not (Test-Path $path)) {
        return $defaults
    }
    try {
        $j = Get-Content $path -Raw -Encoding UTF8 | ConvertFrom-Json
        $fr = $j.feature_resolution
        if (-not $fr) {
            return $defaults
        }
        $validate = $true
        if ($null -ne $fr.PSObject.Properties['validate_git_branch']) {
            $validate = [bool]$fr.validate_git_branch
        }
        $extra = ''
        if ($fr.extra_branch_regex -is [string]) {
            $extra = $fr.extra_branch_regex
        }
        $fixed = ''
        if ($fr.fixed_specs_subdir -is [string]) {
            $fixed = $fr.fixed_specs_subdir
        }
        return [PSCustomObject]@{
            ValidateGitBranch = $validate
            ExtraBranchRegex  = $extra
            FixedSpecsSubdir  = $fixed
        }
    } catch {
        return $defaults
    }
}

function Test-FeatureBranch {
    param(
        [string]$Branch,
        [bool]$HasGit
    )

    if (-not $HasGit) {
        Write-Warning "[specify] Warning: Git repository not detected; skipped branch validation"
        return $true
    }

    $repoRoot = Get-RepoRoot
    $fr = Read-FeatureResolutionFromInitOptions -RepoRoot $repoRoot
    if (-not $fr.ValidateGitBranch) {
        return $true
    }

    if ($Branch -match '^[0-9]{3}-' -or $Branch -match '^\d{8}-\d{6}-') {
        return $true
    }

    if ($fr.ExtraBranchRegex -and ($Branch -match $fr.ExtraBranchRegex)) {
        return $true
    }

    Write-Output "ERROR: Not on a feature branch. Current branch: $Branch"
    Write-Output "Feature branches should be named like: 001-feature-name or 20260319-143022-feature-name"
    Write-Output "Or configure .ai/specify/init-options.json feature_resolution, SPECIFY_FEATURE, or fixed_specs_subdir."
    return $false
}

function Find-FeatureDirByPrefix {
    param([string]$RepoRoot, [string]$BranchName)
    $specsDir = Join-Path $RepoRoot 'specs'
    $prefix = $null
    if ($BranchName -match '^(\d{8}-\d{6})-') {
        $prefix = $Matches[1]
    } elseif ($BranchName -match '^(\d{3})-') {
        $prefix = $Matches[1]
    } else {
        return (Join-Path $specsDir $BranchName)
    }

    $matchesDirs = @(Get-ChildItem -Path $specsDir -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -like "$prefix-*" })
    if ($matchesDirs.Count -eq 0) {
        return (Join-Path $specsDir $BranchName)
    }
    if ($matchesDirs.Count -eq 1) {
        return $matchesDirs[0].FullName
    }
    Write-Error "Multiple spec directories found with prefix '$prefix': $($matchesDirs.Name -join ', ')"
    return $null
}

function Resolve-FeatureDir {
    param([string]$RepoRoot, [string]$BranchName)
    $specsDir = Join-Path $RepoRoot 'specs'
    if ($BranchName -match '^(\d{8}-\d{6})-' -or $BranchName -match '^(\d{3})-') {
        return Find-FeatureDirByPrefix -RepoRoot $RepoRoot -BranchName $BranchName
    }
    if ($BranchName -match '^(client|server|fullstack)-headway/(.+)$') {
        return (Join-Path $specsDir $matches[2])
    }
    $fr = Read-FeatureResolutionFromInitOptions -RepoRoot $RepoRoot
    if ($fr.FixedSpecsSubdir) {
        return (Join-Path $specsDir $fr.FixedSpecsSubdir)
    }
    Write-Error "Branch '$BranchName' does not match Speckit patterns. Set SPECIFY_FEATURE or feature_resolution.fixed_specs_subdir in .ai/specify/init-options.json."
    return $null
}

function Get-FeaturePathsEnv {
    $repoRoot = Get-RepoRoot
    $currentBranch = Get-CurrentBranch
    $hasGit = Test-HasGit
    $featureDir = Resolve-FeatureDir -RepoRoot $repoRoot -BranchName $currentBranch
    if ($null -eq $featureDir) {
        throw "Failed to resolve feature directory"
    }

    [PSCustomObject]@{
        REPO_ROOT      = $repoRoot
        CURRENT_BRANCH = $currentBranch
        HAS_GIT        = $hasGit
        FEATURE_DIR    = $featureDir
        FEATURE_SPEC   = Join-Path $featureDir 'spec.md'
        IMPL_PLAN      = Join-Path $featureDir 'plan.md'
        TASKS          = Join-Path $featureDir 'tasks.md'
        RESEARCH       = Join-Path $featureDir 'research.md'
        DATA_MODEL     = Join-Path $featureDir 'data-model.md'
        QUICKSTART     = Join-Path $featureDir 'quickstart.md'
        CONTRACTS_DIR  = Join-Path $featureDir 'contracts'
    }
}

function Test-FileExists {
    param([string]$Path, [string]$Description)
    if (Test-Path -Path $Path -PathType Leaf) {
        Write-Output "  ✓ $Description"
        return $true
    } else {
        Write-Output "  ✗ $Description"
        return $false
    }
}

function Test-DirHasFiles {
    param([string]$Path, [string]$Description)
    if ((Test-Path -Path $Path -PathType Container) -and (Get-ChildItem -Path $Path -ErrorAction SilentlyContinue | Where-Object { -not $_.PSIsContainer } | Select-Object -First 1)) {
        Write-Output "  ✓ $Description"
        return $true
    } else {
        Write-Output "  ✗ $Description"
        return $false
    }
}

# Resolve a template name to a file path using the priority stack:
#   1. .ai/specify/templates/overrides/
#   2. .ai/specify/presets/<preset-id>/templates/ (sorted by priority from .registry)
#   3. .ai/specify/extensions/<ext-id>/templates/
#   4. .ai/specify/templates/ (core)
function Resolve-Template {
    param(
        [Parameter(Mandatory=$true)][string]$TemplateName,
        [Parameter(Mandatory=$true)][string]$RepoRoot
    )

    $base = Join-Path $RepoRoot '.ai/specify/templates'

    # Priority 1: Project overrides
    $override = Join-Path $base "overrides/$TemplateName.md"
    if (Test-Path $override) { return $override }

    # Priority 2: Installed presets (sorted by priority from .registry)
    $presetsDir = Join-Path $RepoRoot '.ai/specify/presets'
    if (Test-Path $presetsDir) {
        $registryFile = Join-Path $presetsDir '.registry'
        $sortedPresets = @()
        if (Test-Path $registryFile) {
            try {
                $registryData = Get-Content $registryFile -Raw | ConvertFrom-Json
                $presets = $registryData.presets
                if ($presets) {
                    $sortedPresets = $presets.PSObject.Properties |
                        Sort-Object { if ($null -ne $_.Value.priority) { $_.Value.priority } else { 10 } } |
                        ForEach-Object { $_.Name }
                }
            } catch {
                # Fallback: alphabetical directory order
                $sortedPresets = @()
            }
        }

        if ($sortedPresets.Count -gt 0) {
            foreach ($presetId in $sortedPresets) {
                $candidate = Join-Path $presetsDir "$presetId/templates/$TemplateName.md"
                if (Test-Path $candidate) { return $candidate }
            }
        } else {
            # Fallback: alphabetical directory order
            foreach ($preset in Get-ChildItem -Path $presetsDir -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -notlike '.*' }) {
                $candidate = Join-Path $preset.FullName "templates/$TemplateName.md"
                if (Test-Path $candidate) { return $candidate }
            }
        }
    }

    # Priority 3: Extension-provided templates
    $extDir = Join-Path $RepoRoot '.ai/specify/extensions'
    if (Test-Path $extDir) {
        foreach ($ext in Get-ChildItem -Path $extDir -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -notlike '.*' } | Sort-Object Name) {
            $candidate = Join-Path $ext.FullName "templates/$TemplateName.md"
            if (Test-Path $candidate) { return $candidate }
        }
    }

    # Priority 4: Core templates
    $core = Join-Path $base "$TemplateName.md"
    if (Test-Path $core) { return $core }

    return $null
}

