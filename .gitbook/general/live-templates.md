---
icon: forward
---

# Live Templates

Общие Live Templates лежат в репозитории. Задача `installLiveTemplates` объявлена в `config/templates/installer.gradle.kts`, скрипт подключён и в **`client/build.gradle.kts`**, и в **`server/build.gradle.kts`**, поэтому команду можно запускать **из каталога `client/` или `server/`** (с тем же эффектом).

## Установка

```
cd client && ./gradlew installLiveTemplates
```

или

```
cd server && ./gradlew installLiveTemplates
```

Файлы копируются в каталог шаблонов Android Studio. Перезапустите IDE (или **File → Invalidate Caches / Restart**). Шаблоны ищите в **Settings → Editor → Live Templates**, группа **Headway**.

## Если автоопределение каталога IDE не сработало

Укажите каталог конфигурации вручную (пример для macOS):

```
cd client && ./gradlew installLiveTemplates -PasConfigDir="$HOME/Library/Application Support/Google/AndroidStudio2025.1"
```

(аналогично с `cd server && …`.)

## Ручная установка

Скопируйте XML из `config/templates/` в каталог templates вашей версии Android Studio:

* macOS: `~/Library/Application Support/Google/AndroidStudio<версия>/templates/`
* Linux: `~/.config/Google/AndroidStudio<версия>/templates/`
* Windows: `%APPDATA%\Google\AndroidStudio<версия>\templates\`
