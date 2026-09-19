# Server

Java entry point: `com.server.core.Main`. Source is in `src/main/java`; checked-in libraries are in `lib`. Run `../Build.ps1 Server`, then `./Start.ps1` from PowerShell.

## Database

Use MariaDB (the recovery has been tested with 10.11.11) with `sql-mode=NO_ENGINE_SUBSTITUTION` because the historical schema is not strict-mode compatible.

Create an empty database and a dedicated local user using a database administrator:

```sql
CREATE DATABASE leimz_reborn CHARACTER SET utf8mb4;
CREATE USER 'leimz_reborn'@'127.0.0.1' IDENTIFIED BY 'choose-a-local-password';
GRANT ALL PRIVILEGES ON leimz_reborn.* TO 'leimz_reborn'@'127.0.0.1';
```

Import `database/seed.sql` with the MariaDB client into that **empty** database. From the MariaDB interactive client:

```sql
USE leimz_reborn;
SOURCE C:/path/to/Leimz Reborn/Server/database/seed.sql;
```

The seed recreates tables and is for initial setup, not a migration to apply over saved progress. It includes recovered world content and one demo account (`player / leimz-local`), with historical account passwords removed.

Configure `dbUrl`, `dbUser` and `dbPassword` in root `local.settings.json`. Alternatively use `LEIMZ_DB_URL`, `LEIMZ_DB_USER` and `LEIMZ_DB_PASSWORD`. The file is ignored by Git. MariaDB must already be running when the server starts.

On the recovery workstation, local settings point to a separate `leimz_reborn` database on the existing local MariaDB instance at port 3307. Its game server uses port 1501 to avoid interrupting the older recovery session. These workstation settings are deliberately not committed.

Server output and PID are written under `logs/`. Stop the specific server process using that PID. Do not terminate unrelated Java processes.

## One character per account

The server uses account.currjoueur when valid, otherwise deterministically adopts an existing character. Legacy extra rows remain preserved but are not playable through the protocol. An account with no character receives a Groz/barbare character and the historical default stats transactionally on first login. There is no character-creation command. The server chooses identity, race and starting position from its own account record; client-supplied choices are ignored. Run ./Server/Test-Accounts.ps1 for rolled-back database integration checks.
