const fs = require("node:fs");
const path = require("node:path");

// Publish only browser resources; preview tooling and tests stay local.
function buildWebsite(websiteRoot = __dirname) {
  const output = path.resolve(websiteRoot, "dist");
  const publicFiles = ["index.html", "app.js", "style.css", "assets"];
  for (const name of publicFiles) {
    fs.accessSync(path.join(websiteRoot, name));
  }
  if (fs.existsSync(output) && fs.lstatSync(output).isSymbolicLink()) {
    throw new Error("Refusing to replace a linked dist directory");
  }
  fs.rmSync(output, { recursive: true, force: true });
  fs.mkdirSync(output);
  for (const name of publicFiles) {
    fs.cpSync(path.join(websiteRoot, name), path.join(output, name), {
      recursive: true,
      filter(source) {
        if (fs.lstatSync(source).isSymbolicLink()) {
          throw new Error(`Public resources must not contain links: ${source}`);
        }
        return true;
      },
    });
  }
  return output;
}

if (require.main === module) {
  console.log(`Website built in ${buildWebsite()}`);
}

module.exports = { buildWebsite };
