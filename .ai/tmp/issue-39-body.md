<img width="258" height="540" alt="Image" src="https://github.com/user-attachments/assets/77279b5c-56a4-46ff-91b2-e12db2d9b0df" />

https://www.figma.com/design/CbcTJunkStYfoawELSAZt4/Headway?node-id=2323-47381&t=C7v7fmPOvU0qc8IB-4

Add a `FreudRadioGroup` component to the design system for "choose one of several". It behaves as a radio group: only one item can be active at a time (if starting with no selection is needed, support `selectedIndex: Int?`; by default assume one item is selected). Individual items exist but are internal and must not be a public component: make `FreudRadioItem` `private`/`internal`; the public entry point is only `FreudRadioGroup`.

Layout modes (mockups show different patterns):

1. Vertical list filling container width, each item stretches (like "Good answer / Not answered / Yes, but leave a comment").
2. Grid of equal-size tiles ("Check / Spot / Mock / Soft Skills / Custom").
3. Equal width for all items even when content differs (width = max among items).
4. Wrap content with line breaks — each item sized to its content, laid out flow/wrap (emoji row with a wider "None" wrapping to the next line).

The group must support fixed sizes, equal sizes, wrap with breaks, and vertical fill width.

Item content: icon only, icon + text, or text only. Emojis/flags count as icons and use the same API. No slot lambdas in parameters. Use a clear data type for leading icons (`sealed interface`/`sealed class`) for allowed inputs (resource icon, text icon/emoji). Icons must support two tint modes: tint changes on selection (emoji group) or stays unchanged (language flags). Control this via parameter/spec, not hidden logic.

Colors and states: tiles can use different color schemes. Vertical variants can be standardized with simple selected/unselected; other modes must change visuals by selected/unselected (background/border/content) to match highlighted selection vs neutral unselected. Tokenize everything via `FreudDsToken`; no raw colors/sizes in public API; boolean params use `isXxx`.

Behavior API: expose current selection (`selectedIndex`/`selectedKey`) and `onSelectionChanged` on click with index (or key). Clicking another item clears the previous selection and fires the callback only when selection actually changes.

Previews: light/dark, vertical fill, fixed grid, equal width, wrap with breaks, icon-only, icon+text, long text affecting width/wrap, and both icon tint modes. Use DS preview style: preview theme mapping palette to semantic tokens, cases via `PreviewParameterProvider`.

Add user-friendly animations where possible.
