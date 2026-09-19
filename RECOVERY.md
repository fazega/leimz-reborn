# Recovery baseline and outstanding work

This repository starts from the latest recovered August 2014 Java source, not one of the older packaged client JARs. Original archives remain outside the repository.

## Compatibility changes carried forward

- Local configurable database connection and loopback network endpoints.
- Correct character class/Y-coordinate assignment, a longer network timeout and a non-busy server loop.
- Empty map groups supported; archived multi-layer map imported, repeated identical layers collapsed, distinct layers retained.
- Placeholder `none` overlays skipped; original green loading background/progress bar restored.
- Copied position vectors prevent movement from modifying map tile coordinates. World tile lookup is independent of the camera viewport.
- Movement approval tags are delivered correctly; accepted keyboard positions are synchronized to the server so NPC discovery follows the player.
- Client tile types retain collision flags; server loads all 112 solid tiles. Solid destination tiles are rejected for keyboard movement. Client pathfinding reads the same tile flags.
- Java AWT/font initialization runs before native window creation to avoid late scaling changes.

## Known limits

The newer map and old NPC/quest database are from different dates. Later quest records are missing. Combat, complete quest flows, multiplayer synchronization and all pathfinding cases have not been exhaustively verified. Collision is tile-based. Movement messaging and database writes remain legacy code and need architectural work.

Authentication is legacy plaintext, with direct SQL construction in historical handlers. The bundled dependencies are old. Keep this recovery local until authentication, SQL handling, protocol validation and dependencies have been modernized. The seed contains only development credentials; historical passwords and machine-local database secrets are excluded.

## Validation

The pre-extraction recovery passed rendering, 40,000 tile-centre lookups, movement in eight directions, a barrel-boundary check and NPC discovery after movement synchronization. Extraction is checked by rebuilding both components from UTF-8 source, syntax-checking the website and running the included demo integration check against the separate seed database.

The window check confirmed a constant 1000×680 game surface across states; physical placement across different monitors was not instrumented.

## Cleanup and preservation checks

MapEditor is now a separate component. Its test loads the recovered 200×200 map, saves only to an ignored build file, verifies all 40,000 tile coordinates/layers, reloads it and renders 120 frames. Archive hashes protect 185 editor resources; only eleven documented absolute image paths were made relative.

Fast tests protect 518 content files, compare website HTML text/attributes against the original commit, exercise the preview server and check 40,000 world-coordinate lookups plus tile boundaries/collision layers without opening a game window.

Repeated integration testing exposed an existing movement reply race: a late approval could apply a newer blocked candidate. Keyboard movement now keeps only one request in flight until its approval/rejection arrives. A burst-input barrel test covers this fix. This changes movement synchronization, not map content, assets, text or quests. General network-thread ownership remains a planned refactor.
