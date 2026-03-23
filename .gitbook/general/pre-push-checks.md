---
icon: seedling
---

# Проверки перед push

Цель — не слать в CI заведомо сломанный код и поймать detekt/lint раньше.

## Установка git hooks (один раз)

Скрипт `config/git/hooks/installer.gradle.kts` подключён и в **`client/build.gradle.kts`**, и в **`server/build.gradle.kts`**. Установить хуки можно из **любого из этих каталогов**:

```
cd client && ./gradlew installGitHooks
```

или

```
cd server && ./gradlew installGitHooks
```

Это выставляет `core.hooksPath` на `config/git/hooks` и делает файлы хуков исполняемыми (на Unix).

## Что делает pre-push

Скрипт `config/git/hooks/pre-push` запускает из **текущей рабочей директории git** команды `./gradlew detekt` и `./gradlew lint`. В корне монорепозитория отдельного `gradlew` нет: если push делаете из корня и hook падает с «gradlew not found», выполняйте проверки вручную (ниже) или доработайте hook под ваш сценарий.

## Ручная проверка (надёжный вариант)

Перед push рекомендуется:

```
cd client && ./gradlew detekt
cd server && ./gradlew detekt
```

При работе только с одной стороной достаточно соответствующей команды. Для существенных изменений на клиенте дополнительно:

```
cd client && ./gradlew app:headwayAndroid:assembleDebug
```

Для сервера:

```
cd server && ./gradlew build
```

## Обход hook (редко)

```
git push --no-verify
```

Используйте только осознанно (например, экстренный hotfix по договорённости).

## Альтернатива: только путь к hooks

```
git config core.hooksPath config/git/hooks
chmod -R +x config/git/hooks
```

Проверка:

```
git config --get core.hooksPath
```
