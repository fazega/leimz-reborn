const assert = require("node:assert/strict");

// This site's static HTML contains no inline scripts, styles or attribute names
// with namespaces. Keep every element and attribute; ignore formatting whitespace.
function contentSnapshot(html) {
  const tokens = html.match(/<!--[^]*?-->|<[^>]+>|[^<]+/g) || [];
  return tokens.flatMap((token) => {
    if (token.startsWith("<!--") || /^<!doctype/i.test(token)) return [];
    if (!token.startsWith("<")) {
      const text = token.replace(/\s+/g, " ").trim();
      return text ? [{ text }] : [];
    }
    const endTag = token.match(/^<\/([\w-]+)\s*>$/);
    if (endTag) return [{ close: endTag[1] }];
    const element = token.match(/^<([\w-]+)([^]*?)\/?\s*>$/);
    assert.ok(element, `Unsupported HTML token: ${token}`);
    const attributes = {};
    const pattern = /([^\s=/>]+)(?:\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s>]+)))?/g;
    for (const attribute of element[2].matchAll(pattern)) {
      attributes[attribute[1]] =
        attribute[2] ?? attribute[3] ?? attribute[4] ?? true;
    }
    return [{ open: element[1], attributes }];
  });
}

module.exports = { contentSnapshot };
