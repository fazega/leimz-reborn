const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const http = require("node:http");
const { spawn } = require("node:child_process");
const { once } = require("node:events");
const { contentSnapshot } = require("./content-snapshot.cjs");

const websiteRoot = path.resolve(__dirname, "..");
const baseline = require("./fixtures/content-before-cleanup.json");

test("all website text, elements, routes and gallery metadata remain unchanged", () => {
  const html = fs.readFileSync(path.join(websiteRoot, "index.html"), "utf8");
  assert.deepEqual(contentSnapshot(html), baseline.content);
});

test(
  "static preview serves files and rejects malformed or escaping paths",
  { timeout: 10000 },
  async (t) => {
    // PORT=0 lets the OS pick an unused port without disturbing the user's preview.
    const server = spawn(process.execPath, ["server.cjs"], {
      cwd: websiteRoot,
      env: { ...process.env, PORT: "0" },
      windowsHide: true,
      stdio: ["ignore", "pipe", "pipe"],
    });
    t.after(async () => {
      if (server.exitCode === null) {
        const exited = once(server, "exit");
        server.kill();
        await exited;
      }
    });
    const port = await new Promise((resolve, reject) => {
      let output = "";
      server.once("error", reject);
      server.once("exit", (code) =>
        reject(new Error(`Preview exited early: ${code}`)),
      );
      server.stderr.on("data", (data) => reject(new Error(String(data))));
      server.stdout.on("data", (data) => {
        output += data;
        const match = output.match(/http:\/\/localhost:(\d+)/);
        if (match) resolve(Number(match[1]));
      });
    });
    assert.ok(port > 0, "startup log must report the actual selected port");

    function request(url) {
      return new Promise((resolve, reject) => {
        http
          .get({ host: "127.0.0.1", port, path: url }, (response) => {
            const chunks = [];
            response.on("data", (chunk) => chunks.push(chunk));
            response.on("end", () =>
              resolve({
                status: response.statusCode,
                headers: response.headers,
                body: Buffer.concat(chunks),
              }),
            );
          })
          .on("error", reject);
      });
    }

    for (const [url, file, contentType] of [
      ["/", "index.html", "text/html; charset=utf-8"],
      ["/style.css", "style.css", "text/css; charset=utf-8"],
      ["/app.js", "app.js", "text/javascript; charset=utf-8"],
      ["/assets/button.svg", "assets/button.svg", "image/svg+xml"],
      ["/assets/logo-hd.png", "assets/logo-hd.png", "image/png"],
    ]) {
      await t.test(url, async () => {
        const response = await request(url);
        assert.equal(response.status, 200);
        assert.equal(response.headers["content-type"], contentType);
        assert.equal(response.headers["cache-control"], "no-cache");
        assert.deepEqual(
          response.body,
          fs.readFileSync(path.join(websiteRoot, file)),
        );
      });
    }
    for (const [url, status] of [
      ["/missing-file", 404],
      ["/%ZZ", 400],
      ["/%2e%2e%2fREADME.md", 403],
      ["/%2e%2e%5cREADME.md", process.platform === "win32" ? 403 : 404],
    ]) {
      await t.test(url, async () =>
        assert.equal((await request(url)).status, status),
      );
    }
  },
);
