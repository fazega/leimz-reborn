# Client

Java entry point: `com.client.gamestates.Base`. Run `../Build.ps1 Client` and `./Start.ps1` in PowerShell. The launcher sets the working directory, native-library path and matching server port.

`src/main/java` contains the recovered source; `data` contains runtime artwork, audio, themes and XML definitions; `lib` contains the archived Java/native dependencies. Windows with JDK 8 is the tested platform. Linux libraries are preserved but Linux execution has not been verified.

Demo login: **player / leimz-local** → **FaZeGa**. NPC **Well Caume** is at (5,5); select him and choose **Parler**. Inventory: **I**; character statistics: **P**; menu: **Escape**. There is no recovered quest-log shortcut.

The launcher opens a resizable 1280×800 window. Font/DPI initialization now happens before window creation. Collisions use solid map tiles, rather than pixel-perfect sprite shapes.

The optional `Test-Recovery.ps1` integration check logs into the demo database, tests a barrel collision and verifies NPC discovery after synchronizing position. Run only against the demo seed: it temporarily changes FaZeGa's saved position. It does not test all quest, combat or click-pathfinding behavior.

# Loading and world perimeter

The window opens at 1280×800, reduced to fit smaller desktops, and can be resized or maximized. The map camera, GUI viewport and mouse coordinates follow the new dimensions without reconnecting. Chat stays near the bottom-left. Clicking outside its input releases keyboard focus and preserves any unfinished message.

Playable characters use a cleaned transparent atlas with eight-direction walking and subtle idle breathing. Original images remain available; see [character art and generation provenance](../CHARACTER-ART.md).

World loading consumes queued network packets immediately and enters gameplay as soon as map and player loading finish. The illustrated screen remains during actual work; there is no minimum display duration. `WORLD_LOAD_MS` in the client log measures this phase.

The outermost map tiles are solid on both client and server. A procedural mossy rock field continues beyond the map to cover the camera view at every edge. This is a runtime border; the historical map XML, database seed, NPCs and interior scenery remain unchanged. The MapEditor still edits the original map data.

Run `./Client/Test-Recovery.ps1 -Borders` from the repository root with the demo server running to capture all four corners and four edge midpoints under `Client/build/tests/border-*.png`. This includes the normal gameplay smoke checks. Animated character candidates and renderer requirements are documented in [CHARACTER-ART.md](../CHARACTER-ART.md).

## Direct login and local testing

Login opens the account's single character directly; there are no character selection or creation screens. The local login form offers **Connexion rapide**, which signs in with the demo account without typing credentials. The button is only offered for a loopback server and still uses normal server authentication.

Run Client/Test-Recovery.ps1 -QuickConnect to exercise the quick-connect callback and full world entry, or omit the switch to test entered credentials. Client/Test-Animation.ps1 renders a contact sheet of the actual runtime sprites for visual inspection.
