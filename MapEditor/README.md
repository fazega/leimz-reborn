# Leimz Map Editor

Recovered standalone map editor, using Java 8, Slick2D, LWJGL 2 and TWL. No game server or database connection is required.

From the repository root:

```powershell
.\Build.ps1 -Component MapEditor
.\MapEditor\Start.ps1
```

The launcher uses `javaHome` in the root `local.settings.json` (or `JAVA_HOME`) and sets the working directory and bundled Windows native library path. `Start.ps1 -Build` recompiles first.

## Editing maps

The editor starts with a new map. Choose **Fichier > Ouvrir ...**, enter `data/Maps/map2.xml`, then select **Ouvrir la map** to load the recovered 2014 scenery. Tile types are available in the palette; the original tool controls support placing, selecting, deleting and moving around the map. Space clears the selection.

**Enregistrer currently writes to `data/Maps/map2.xml` regardless of which file was opened.** Copy that file before experimenting. This is inherited behavior, documented rather than silently changed during recovery. Editor output is XML; it does not automatically update the running server's SQL world. Tile collisions and graphics are defined separately in `data/Maps/types_tiles.xml`; reconciling these definitions with the game remains a future integration task.

## Layout and provenance

- `src/main/java`: original editor source, converted from Windows-1252 to UTF-8 without changing game logic. Entry point: `com.loop.Base`; main UI/state: `com.loop.Principal`; tile and map data: `com.map`; widgets: `com.utils.gui`.
- `data`: copied runtime resources and archived XML maps.
- `lib`: original Java libraries and Windows native libraries.
- `build`, `logs`: generated, ignored files.

Source: `Versions/Leïmz Map Editor` in the original LEIMZ archive. Eleven absolute paths in the tile catalogue were changed to relative `data/tiles/recovered/...` paths. Their PNG files were recovered from `Versions/Leimz-CLIENT/data/Images/tiles`. The original archive was not modified. Compiled classes, packaged copies, Eclipse metadata, old backups and editable artwork files are excluded.

This is a recovery baseline, not a rewritten editor. The original 1353×700 layout and TWL theme warnings remain; malformed input handling, undo/redo and safer save controls need separate improvements.

## Verification

`Test-Content.ps1` verifies archived asset/map hashes and reverses the eleven path substitutions in memory to prove the tile catalogue has no other changes. `data-provenance.json` records archive-relative source paths and hashes.

`Test-Recovery.ps1` needs a graphics-capable Windows desktop and Java 8. It loads the recovered 200×200 map, verifies every referenced tile type, saves to `build/map-roundtrip.xml`, compares all tile coordinates and ordered layers, reloads the result and renders 120 frames. No supplied map is overwritten. The legacy loader can take several minutes. Slick may report an unsupported PNG format before its fallback reader succeeds; this and TWL theme warnings are inherited library diagnostics.
