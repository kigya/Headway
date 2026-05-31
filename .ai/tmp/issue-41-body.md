<img width="119" height="117" alt="Image" src="https://github.com/user-attachments/assets/cdb123e1-2c2b-40c3-a924-5478a7d00154" />

<img width="241" height="158" alt="Image" src="https://github.com/user-attachments/assets/271f8ed6-a2fd-4bf8-8fe5-327d356e6795" />

https://www.figma.com/design/CbcTJunkStYfoawELSAZt4/Headway?node-id=2432-17058&t=C7v7fmPOvU0qc8IB-4

https://www.figma.com/design/CbcTJunkStYfoawELSAZt4/Headway?node-id=2323-47157&t=C7v7fmPOvU0qc8IB-4

Add a `FreudAsyncImage` component to the design system that renders an image from a URL the same way on Android, iOS, Web, and Desktop. The screen passes a link; the component handles loading, placeholder, error, cache, and appearance so the app does not reimplement this every time.

The component must accept `url` (string) and draw the image from that link. If `url` is empty/invalid, treat it as an error and show fallback immediately. Fallback must be configurable via a parameter (type-safe, not a slot lambda): for example a design-system resource icon/image, or a predefined DS fallback (a "picture" icon as in the mockup). On load failure (or empty url) the component must always show something reasonable, not leave empty space.

While loading, show a shimmer — not a flat gray rectangle, but a shimmer effect in the same shape as the final image (circle shimmer in a circle, rounded rect shimmer in the same radius). Shimmer appears as soon as loading starts and disappears when the image renders successfully or when switching to fallback on error. Transition from shimmer to content should be soft (light crossfade) without heavy animation.

The component must support both circular and rectangular shapes via an explicit parameter, not by passing shape from outside. Expect something like enum/sealed: circle (avatars) and rectangle (content/card images). For rectangles, support controlled corner radius (design token) without excessive customization. Border/stroke around the image is not needed and must not be supported in the public API: strokes on mockups were demonstrative only.

Caching: by default cache loaded images so revisiting a screen, scrolling, or navigating back does not reload and flash shimmer unnecessarily. Caching must work on all four platforms and stay transparent to consumers — callers should not configure cache at every use site. Where the platform allows, use memory and disk cache; if disk cache is unavailable on a platform, at least memory cache with the same external semantics. Optionally expose a `cachePolicy` (enabled/disabled), but prefer correct defaults and a small API.

Also support basics for normal use: `contentScale` (crop/fit), `contentDescription` (nullable, for accessibility), and standard `modifier`. The component must not guess its own size — it must respect sizes from the modifier (`.size(…)`, `.fillMaxWidth()`, etc.) and clip content to the shape internally.

`FreudAsyncImage` must not leak implementation details: no direct dependencies on a specific loading library in the public API, no slot lambdas for content. At most type-safe specs/enums: shape, fallback spec, and if needed cache policy.

In previews cover at least: circular and rectangular shape, "loading" (shimmer visible), "success" (in preview simulate with a local resource/substitute loader because network in preview is unstable), "error" (fallback visible), plus light/dark. Also a case where the component is stretched wide (web/desktop) to verify shape and `contentScale` are preserved.
