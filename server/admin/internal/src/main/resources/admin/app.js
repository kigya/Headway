import React from "https://esm.sh/react@18.3.1";
import { createRoot } from "https://esm.sh/react-dom@18.3.1/client";
import htm from "https://esm.sh/htm@3.1.1";

const html = htm.bind(React.createElement);

const errorMessages = {
    invalid_state: "The GitHub sign-in flow could not be verified. Please try again.",
    auth_failed: "GitHub sign-in failed. Please try again with a valid GitHub account.",
    github_unavailable: "GitHub is temporarily unavailable. Please try again shortly.",
    session_expired: "Your admin session expired. Sign in again to continue.",
};

function App() {
    const [bootstrap, setBootstrap] = React.useState(null);
    const [routeState, setRouteState] = React.useState(() => parseRouteState(window.location.search));
    const [isLoading, setIsLoading] = React.useState(true);
    const [feedback, setFeedback] = React.useState(null);
    const [isInviting, setIsInviting] = React.useState(false);
    const [isLoggingOut, setIsLoggingOut] = React.useState(false);
    const [form, setForm] = React.useState({
        email: "",
        role: "",
        department: "",
    });

    React.useEffect(() => {
        let isMounted = true;

        async function loadBootstrap() {
            setIsLoading(true);
            try {
                const response = await fetch(resolvePath("./bootstrap"), {
                    credentials: "same-origin",
                });
                const raw = await response.json();
                if (!isMounted) {
                    return;
                }
                const data = normalizeBootstrap(raw);
                if (!data) {
                    setBootstrap(null);
                    return;
                }
                setBootstrap(data);
                setForm((currentForm) => ({
                    email: currentForm.email,
                    role: currentForm.role || data.roleOptions[0]?.value || "",
                    department: currentForm.department || data.departmentOptions[0]?.value || "",
                }));
            } finally {
                if (isMounted) {
                    setIsLoading(false);
                }
            }
        }

        loadBootstrap();

        return () => {
            isMounted = false;
        };
    }, []);

    const errorMessage = routeState.error ? errorMessages[routeState.error] : null;
    const session = bootstrap?.session ?? null;
    const environment = bootstrap?.environment ?? "";
    const isProd = bootstrap?.isProd ?? false;
    const repositoryFullName = bootstrap?.repositoryFullName ?? "Headway";

    async function handleInviteSubmit(event) {
        event.preventDefault();
        setFeedback(null);
        setIsInviting(true);

        try {
            const response = await fetch(resolvePath("./invite"), {
                method: "POST",
                credentials: "same-origin",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email: form.email.trim(),
                    role: form.role,
                    department: form.department,
                }),
            });

            if (response.status === 401) {
                setBootstrap((currentBootstrap) => currentBootstrap ? { ...currentBootstrap, session: null } : currentBootstrap);
                setRouteState({
                    error: "session_expired",
                    screen: null,
                    login: null,
                    repo: null,
                });
                setFeedback(null);
                return;
            }

            if (!response.ok) {
                const message = await response.text();
                setFeedback({
                    type: "error",
                    message: message || "Unable to send invite right now.",
                });
                return;
            }

            const data = await response.json();
            setFeedback({
                type: "success",
                message: data.message,
            });
            setForm((currentForm) => ({
                ...currentForm,
                email: "",
            }));
        } finally {
            setIsInviting(false);
        }
    }

    async function handleLogout() {
        setIsLoggingOut(true);
        try {
            await fetch(resolvePath("./logout"), {
                method: "POST",
                credentials: "same-origin",
            });
        } finally {
            const url = new URL(window.location.href);
            url.search = "";
            window.location.assign(url.toString());
        }
    }

    let view = html`
        <${LoadingView} />
    `;

    if (!isLoading && bootstrap) {
        if (session) {
            view = html`
                <${DashboardView}
                    environment=${environment}
                    isProd=${isProd}
                    session=${session}
                    repositoryFullName=${repositoryFullName}
                    roleOptions=${bootstrap.roleOptions}
                    departmentOptions=${bootstrap.departmentOptions}
                    form=${form}
                    setForm=${setForm}
                    feedback=${feedback}
                    onSubmit=${handleInviteSubmit}
                    onLogout=${handleLogout}
                    isInviting=${isInviting}
                    isLoggingOut=${isLoggingOut}
                />
            `;
        } else if (routeState.screen === "denied") {
            view = html`
                <${DeniedView}
                    environment=${environment}
                    isProd=${isProd}
                    login=${routeState.login}
                    repo=${routeState.repo || repositoryFullName}
                />
            `;
        } else {
            view = html`
                <${LoginView}
                    environment=${environment}
                    isProd=${isProd}
                    repositoryFullName=${repositoryFullName}
                    errorMessage=${errorMessage}
                />
            `;
        }
    }

    return html`
        <div className="app-shell">
            <div className="app-frame">
                <${IllustrationPane} repositoryFullName=${repositoryFullName} />
                <div className="content-pane">
                    ${view}
                </div>
            </div>
        </div>
    `;
}

function LoadingView() {
    return html`
        <section className="content-card loading">
            <div className="loading-body">
                <div className="card-toolbar loading-toolbar">
                    <div className="status-pill">Preparing access</div>
                </div>
                <div className="spinner"></div>
                <div className="view-header">
                    <h1>Loading developer settings</h1>
                    <p>Checking this environment and whether you already have an admin session.</p>
                </div>
            </div>
        </section>
    `;
}

function LoginView({ environment, isProd, repositoryFullName, errorMessage }) {
    return html`
        <section className="content-card">
            <div className="card-toolbar">
                <div className="status-pill">Headway internal</div>
                <div className=${`env-pill ${isProd ? "prod" : ""}`}>ENV: ${environment}</div>
            </div>
            <header className="view-header">
                <h1>Developer settings</h1>
                <p>
                    Internal tools for engineers working on Headway in this environment. Sign in with GitHub using an
                    account that is already a collaborator on
                    <strong> ${repositoryFullName}</strong>.
                </p>
            </header>
            <div className="view-body">
                ${errorMessage ? html`<div className="banner error">${errorMessage}</div>` : null}
                <div className="actions">
                    <a className="button-primary" href=${resolvePath("./auth/github")}>
                        <span className="button-icon">G</span>
                        <span>Sign in with GitHub</span>
                    </a>
                </div>
                <div className="meta-note">
                    If you are not a collaborator on that repository, GitHub will not grant access and this panel will
                    stay closed.
                </div>
            </div>
        </section>
    `;
}

function DeniedView({ environment, isProd, login, repo }) {
    return html`
        <section className="content-card">
            <div className="card-toolbar">
                <div className="status-pill">Access denied</div>
                <div className=${`env-pill ${isProd ? "prod" : ""}`}>ENV: ${environment}</div>
            </div>
            <header className="view-header">
                <h1>Not a collaborator</h1>
                <p>
                    Developer settings are only for GitHub collaborators. The account
                    <strong> @${login || "unknown"}</strong> is not listed as a collaborator on
                    <strong> ${repo}</strong>.
                </p>
            </header>
            <div className="view-body">
                <div className="banner error">
                    Ask a repository admin to add you as a collaborator, or sign in with a different GitHub account that
                    already has access.
                </div>
                <div className="actions">
                    <a className="button-primary" href=${resolvePath("./auth/github")}>
                        <span className="button-icon">G</span>
                        <span>Try another GitHub account</span>
                    </a>
                    <a className="button-outline" href=${currentPagePath()}>
                        Back to login
                    </a>
                </div>
            </div>
        </section>
    `;
}

function DashboardView({
    environment,
    isProd,
    session,
    repositoryFullName,
    roleOptions,
    departmentOptions,
    form,
    setForm,
    feedback,
    onSubmit,
    onLogout,
    isInviting,
    isLoggingOut,
}) {
    return html`
        <section className="content-card">
            <div className="card-toolbar">
                <div className="status-pill">Invite tool</div>
                <div className=${`env-pill ${isProd ? "prod" : ""}`}>ENV: ${environment}</div>
            </div>
            <header className="view-header">
                <h1>Invite collaborators</h1>
                <p>
                    You are signed in as a verified collaborator. Invites created here apply to this Headway environment
                    and repository
                    <strong> ${repositoryFullName}</strong>.
                </p>
            </header>
            <div className="view-body">
                <div className="account-box">
                    <div className="account-meta">
                        <div className="avatar">
                            ${session.githubAvatarUrl
                                ? html`<img src=${session.githubAvatarUrl} alt="GitHub avatar" />`
                                : html`<span>@</span>`}
                        </div>
                        <div className="account-copy">
                            <strong>@${session.githubLogin}</strong>
                            <span>Signed in through GitHub</span>
                        </div>
                    </div>
                    <button
                        className="button-outline"
                        type="button"
                        onClick=${onLogout}
                        disabled=${isLoggingOut}
                    >
                        ${isLoggingOut ? "Signing out..." : "Log out"}
                    </button>
                </div>

                ${feedback
                    ? html`<div className=${`banner ${feedback.type}`}>${feedback.message}</div>`
                    : null}

                <form className="invite-form" onSubmit=${onSubmit}>
                    <div className="field-grid">
                        <label className="field full">
                            <span>Email</span>
                            <input
                                type="email"
                                placeholder="name@headway.com"
                                value=${form.email}
                                onInput=${(event) => setForm((currentForm) => ({
                                    ...currentForm,
                                    email: event.target.value,
                                }))}
                                required
                            />
                        </label>

                        <label className="field">
                            <span>Role</span>
                            <select
                                value=${form.role}
                                onChange=${(event) => setForm((currentForm) => ({
                                    ...currentForm,
                                    role: event.target.value,
                                }))}
                                required
                            >
                                ${roleOptions.map((option) => html`
                                    <option key=${option.value} value=${option.value}>${option.label}</option>
                                `)}
                            </select>
                        </label>

                        <label className="field">
                            <span>Department</span>
                            <select
                                value=${form.department}
                                onChange=${(event) => setForm((currentForm) => ({
                                    ...currentForm,
                                    department: event.target.value,
                                }))}
                                required
                            >
                                ${departmentOptions.map((option) => html`
                                    <option key=${option.value} value=${option.value}>${option.label}</option>
                                `)}
                            </select>
                        </label>
                    </div>

                    <div className="actions">
                        <button className="button-primary" type="submit" disabled=${isInviting}>
                            ${isInviting ? "Sending invite..." : "Send invite"}
                        </button>
                    </div>
                </form>

                <div className="dashboard-footnote">
                    Invites are processed on the server; the browser only sends the request and shows the outcome.
                </div>
            </div>
        </section>
    `;
}

function IllustrationPane({ repositoryFullName }) {
    return html`
        <aside className="illustration-pane">
            <div className="brand-mark">
                <div className="brand-logo">H</div>
                <div className="brand-copy">
                    <strong>Headway Admin</strong>
                    <span>Developer settings</span>
                </div>
            </div>

            <div className="illustration-stack">
                <div className="hero-visual">
                    <div className="hero-glow"></div>
                    <div className="orbit orbit-1"></div>
                    <div className="orbit orbit-2"></div>
                    <div className="orbit orbit-3"></div>
                    <div className="orbit-core"></div>
                </div>
            </div>

            <p className="illustration-tagline">
                Internal developer settings for this Headway runtime. Sign-in is limited to GitHub collaborators on
                <strong> ${repositoryFullName}</strong>.
            </p>
        </aside>
    `;
}

function normalizeBootstrap(raw) {
    if (!raw || typeof raw !== "object") {
        return null;
    }
    const session = raw.session;
    return {
        environment: raw.environment ?? "",
        isProd: raw.is_prod ?? false,
        repositoryFullName: raw.repository_full_name ?? "Headway",
        session: session
            ? {
                  githubLogin: session.github_login,
                  githubAvatarUrl: session.github_avatar_url ?? null,
              }
            : null,
        roleOptions: Array.isArray(raw.role_options)
            ? raw.role_options.map((option) => ({
                  value: option.value,
                  label: option.label,
              }))
            : [],
        departmentOptions: Array.isArray(raw.department_options)
            ? raw.department_options.map((option) => ({
                  value: option.value,
                  label: option.label,
              }))
            : [],
    };
}

function parseRouteState(search) {
    const params = new URLSearchParams(search);
    return {
        error: params.get("error"),
        screen: params.get("screen"),
        login: params.get("login"),
        repo: params.get("repo"),
    };
}

function resolvePath(relativePath) {
    return new URL(relativePath, window.location.href).toString();
}

function currentPagePath() {
    const url = new URL(window.location.href);
    url.search = "";
    return url.toString();
}

createRoot(document.getElementById("root")).render(html`<${App} />`);
