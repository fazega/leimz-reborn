const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");
const { buildWebsite } = require("../build.cjs");

test("deployment contains only public resources, byte-for-byte, with no stale files", (t) => {
  const temporary = fs.mkdtempSync(path.join(os.tmpdir(), "leimz-deployment-"));
  t.after(() => fs.rmSync(temporary, { recursive: true, force: true }));
  const source = path.resolve(__dirname, "..");
  for (const name of ["index.html", "app.js", "style.css", "assets"]) {
    fs.cpSync(path.join(source, name), path.join(temporary, name), {
      recursive: true,
    });
  }
  for (const name of ["server.cjs", "local.settings.json", "README.md"]) {
    fs.writeFileSync(path.join(temporary, name), "must not be published");
  }
  fs.mkdirSync(path.join(temporary, "tests"));
  fs.writeFileSync(path.join(temporary, "tests", "private.txt"), "private");
  fs.mkdirSync(path.join(temporary, "dist"));
  fs.writeFileSync(path.join(temporary, "dist", "stale.txt"), "stale");

  const output = buildWebsite(temporary);
  assert.deepEqual(fs.readdirSync(output).sort(), [
    "app.js",
    "assets",
    "index.html",
    "style.css",
  ]);
  function compare(relative = "") {
    for (const name of fs.readdirSync(path.join(output, relative))) {
      const file = path.join(relative, name);
      if (fs.statSync(path.join(output, file)).isDirectory()) {
        compare(file);
      } else {
        assert.deepEqual(
          fs.readFileSync(path.join(output, file)),
          fs.readFileSync(path.join(source, file)),
          `${file} must remain unchanged`,
        );
      }
    }
  }
  compare();
});
