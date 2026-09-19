# Animated character candidates

Research: 19 September 2026. These are candidates, not imported assets or replacement character art.

| Pack                                                                                                                                  | Visual direction and animations                                                                    | License stated by creator                                     | Fit for Leimz                                                                                                                          |
| ------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- | ------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| [Knight, Princess and Dragon 2 — akylrum](https://opengameart.org/content/knight-princess-and-dragon-2)                               | Stylized cartoon fantasy; walk, run, two attacks, idle, death and jump; transparent PNG sequences  | CC0                                                           | First candidate to evaluate for the playful fantasy style. Confirm all required viewing directions in the download before integration. |
| [HOB — W_K_Studio](https://whiteknightstudios.itch.io/iso)                                                                            | Isometric character; eight directions; idle, walk, attack and death; 128×128 and 512×512 versions  | CC0 / unlimited commercial and private use stated on the page | Strong technical match for the eight orientations already used by Leimz; different character design from Dofus.                        |
| [Hand-Drawn Square Characters — RGS_Dev](https://rgsdev.itch.io/hand-drawn-square-characters-animated-8-directions-top-down-free-cc0) | Four characters, eight directions, 128×128; idle/walk, jump and death FX; sheets and separate PNGs | CC0                                                           | Simple, cheerful art; useful for animation prototyping, but much blockier than the desired Dofus/Wakfu aesthetic.                      |

## Integration requirements

The recovered `Joueur` renderer selects a still image for each orientation; it does not currently advance a walk cycle. Supporting an animated pack needs a small animation controller with idle/walk state, frame durations, direction mapping, and a consistent foot anchor. Keep visual frame dimensions separate from gameplay collision coordinates. Add attack/death states when those gameplay transitions are implemented.

Check actual files for complete direction coverage and consistent foot placement before choosing a pack. Preserve the creator's license alongside imported assets and update only the newly approved content-baseline entries.

Wakfu is a useful visual reference. Its game artwork is proprietary; the [Ankama terms](https://store.steampowered.com/eula/215080_eula_0) do not establish a reusable asset-pack license. The candidates above have explicit reuse terms. No additional Dofus/Wakfu files were extracted or imported.
