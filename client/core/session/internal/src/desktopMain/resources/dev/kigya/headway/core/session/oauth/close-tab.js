function tryCloseBrowserTab() {
    window.close();
}
function beginCloseWatch(maxAttempts, intervalMs) {
    if (window.__headwayCloseWatchStarted) {
        return;
    }
    window.__headwayCloseWatchStarted = true;
    window.addEventListener("blur", tryCloseBrowserTab);
    window.addEventListener("pagehide", tryCloseBrowserTab);
    document.addEventListener("visibilitychange", function() {
        if (document.visibilityState === "hidden") {
            tryCloseBrowserTab();
        }
    });
    var attempts = 0;
    var closeAttemptIntervalId = window.setInterval(function() {
        attempts += 1;
        tryCloseBrowserTab();
        if (attempts >= maxAttempts) {
            window.clearInterval(closeAttemptIntervalId);
        }
    }, intervalMs);
}
