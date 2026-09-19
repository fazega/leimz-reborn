# Codebase guide

## Components

| Component | Entry point | Responsibilities |
| --- | --- | --- |
| Client | `com.client.gamestates.Base` | Input, rendering, local gameplay state, server connection |
| Server | `com.server.core.Main` | Sessions, world data, movement approval, NPCs, quests, database access |
| MapEditor | `com.loop.Base` | Standalone editing of map XML and tile artwork |
| Website | `server.cjs`, `app.js` | Local static preview and browser navigation; no game/database integration |

Root `Build.ps1` builds the three Java components independently. `common.ps1` resolves machine-local settings. Java dependencies are currently bundled per component; there is no shared Java module yet.

## Client flow

`Base` registers Slick states: `Identification` → `ChoixPerso` → `Loading` → `Principal`. Character creation is another state. Login creates `NetworkManager`. Loading requests the map and character data before constructing the world/entity managers. `Principal` runs the update/render loop, camera, input and GUI.

Important code areas:

- `gamestates`: screen and lifecycle transitions.
- `load`: deserialization and asynchronous loading.
- `map`: tile types, map geometry and viewport selection.
- `entities`: world actors and their movement/rendering state.
- `gameplay`: character statistics, inventory, spells, quests and combat.
- `utils/gui` and `display/gui`: TWL widgets and integration.
- `network`: socket I/O, message buffering and dispatch.

These boundaries are imperfect: network handlers currently mutate gameplay/UI objects from a receive thread, `MainJoueur` handles input and movement and sends messages, and quest management creates popups. Refactoring should progressively make those boundaries real rather than simply moving files.

## Server flow

`Main` starts `Server`, which opens the listening socket, connects to MariaDB and loads world data. Accepted sockets become clients; `Calculator` dispatches message codes to classes in `core/functions`. Those handlers currently combine validation, SQL, model updates and reply serialization.

The intended next boundary is **command handler → gameplay service → database repository**, with a separate response encoder. Preserve the current wire format while introducing these layers.

## Terms and coordinates

| Existing term | Meaning |
| --- | --- |
| `Joueur` | Player entity in the world |
| `MainJoueur` | Locally controlled player |
| `Personnage` | Character gameplay data: statistics, inventory, spells, quests |
| `PNJ` | Non-player character (NPC) |
| `Quete`, `Objectif` | Quest and objective |
| `grille`, `calque` | Tile grid and layer |
| `pos` | Usually tile/grid indices; confirm per class |
| `pos_real` | World-space pixel coordinates |
| `pos_screen` | Screen-space coordinates after camera positioning |
| `barycentre` | Tile centre |

Tiles use 80×40 artwork with staggered columns spaced 40 pixels apart; odd columns have a 20-pixel vertical offset. Coordinate implementations are duplicated across components and differ at boundaries. Define edge behavior with tests before replacing them with shared code. Mutable vectors must not be shared between entity positions and map geometry.

## Legacy protocol orientation

Messages are newline-delimited, semicolon-separated text, currently ISO-8859-15 on the Java connection. `NetworkManager.receiveFromServerPossible` removes the first field and buffers the remaining payload under that message code. Dispatchers that expect the code must restore it—the missing `afm` prefix caused one recovery bug.

Selected commands, not a complete protocol specification:

| Code | Existing purpose |
| --- | --- |
| `c` | Login |
| `ci` | Character list/information |
| `cp` | Character creation |
| `lo` | Map, player and entity loading |
| `afm;key` | Keyboard movement approval request |
| `s;pos` | Player position/orientation synchronization |
| `cea` | Nearby entity discovery |
| `res` | State refresh |
| `pd` | NPC dialogue |
| `que` | Quest updates |
| `sa` | Chat |
| `fi`, `a` | Combat and attacks |

There is no consistent escaping or strongly typed schema. Do not rename these strings as part of code formatting. Typed internal messages and validation can be introduced while preserving their serialized representation.
