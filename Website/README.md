# Leïmz — Le carnet des aventuriers

Player-facing French fansite preview at http://localhost:4173.

## Run locally

From this folder run:

    node server.cjs

The dependency-free Node server binds to 127.0.0.1 on port 4173. No build step is needed.

## Design and features

The compact 760px desktop layout follows the original charter: moon and logo, beveled tabs with cyan/orange ornaments, short screenshot banner, inset article/sidebar and tilted community plaque, then an isometric terrain feature. The pages adapt to mobile.

Five working navigation pages: Accueil, Le jeu, Médias, Les forums, F.A.Q. The gallery offers game/art filters, enlarged images, previous/next controls, arrow keys and Escape to close. FAQ disclosures work. Forum content accurately explains that discussions and accounts are not open; no fake live service is implied.

## Asset provenance

Original files are preserved unchanged.

- `assets/night-background.png`: clean landscape generated in image-edit mode by the parent task, using the original graphic charter as reference and removing all UI, logo and text. The supplied output preserves moon, clouds and forest atmosphere. It is the sole decorative page backdrop.
- `assets/logo-hd.png`: 1015×564 transparent native layer `Logo (BG Noir)` exported from `Leimz - Logo Final.psd` inside `site leimz/leimz (1).zip`.
- `assets/navigation.svg`, `assets/corner.svg`: newly authored vector ornaments inspired by the charter, with no baked text.
- `assets/grass.png`, `assets/stone.png`: original tile artwork from `Versions/Versions très anciennes/Versions/test 0.5/data/Images/tiles/Tiles photoshop`. The little island composes these sprites with CSS positioning.
- `assets/esplanade.png`, `quest.png`, `character.png`: original Screens captures.
- `assets/forgeron.jpg`, `troll.jpg`: original Artworks images.
- Lore is adapted from `Scénario/scénario.txt`, which contains development notes.

The old PHP website and its PSDs were inspected. This front-end rebuild does not run historical PHP or connect to an old account database. The original charter screenshot and baked navigation labels are not referenced by the live website.

## Scope

Local prototype only. No public forum, registration, live game status, game download or account integration. The restored Java game is launched separately.

## Latest visual refinement

Buttons use a sculpted iron plaque (`assets/button.svg`) with indented sides and layered bevels. Frames and inset panels have gently asymmetric corners. The night scenery is scaled to 1080px on desktop and 850px on mobile, with soft side masks and a bounded lower gradient into the exact page background color (`#020505`). Checked at 1280px, 1920px and 390px viewport widths, including the footer and mobile gallery; no horizontal overflow at mobile size.
