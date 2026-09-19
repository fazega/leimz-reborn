const http = require("node:http");
const fs = require("node:fs");
const path = require("node:path");

const websiteRoot = __dirname;
const port = Number(process.env.PORT || 4173);
const contentTypes = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".svg": "image/svg+xml",
  ".png": "image/png",
  ".jpg": "image/jpeg",
};

function serveFile(filePath, response) {
  fs.readFile(filePath, (error, data) => {
    if (error) {
      response.writeHead(404).end("Not found");
      return;
    }
    response.writeHead(200, {
      "Content-Type":
        contentTypes[path.extname(filePath)] || "application/octet-stream",
      "Cache-Control": "no-cache",
    });
    response.end(data);
  });
}

function handleRequest(request, response) {
  let filePath;
  try {
    const pathname = new URL(request.url, "http://localhost").pathname;
    filePath = path.resolve(websiteRoot, "." + decodeURIComponent(pathname));
  } catch {
    response.writeHead(400).end();
    return;
  }

  if (filePath === websiteRoot) filePath = path.join(websiteRoot, "index.html");
  // Only serve files inside this website directory.
  if (!filePath.startsWith(websiteRoot + path.sep)) {
    response.writeHead(403).end();
    return;
  }
  serveFile(filePath, response);
}

const server = http.createServer(handleRequest);
server.listen(port, "127.0.0.1", () => {
  console.log(`Leimz fansite: http://localhost:${server.address().port}`);
});
