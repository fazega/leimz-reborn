const assert = require('node:assert/strict');
const crypto = require('node:crypto');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const root = __dirname;
const baseline = require('./content-baseline.json');

function filesUnder(relative) {
  const result = [];
  for (const entry of fs.readdirSync(path.join(root, relative), { withFileTypes: true })) {
    const name = `${relative}/${entry.name}`;
    if (entry.isDirectory()) result.push(...filesUnder(name));
    else result.push(name);
  }
  return result;
}

test('protected content has no added or removed files', () => {
  const actual = baseline.roots.flatMap(filesUnder).concat(baseline.files).sort();
  assert.deepEqual(actual, Object.keys(baseline.sha256).sort());
});

for (const [name, expected] of Object.entries(baseline.sha256)) {
  test(`content unchanged: ${name}`, () => {
    let bytes = fs.readFileSync(path.join(root, name));
    // Git may check text out as CRLF on Windows; this is not a content change.
    if (baseline.normalizeLineEndings.includes(path.extname(name).toLowerCase())) {
      // Latin-1 is a lossless byte mapping here, not an assertion about file encoding.
      // Several archived XML files retain Windows-1252 text.
      bytes = Buffer.from(bytes.toString('latin1').replace(/\r\n/g, '\n'), 'latin1');
    }
    assert.equal(crypto.createHash('sha256').update(bytes).digest('hex'), expected);
  });
}
