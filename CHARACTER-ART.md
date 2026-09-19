# Animated character candidates

## Current playable character

The five recovered race definitions all use the same `perso2` artwork. They now use a cleaned, transparent eight-direction atlas at `Client/data/Images/Persos/reborn/adventurer.png`, generated with the built-in image-generation tool from the original front, back and right-profile sprites. The original files remain intact. Only the new atlas was added to the protected content baseline.

The runtime now uses only the original atlas's neutral directional poses. `CharacterSprites` crops transparent margins and normalizes the body to 76 pixels; right-facing rear diagonals use the original row 3, and left-facing directions mirror their right-facing counterparts. `WalkPose` articulates the two legs continuously at the hips, knees and ankles with opposite phases, projected stride and foot lift. The torso remains anchored. A full stride lasts 1120 ms, with short start/stop blending; brief network pauses retain phase rather than restarting the same pose. The camera centres the fixed-height body rather than a varying sprite canvas.

The generated `adventurer-walk.png` is retained for provenance but is no longer rendered: its side and diagonal poses did not produce a convincing alternating stride. No artwork or gameplay content was deleted. `Client/Test-Animation.ps1` verifies that rendered foot pixels change in all eight directions while upper-body pixels remain stationary. Timing tests cover delayed movement replies, and camera tests cover centred world-to-screen coordinates and resizing.
The atlas is derived from the existing character, not an independently licensed replacement for the historical artwork. The external packs below remain alternatives.

### Generation prompt

Create a production sprite atlas cleaning up and animating the SAME existing game character in references: slim young male fantasy adventurer, tousled brown hair, yellow sleeveless tunic, yellow forearm wraps, dark teal trousers, brown boots. Preserve recognizable costume, hair, proportions and warm hand-painted Dofus-era isometric aesthetic. Remove ALL original grey/beige background; genuinely transparent alpha background, no checkerboard, no labels or grid lines, no ground shadow. EXACT atlas layout: 4 columns by 8 rows of equally sized square cells, total image 1024 pixels wide by 2048 pixels high, each cell 256x256. Each cell holds exactly one full body character, feet centered at x=128 y=224 relative to cell, top of head approximately y=32. No weapons or additional accessories. Rows directions in exact order: row1 faces south/front, row2 southeast/front-right, row3 east/right profile, row4 northeast/back-right, row5 north/back, row6 northwest/back-left, row7 west/left profile, row8 southwest/front-left. Every row: column1 relaxed neutral idle, column2 walking left foot forward/right arm forward, column3 passing mid-stride neutral, column4 walking right foot forward/left arm forward. Both feet whole and fully visible in every cell; same consistent body size and identity throughout. Crisp clean small-game-sprite readable silhouettes, smooth transparent edges, no background scenery, no text. Correct back views especially rows4-6. This atlas will be sliced automatically into 32 equal cells; meticulous alignment is essential.

The tool's returned dimensions determine cell size; the renderer does not assume the requested pixel dimensions.

## External alternatives

Research: 19 September 2026. These are candidates, not imported assets or replacement character art.

| Pack                                                                                                                                  | Visual direction and animations                                                                    | License stated by creator                                     | Fit for Leimz                                                                                                                          |
| ------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- | ------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| [Knight, Princess and Dragon 2 — akylrum](https://opengameart.org/content/knight-princess-and-dragon-2)                               | Stylized cartoon fantasy; walk, run, two attacks, idle, death and jump; transparent PNG sequences  | CC0                                                           | First candidate to evaluate for the playful fantasy style. Confirm all required viewing directions in the download before integration. |
| [HOB — W_K_Studio](https://whiteknightstudios.itch.io/iso)                                                                            | Isometric character; eight directions; idle, walk, attack and death; 128×128 and 512×512 versions  | CC0 / unlimited commercial and private use stated on the page | Strong technical match for the eight orientations already used by Leimz; different character design from Dofus.                        |
| [Hand-Drawn Square Characters — RGS_Dev](https://rgsdev.itch.io/hand-drawn-square-characters-animated-8-directions-top-down-free-cc0) | Four characters, eight directions, 128×128; idle/walk, jump and death FX; sheets and separate PNGs | CC0                                                           | Simple, cheerful art; useful for animation prototyping, but much blockier than the desired Dofus/Wakfu aesthetic.                      |

## Integration requirements

`Joueur` now uses `CharacterMotion` and `CharacterSprites` for the cleaned playable character. Other packs would need their own frame definitions, timing, direction mapping and foot anchors. Keep visual frame dimensions separate from gameplay collision coordinates. Add attack/death states when those gameplay transitions are implemented.

Check actual files for complete direction coverage and consistent foot placement before choosing a pack. Preserve the creator's license alongside imported assets and update only the newly approved content-baseline entries.

Wakfu is a useful visual reference. Its game artwork is proprietary; the [Ankama terms](https://store.steampowered.com/eula/215080_eula_0) do not establish a reusable asset-pack license. The candidates above have explicit reuse terms. No additional Dofus/Wakfu files were extracted or imported.

## Revised walking atlas generation

Generated with the built-in image-generation tool using the previous atlas only as a character reference. Saved in Client/data/Images/Persos/reborn/adventurer-walk.png. The first eight-direction draft was rejected because it mixed camera directions.

Prompt: Redraw the reference character as a clean transparent walk animation sprite sheet, exactly eight columns and five rows: south, southeast, east profile, northeast and north. Eight chronological poses per row: left contact, down, passing, up, right contact, down, passing, up. Preserve brown ponytail, yellow sleeveless tunic, teal trousers and brown boots; consistent body scale and ground anchor, opposite arm/leg swing, minimal torso bob, no text, shadows or gridlines. West-facing directions are mirrored in the renderer.

The previous frame-based renderer required explicit row bounds for the uneven 1586×992 sheet. That approach has been superseded by the continuous gait described above. Client/Test-Animation.ps1 reproduces and verifies the current rendered poses.
