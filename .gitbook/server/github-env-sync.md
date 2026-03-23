---
icon: arrows-rotate
---

# githubEnvSync

Плагин Gradle в **`server/build-logic/github-env-sync`**. Подключён в **`server/build.gradle.kts`**. Нужен, чтобы **подтянуть переменные из GitHub** (repository variables + variables окружений **dev** / **prod**) и **сгенерировать файлы** в `server/docker/` из шаблонов.

## Зачем

В GitHub хранятся значения для разных окружений. Локально не дублируем секреты вручную в финальных `env.*` файлах: в репозитории лежат **`*.template`** в `server/docker/template/`, а плагин подставляет плейсхолдеры и записывает результат (например `env.common`, `env.gateway`, …) в каталог назначения.

## Конфигурация в проекте

В `server/build.gradle.kts` задано примерно следующее:

* репозиторий **`kigya/Headway`**;
* шаблоны: `server/docker/template/`;
* вывод: `server/docker/` (корень docker в проекте);
* окружения: **`dev`** и **`prod`**;
* свойства Gradle для токена и имени пользователя: `github.env.sync.token`, `github.env.sync.username`;
* при отсутствии переменной в GitHub сборка может падать (`failOnMissingVariables`).

## Задачи Gradle

| Задача | Назначение |
|--------|------------|
| **`githubLogin`** | OAuth-вход в GitHub, сохранение токена в user Gradle properties (может открыть браузер). |
| **`syncGithubEnv`** | Читает переменные из GitHub API и рендерит шаблоны. |
| **`githubEnvSync`** | Алиас, зависит от `syncGithubEnv`. |

Группа задач в списке Gradle: **github env sync**.

## Как это работает внутри (логика)

1. Берётся токен из Gradle user properties (после `githubLogin`).
2. Проверяются права пользователя на репозиторий (collaborator permission не ниже read).
3. Загружаются **repository variables**, затем для каждого целевого окружения — **environment variables**; словарь объединяется (env перекрывает repo при совпадении имён).
4. Для каждого файла `*.template` в `server/docker/template/` читается текст, подставляются значения, результат пишется в файл с тем же именем **без** суффикса `.template` в каталог вывода.
5. Состояние синхронизации кешируется (файл **`.github-env-sync.state`**) — повторный запуск пропускается, если шаблоны и удалённые переменные не менялись.

## IDE sync

Если включено **`runOnIdeSync`** (по умолчанию да), при синхронизации Gradle из IDE плагин может **автоматически** вызвать логин и синхронизацию, когда сгенерированные файлы устарели. При ошибке в лог пишется предупреждение, синк проекта не обязан прерваться; тогда вручную: `githubLogin`, затем `syncGithubEnv` / `githubEnvSync`.

## Что сделать разработчику

1. Один раз: **`cd server && ./gradlew githubLogin`** (и при необходимости выдать права на репо).
2. При смене переменных в GitHub или шаблонов: **`cd server && ./gradlew githubEnvSync`**.
3. Не коммитить секреты; коммитить можно шаблоны и при необходимости документацию; сами подставленные секретные значения — только локально/в CI по вашей политике.

Если Gradle пишет, что не настроены окружения, проверьте блок `githubEnvSync { environments.set(listOf("dev", "prod")) }` (или legacy `environment`).
