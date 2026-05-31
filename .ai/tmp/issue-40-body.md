<img width="206" height="375" alt="Image" src="https://github.com/user-attachments/assets/1b8a7962-7df9-4d03-b216-62b461616480" />

https://www.figma.com/design/CbcTJunkStYfoawELSAZt4/Headway?node-id=2889-3973&t=C7v7fmPOvU0qc8IB-4

Add a `FreudAlertDialog` component to the design system. It is a modal confirmation dialog that overlays the current screen, dims the background, and asks the user to explicitly choose one of two actions. It always has exactly two buttons: negative and positive. No button slots and no passing "ready-made" buttons from outside: buttons are always drawn by the component, in the same place, same order, and same layout. Externally we only control button labels and their colors.

Input parameters: title text and labels for both buttons. Besides title text, allow changing title color (via token, not raw `Color`). For buttons, allow setting negative button color and positive button color together with its text — for each button, container color and content/text color should be clear so we can match cases where Cancel is neutral and Confirm is accent. Buttons must not be customized for size/shape/placement via the public API.

Also allow customizing the dialog window color (container/modal background) via token for different surfaces or themes. The dialog should embed in a screen in the standard way: render alongside screen content at the same tree level (typical "screen always draws; dialog draws on top when `isVisible`"). The component must not require wrapping the whole screen in special containers or changing hierarchy; it should live as an overlay toggled on/off.

Dimming: the component must draw dimming/overlay under the dialog so it works the same on all Compose Multiplatform targets: Android, iOS, Web, Desktop. Check what Compose Multiplatform provides out of the box (dialog APIs/Popup/Modal) and pick the simplest integration: minimal screen code and predictable behavior everywhere. Ideally callers pass `isVisible`, texts/colors, and button callbacks; FreudAlertDialog handles centering, dimming, blocking background clicks, and draw order.

Behavior: explicit callbacks for negative and positive button clicks. Also decide dismiss-on-outside-click and/or back/escape: either support `onDismissRequest`, or intentionally allow closing only via buttons — but behavior must be the same on all platforms and not depend on accidental platform dialog defaults.

Include pleasant animations.

In previews show: light/dark theme, different dialog container colors, different title colors, several positive/negative color combinations (neutral/accent and two neutrals), short and long text (line breaks), and a case that verifies overlay/dimming. Follow current DS preview style: dedicated preview theme with semantic tokens, cases via `PreviewParameterProvider` to cover variants without manual duplication.
