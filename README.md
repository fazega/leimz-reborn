# Leimz Reborn

Recovered Leïmz game and rebuilt player website, maintained in one repository.

| Folder | Purpose | Runtime |
| --- | --- | --- |
| `Server` | Game server, database seed and server dependencies | Java 8, MariaDB |
| `Client` | Desktop game, assets and native libraries | Java 8, Windows |
| `Website` | Static French player portal and preview server | Node.js |

## Quick start on Windows

1. Install a **JDK 8** and Node.js. Set `JAVA_HOME`, or copy `local.settings.example.json` to `local.settings.json` and fill in machine-local settings.
2. Set up MariaDB and import `Server/database/seed.sql` into a new empty `leimz_reborn` database. See `Server/README.md`.
3. Run these commands from the repository root:

```powershell
.\Build.ps1
.\Server\Start.ps1
.\Client\Start.ps1
# In a separate terminal:
.\Website\Start.ps1
```

Demo game login: **player / leimz-local**, character **FaZeGa**. These are deliberately public development credentials, unrelated to historical accounts. The demo starts near Well Caume at (5,5).

The game port defaults to 1500; `gamePort` in local settings changes both client and server. The website uses 4173 (`PORT` overrides it). Services bind to loopback. Local settings, generated classes and logs are ignored by Git.

## Repository workflow

Use one branch per change and pull requests into `main`. Server and client share a legacy wire protocol; changes affecting both belong in the same commit or pull request. Component ownership and release cadence can be separated later without splitting Git history now.

Use labels such as `client`, `server`, `website`, `recovery` and `bug` for issues. A GitHub Project is an optional board for these issues; it does not hold the source code. No nested repositories or submodules are used.

## Recovery baseline

- Latest identified Java sources: August 2014, including the local crash, movement, NPC synchronization, collision and startup-scaling fixes.
- Map: March 2014 editor export, 40,000 tiles and 112 solid tiles. Extra distinct scenery layers are retained.
- NPC/quest records: January 2013 database. Later Avento/Ganymède quest records remain missing. There is a new-quest popup but no recovered persistent quest journal.
- Loading: original green artwork and progress bar restored.
- Website: rebuilt from the graphic charter with original assets, generated clean scenery and code-based ornamentation. See its README for provenance.

See `RECOVERY.md` for implementation details, checks and known limitations. This is a runnable historical recovery baseline, not a modernized production service. The legacy authentication/protocol and dependency stack still need modernization before internet deployment.

## Dependencies and assets

Exact historical JARs and native libraries are included to make offline builds reproducible. See `THIRD_PARTY.md`. Java sources are normalized to UTF-8. Existing runtime text/wire encoding remains ISO-8859-15 for compatibility. Original art/data files preserve their formats and names.

No project-wide open-source license is assigned: ownership and third-party asset permissions must be established before choosing one. The original historical archive and its version directories are not part of this repository.
