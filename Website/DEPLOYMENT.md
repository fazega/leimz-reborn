# Publish to Cloudflare

## Workers deployment (current setup)

Connect `fazega/leimz-reborn`, branch `main`, with the repository root as the
root directory. Use build command `node Website/build.cjs` and deploy command
`npx wrangler deploy`. The root `wrangler.jsonc` defines the Worker name,
compatibility date and public assets directory. The existing command
`npx wrangler deploy --assets ./Website/dist --name leimz-reborn` also works.

Deploy the latest commit, then open the Worker's **Settings → Domains & Routes →
Add → Custom domain** and enter `leimz.com`. Approve Cloudflare's DNS changes.

The compatibility date is pinned to 2026-09-18; it selects platform behavior,
not the date the website was built. A failed historical deployment must be
retried with the latest commit to include this configuration.

## Alternative: Pages deployment

The website is static. `node build.cjs` creates `dist/` containing only
`index.html`, `app.js`, `style.css`, and `assets/`. No Node server is needed in
production. Tests, documentation, local scripts and repository settings are not
published. The build preserves every public file byte-for-byte.

## First deployment

1. In your Cloudflare account, open **Workers & Pages → Create application →
   Pages → Import an existing Git repository**.
2. Connect GitHub and select **fazega/leimz-reborn**, then **Begin setup**.
3. Use these settings:

   | Setting                | Value                               |
   | ---------------------- | ----------------------------------- |
   | Project name           | `leimz` (or another available name) |
   | Production branch      | `main`                              |
   | Framework preset       | None                                |
   | Root directory         | `Website`                           |
   | Build command          | `node build.cjs`                    |
   | Build output directory | `dist`                              |

4. Save and deploy. Open the generated `pages.dev` URL to check the site.
5. In the Pages project, open **Custom domains → Set up a custom domain** and
   enter **leimz.com**. Confirm the DNS changes Cloudflare proposes.
6. Wait for the domain and HTTPS certificate to become active, then visit
   `https://leimz.com`. Add `www.leimz.com` through the same custom-domain flow
   if you want that address too.

The domain already uses Cloudflare, so no registrar transfer or nameserver change
is required. Add domains through Pages before manually changing DNS.

Git integration automatically publishes future pushes to `main`; it does not
wait for unrelated GitHub Actions checks. A deployment job dependent on CI can
replace this flow later if publication must require all tests to pass.

## Local checks

Run `npm test` and `npm run build` from `Website/`. The generated `dist/` is a
build artifact and should not be committed. Hosting the website does not host
the multiplayer Java server.

References: [Cloudflare static HTML deployment](https://developers.cloudflare.com/pages/framework-guides/deploy-anything/)
and [custom domains](https://developers.cloudflare.com/pages/configuration/custom-domains/).
