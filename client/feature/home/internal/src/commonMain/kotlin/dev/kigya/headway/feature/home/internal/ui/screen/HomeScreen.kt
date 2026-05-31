package dev.kigya.headway.feature.home.internal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.kigya.headway.core.designSystem.component.FreudAsyncImage
import dev.kigya.headway.core.designSystem.component.FreudAsyncImageShape
import dev.kigya.headway.core.designSystem.component.FreudFallback
import dev.kigya.headway.core.designSystem.component.FreudHomeHeader
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderContent
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderMetrics
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.FreudWideNavigation
import dev.kigya.headway.core.designSystem.component.FreudWideNavigationItem
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.isWide
import dev.kigya.headway.core.session.model.HomeAccessRole
import dev.kigya.headway.core.session.model.HomeActionSemanticType
import dev.kigya.headway.core.session.model.HomeActionVisualStyle
import dev.kigya.headway.core.session.model.HomeScreenAction
import dev.kigya.headway.core.session.model.HomeScreenSummary
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homeActionContainer
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homeBackground
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homeGridCellBorder
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homeGridCellSurface
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homePrimaryText
import kotlinx.collections.immutable.toPersistentList
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = state,
        onRetryLoad = viewModel::onRetryLoad,
        onActionClick = viewModel::onActionClick,
        onToggleWideNavigation = viewModel::onToggleWideNavigation,
    )
}

@Composable
private fun HomeScreenContent(
    state: State<HomeStore.State>,
    onRetryLoad: () -> Unit,
    onActionClick: (HomeActionSemanticType) -> Unit,
    onToggleWideNavigation: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeTheme.colorScheme.homeBackground.value),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            FreudFallback(
                isError = state.value.errorMessage != null,
                onRetry = onRetryLoad,
            ) {
                if (state.value.isLoading && state.value.summary == null) {
                    Box(modifier = Modifier.fillMaxSize())
                } else {
                    state.value.summary?.let { summary ->
                        HomeLoadedBody(
                            summary = summary,
                            isWideNavigationVisible = state.value.isWideNavigationVisible,
                            onActionClick = onActionClick,
                            onToggleWideNavigation = onToggleWideNavigation,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeLoadedBody(
    summary: HomeScreenSummary,
    isWideNavigationVisible: Boolean,
    onActionClick: (HomeActionSemanticType) -> Unit,
    onToggleWideNavigation: () -> Unit,
) {
    val headerContent = remember(summary) { buildHeaderContent(summary) }
    if (isWide()) {
        FreudWideNavigation(
            items = summary.actions.map { action ->
                FreudWideNavigationItem(
                    key = action.semanticType.name,
                    label = FreudTextValue.text(action.title),
                    iconUrl = action.iconUrl,
                )
            }.toPersistentList(),
            selectedKey = HomeActionSemanticType.Home.name,
            isRailVisible = isWideNavigationVisible,
            onToggleRail = onToggleWideNavigation,
            headerLeadingContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(HomeTheme.dimension.dp8.value),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FreudText(
                        value = FreudTextValue.text("headway"),
                        color = HomeTheme.colorScheme.homePrimaryText,
                        typography = HomeTheme.typography.headingSmExtraBold,
                    )
                }
            },
            headerTrailingContent = {
                Row(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = onToggleWideNavigation,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(HomeTheme.dimension.dp2.value),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FreudText(
                        value = FreudTextValue.text("<<"),
                        color = HomeTheme.colorScheme.homePrimaryText,
                        typography = HomeTheme.typography.textLgBold,
                    )
                }
            },
            onItemClick = { item ->
                val semantic = HomeActionSemanticType.entries.first { it.name == item.key }
                onActionClick(semantic)
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(HomeTheme.dimension.dp24.value),
                verticalArrangement = Arrangement.spacedBy(HomeTheme.dimension.dp16.value),
            ) {
                FreudHomeHeader(
                    content = headerContent,
                    avatarContentDescription = summary.userSummary.displayName,
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HomeTheme.dimension.dp24.value),
            verticalArrangement = Arrangement.spacedBy(HomeTheme.dimension.dp16.value),
        ) {
            FreudHomeHeader(
                content = headerContent,
                avatarContentDescription = summary.userSummary.displayName,
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = GRID_CELL_MIN_DP.dp),
                horizontalArrangement = Arrangement.spacedBy(HomeTheme.dimension.dp12.value),
                verticalArrangement = Arrangement.spacedBy(HomeTheme.dimension.dp12.value),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                items(
                    items = summary.actions,
                    key = { it.semanticType.name },
                ) { action ->
                    HomeActionGridCell(
                        action = action,
                        onClick = { onActionClick(action.semanticType) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeActionGridCell(
    action: HomeScreenAction,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(HomeTheme.dimension.dp12.value)
    val surfaceToken = when (action.style) {
        HomeActionVisualStyle.FilledPrimary -> HomeTheme.colorScheme.homeActionContainer
        HomeActionVisualStyle.OutlinedAccent,
        HomeActionVisualStyle.Default,
        -> HomeTheme.colorScheme.homeGridCellSurface
    }
    val modifierBase = Modifier
        .aspectRatio(1f)
        .clip(shape)
        .background(surfaceToken.value)
        .then(
            if (action.style == HomeActionVisualStyle.OutlinedAccent) {
                Modifier.border(
                    width = HomeTheme.dimension.dp2.value,
                    color = HomeTheme.colorScheme.homeGridCellBorder.value,
                    shape = shape,
                )
            } else {
                Modifier
            },
        )
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            role = Role.Button,
            onClick = onClick,
        )
        .padding(HomeTheme.dimension.dp12.value)
    Column(
        modifier = modifierBase,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HomeTheme.dimension.dp8.value),
    ) {
        FreudAsyncImage(
            imageUrl = action.iconUrl,
            contentDescription = action.title,
            shape = FreudAsyncImageShape.RoundedRectangle(HomeTheme.dimension.dp8),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Fit,
        )
        FreudText(
            value = FreudTextValue.text(action.title),
            color = HomeTheme.colorScheme.homePrimaryText,
            typography = HomeTheme.typography.textSmSemiBold,
            align = TextAlign.Center,
            maxLines = 3,
        )
    }
}

private fun buildHeaderContent(summary: HomeScreenSummary): FreudHomeHeaderContent {
    val display = summary.userSummary.displayName.trim()
    val given = display.substringBefore(' ').trim().ifEmpty { display }
    val greetingPrimary = FreudTextValue.text("Hi, $display!")
    val greetingShortIfOverflow = if (given != display) {
        FreudTextValue.text("Hi, $given!")
    } else {
        null
    }
    val date = FreudTextValue.text(summary.userSummary.dateLabel)
    val role = summary.userSummary.roleLabel?.let { FreudTextValue.text(it) }
    val metrics = when (summary.userSummary.accessRole) {
        HomeAccessRole.Mentor,
        HomeAccessRole.Employee,
        -> {
            val readiness = summary.userSummary.readinessPercent?.let { percent ->
                FreudTextValue.text("$percent%")
            }
            val next = summary.userSummary.nextSessionTypeLabel?.let { FreudTextValue.text(it) }
            if (readiness == null && next == null) {
                null
            } else {
                FreudHomeHeaderMetrics(
                    readinessLine = readiness,
                    nextSessionLine = next,
                )
            }
        }

        HomeAccessRole.Developer,
        HomeAccessRole.Manager,
        HomeAccessRole.Guest,
        -> null
    }
    return FreudHomeHeaderContent(
        greetingPrimary = greetingPrimary,
        greetingShortIfOverflow = greetingShortIfOverflow,
        dateLabel = date,
        roleLabel = role,
        metrics = metrics,
        avatarImageUrl = summary.userSummary.avatarUrl,
    )
}

private const val GRID_CELL_MIN_DP = 140
