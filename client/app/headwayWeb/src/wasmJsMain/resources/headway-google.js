(function () {
    window.headwayStartGoogleCredentialFlow = function (clientId) {
        return new Promise(function (resolve, reject) {
            function fail(message) {
                reject(new Error(message));
            }
            function begin() {
                if (!window.google || !google.accounts || !google.accounts.id) {
                    fail('gsi_not_loaded');
                    return;
                }
                var settled = false;
                function finishOk(cred) {
                    if (settled) {
                        return;
                    }
                    settled = true;
                    resolve(cred);
                }
                function finishFail(msg) {
                    if (settled) {
                        return;
                    }
                    settled = true;
                    fail(msg);
                }
                google.accounts.id.initialize({
                    client_id: clientId,
                    callback: function (resp) {
                        if (resp && resp.credential) {
                            finishOk(resp.credential);
                        } else {
                            finishFail('no_credential');
                        }
                    },
                    auto_select: false,
                });
                var div = document.createElement('div');
                div.style.position = 'fixed';
                div.style.left = '-9999px';
                div.style.top = '0';
                div.style.opacity = '0';
                document.body.appendChild(div);
                google.accounts.id.renderButton(div, {
                    type: 'standard',
                    theme: 'outline',
                    size: 'large',
                    text: 'signin_with',
                });
                window.setTimeout(function () {
                    var btn = div.querySelector('div[role="button"]');
                    if (btn && typeof btn.click === 'function') {
                        btn.click();
                    } else {
                        finishFail('no_google_button');
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
