import { readFileSync } from 'node:fs';
import vm from 'node:vm';
import assert from 'node:assert/strict';

const html = readFileSync(new URL('../index.html', import.meta.url), 'utf8');
const scripts = [...html.matchAll(/<script>([\\s\\S]*?)<\\/script>/gi)].map(m => m[1]);
assert.ok(scripts.length, 'PWA must contain its application script');
for (const [i, source] of scripts.entries()) {
  new vm.Script(source, { filename: `inline-script-${i}.js` });
}
const requiredIds = [
  'auth','accountEmail','accountPassword','authSubmit','welcome','createBtn',
  'joinBtn','confirmJoin','game','roundClock','assignTeams','startRound',
  'pauseRound','endRound','callRoster','acceptCall','declineCall','endCall',
  'notifyBtn','logoutBtn','leaveBtn'
];
for (const id of requiredIds) {
  assert.match(html, new RegExp(`id=["']${id}["']`), `Missing required UI control: ${id}`);
}
assert.match(html, /PBKDF2/, 'Local password hashing must remain enabled');
assert.match(html, /iterations:180000/, 'Password hashing work factor changed unexpectedly');
assert.match(html, /getUserMedia\\(\\{audio:true,video:false\\}\\)/, 'Microphone must be requested only by call actions');
assert.match(html, /serviceWorker\\.register/, 'PWA service worker registration is missing');
assert.doesNotMatch(html, /navigator\\.geolocation|watchPosition|getCurrentPosition/, 'The safe-zone build must not collect GPS');
assert.match(html, /fictional game zones only/, 'The UI must explain that zones are fictional');
console.log('PASS: inline JavaScript syntax, authentication UI, game controls, call permission flow, PWA registration, and no-GPS safety checks.');
