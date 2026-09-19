# Cleanup plan

The goal is understandable responsibilities and predictable state, not a large cosmetic rewrite. Keep each phase reviewable and preserve the playable recovery as a baseline.

## 1. Readability and navigation

Current pass: integrate MapEditor, document architecture/terminology, expand minified website source, and keep build/launch conventions consistent. Preserve historical names where they convey domain meaning. French mojibake corrections are deferred because displayed text is protected content in this pass.

Next: configure a pinned Java formatter and apply it in a dedicated mechanical commit; remove obsolete commented-out code only after checking whether it records unrecovered functionality. Prefer short comments about intent and invariants over comments restating syntax.

## 2. A faster safety net

Keep `Client/Test-Recovery.ps1` as an end-to-end check. Add small headless tests for tile/world conversion, boundary points, solid tiles, protocol parsing, malformed messages and movement acknowledgements. Separate test scenarios from the current reflection/frame-count harness. Tests should capture intended behavior, including existing edge cases, before extracting shared rules.

## 3. Extract database operations

Start with login and character creation. `ConnectFunction` concatenates SQL and has incomplete argument validation; `CreationPersoFunction` performs related inserts without a transaction. Add validated requests, prepared statements, resource cleanup and transactions. Extract player/map/quest repositories from the 595-line `LoadFunction`, and quest operations from `PnjDialogFunction`.

Password hashing requires an explicit schema/data migration and tests. Do not disguise it as a formatting change. Report database startup failures instead of exiting with a success code.

## 4. Make network/thread ownership explicit

Replace the shared unsynchronized mailbox in `NetworkManager` with one socket reader and a queue of parsed events. Apply gameplay/UI changes on the game thread. Define shutdown, disconnect and timeout behavior; stop swallowing exceptions. On the server, make the command registry immutable and establish who owns each mutable client list.

## 5. Split gameplay controllers

Extract input and movement controllers from `MainJoueur`; separate quest state from popup creation in `QuetesManager`. Keep rendering, world state, input and network serialization independently understandable. Replace the duplicated busy-spin `Chrono` timer implementations with elapsed-time calculations using a monotonic clock.

## 6. Consolidate shared rules and modernize the build

Once tests define coordinate and message semantics, consider a narrowly scoped shared module for protocol types and geometry. Avoid a miscellaneous `Common` dumping ground. A Gradle or Maven multi-module build can replace custom compiler scripts when dependency resolution and the historical libraries have been verified. Upgrade Java, LWJGL/Slick and database libraries as separate projects with explicit compatibility tests.

## Known behavior to investigate separately

`Client/load/LoadJoueur` appears to reuse race request tags while loading class spells/statistics. Verify the protocol and data before changing it. Quest completeness, multiplayer behavior and click-pathfinding require more coverage than the current local smoke test.
