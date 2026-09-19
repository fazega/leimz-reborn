# Publish to Cloudflare Pages

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
