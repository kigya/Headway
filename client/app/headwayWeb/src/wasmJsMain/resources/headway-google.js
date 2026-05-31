(function () {
    window.headwayStartGoogleCredentialFlow = function (clientId) {
        return new Promise(function (resolve, reject) {
            var settled = false;
            var cleanupTasks = [];

            function registerCleanup(task) {
                cleanupTasks.push(task);
            }

            function cleanup() {
                while (cleanupTasks.length > 0) {
                    var task = cleanupTasks.pop();
                    try {
                        task();
                    } catch (ignored) {
                    }
                }
            }

            function finishOk(credential) {
                if (settled) {
                    return;
                }
                settled = true;
                cleanup();
                resolve(credential);
            }

            function finishFail(code) {
                if (settled) {
                    return;
                }
                settled = true;
                cleanup();
                reject(new Error(code));
            }

            function beginOAuthAbandonmentWatch() {
                var popupWindow = null;
                var oauthWindowOpened = false;
                var abandonTimer = null;
                var originalOpen = window.open;

                window.open = function () {
                    var openedWindow = originalOpen.apply(window, arguments);
                    if (openedWindow) {
                        popupWindow = openedWindow;
                        oauthWindowOpened = true;
                    }
                    return openedWindow;
                };
                registerCleanup(function () {
                    window.open = originalOpen;
                });

                var pollTimer = window.setInterval(function () {
                    if (popupWindow && popupWindow.closed) {
                        finishFail('cancelled');
                    }
                }, 300);
                registerCleanup(function () {
                    window.clearInterval(pollTimer);
                });

                function clearAbandonTimer() {
                    if (abandonTimer !== null) {
                        window.clearTimeout(abandonTimer);
                        abandonTimer = null;
                    }
                }

                function onWindowBlur() {
                    oauthWindowOpened = true;
                    clearAbandonTimer();
                }

                function onWindowFocus() {
                    if (!oauthWindowOpened || settled) {
                        return;
                    }
                    clearAbandonTimer();
                    abandonTimer = window.setTimeout(function () {
                        if (!settled) {
                            finishFail('cancelled');
                        }
                    }, 1500);
                }

                window.addEventListener('blur', onWindowBlur);
                window.addEventListener('focus', onWindowFocus);
                registerCleanup(function () {
                    window.removeEventListener('blur', onWindowBlur);
                    window.removeEventListener('focus', onWindowFocus);
                    clearAbandonTimer();
                });
            }

            function fail(message) {
                finishFail(message);
            }

            function begin() {
                if (!window.google || !google.accounts || !google.accounts.id) {
                    fail('gsi_not_loaded');
                    return;
                }

                var timeoutTimer = window.setTimeout(function () {
                    finishFail('cancelled');
                }, 300000);
                registerCleanup(function () {
                    window.clearTimeout(timeoutTimer);
                });

                google.accounts.id.initialize({
                    client_id: clientId,
                    callback: function (response) {
                        if (response && response.credential) {
                            finishOk(response.credential);
                        } else {
                            finishFail('cancelled');
                        }
                    },
                    auto_select: false,
                });
                var container = document.createElement('div');
                container.style.position = 'fixed';
                container.style.left = '-9999px';
                container.style.top = '0';
                container.style.opacity = '0';
                document.body.appendChild(container);
                registerCleanup(function () {
                    if (container.parentNode) {
                        container.parentNode.removeChild(container);
                    }
                });
                google.accounts.id.renderButton(container, {
                    type: 'standard',
                    theme: 'outline',
                    size: 'large',
                    text: 'signin_with',
                });
                window.setTimeout(function () {
                    beginOAuthAbandonmentWatch();
                    var button = container.querySelector('div[role="button"]');
                    if (button && typeof button.click === 'function') {
                        button.click();
                    } else {
                        fail('no_google_button');
                    }
                }, 500);
            }

            if (document.readyState === 'complete') {
                begin();
            } else {
                window.addEventListener('load', begin);
            }
        });
    };
})();
