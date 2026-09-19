# Client

Java entry point: `com.client.gamestates.Base`. Run `../Build.ps1 Client` and `./Start.ps1` in PowerShell. The launcher sets the working directory, native-library path and matching server port.

`src/main/java` contains the recovered source; `data` contains runtime artwork, audio, themes and XML definitions; `lib` contains the archived Java/native dependencies. Windows with JDK 8 is the tested platform. Linux libraries are preserved but Linux execution has not been verified.

Demo login: **player / leimz-local** → **FaZeGa**. NPC **Well Caume** is at (5,5); select him and choose **Parler**. Inventory: **I**; character statistics: **P**; menu: **Escape**. There is no recovered quest-log shortcut.

The launcher preserves the legacy 1000×680 game surface. Font/DPI initialization now happens before window creation. Collisions use solid map tiles, rather than pixel-perfect sprite shapes.

The optional `Test-Recovery.ps1` integration check logs into the demo database, tests a barrel collision and verifies NPC discovery after synchronizing position. Run only against the demo seed: it temporarily changes FaZeGa's saved position. It does not test all quest, combat or click-pathfinding behavior.
