---
icon: forward
---

# Live Templates

Общие Live Templates лежат в репозитории; ставятся через Gradle **из `server/`** (подключён `config/templates/installer.gradle.kts`).

## Установка

```
cd server && ./gradlew installLiveTemplates
```

Файлы копируются в каталог шаблонов Android Studio. Перезапустите IDE (или **File → Invalidate Caches / Restart**). Шаблоны ищите в **Settings → Editor → Live Templates**, группа **Headway**.

## Если автоопределение каталога IDE не сработало

Укажите каталог конфигурации вручную (пример для macOS):

```
cd server && ./gradlew installLiveTemplates -PasConfigDir="$HOME/Library/Application Support/Google/AndroidStudio2025.1"
```

## Ручная установка

Скопируйте XML из `config/templates/` в каталог templates вашей версии Android Studio:

* macOS: `~/Library/Application Support/Google/AndroidStudio<версия>/templates/`
* Linux: `~/.config/Google/AndroidStudio<версия>/templates/`
* Windows: `%APPDATA%\Google\AndroidStudio<версия>\templates\`
