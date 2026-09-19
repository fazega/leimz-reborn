# Bundled historical dependencies

Most binaries were recovered from the original Leïmz archive. The Client's LWJGL core, utilities and Windows/Linux native libraries were updated to 2.9.3 to support native window resizing; MapEditor retains its recovered versions.

| Component | Recovered dependency                                                                          |
| --------- | --------------------------------------------------------------------------------------------- |
| Client    | Slick build 274, LWJGL 2.9.3 core/utilities and matching Windows/Linux native libraries       |
| Client    | TWL, JInput, JDOM, XPP3 1.1.4c                                                                |
| Client    | Log4j 1.2.16, MySQL Connector/J 5.1.18                                                        |
| Server    | MySQL Connector/J 5.1.18                                                                      |
| MapEditor | Archived Slick/LWJGL, TWL, JInput, JDOM, XPP3 and supporting libraries; see dependency hashes |

Some recovered JAR names do not encode an exact version. Their SHA-256 hashes are recorded in `dependency-manifest.json`; do not silently replace them. Embedded JAR license/notice files remain intact. No claim is made that all required upstream notices or artwork permissions have been recovered.

Game artwork, music, fonts and website source art come from the historical archive. Website-specific generated artwork and new vector ornaments are described in `Website/README.md`. Modernizing dependencies and reviewing asset redistribution permissions are separate follow-up tasks.
