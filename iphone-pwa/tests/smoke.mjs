import { readFileSync } from 'node:fs';
import vm from 'node:vm';
import assert from 'node:assert/strict';

const html = readFileSync(new URL('../index.html', import.meta.url), 'utf8');
const scripts = [...html.matchAll(/<script>([\s\S]*?)<\/script>/gi)].map(m => m[1]);
assert.ok(scripts.length, 'PWA must contain its application script');
for (const [i, source] of scripts.entries()) new vm.Script(source, { filename: `inline-script-${i}.js` });

const requiredIds = ['auth','accountEmail','accountPassword','authSubmit','welcome','createBtn','joinBtn','confirmJoin','game','roundClock','invitePlayers','centerMyLocation','pauseZoneSharing','clearMap','openMap','assignTeams','startRound','pauseRound','endRound','callRoster','acceptCall','declineCall','endCall','notifyBtn','logoutBtn','leaveBtn'];
for (const id of requiredIds) assert.match(html, new RegExp(`id=["']${id}["']`), `Missing required UI control: ${id}`);

assert.match(html, /PBKDF2/);
assert.match(html, /iterations:180000/);
assert.match(html, /getUserMedia\(\{audio:true,video:false\}\)/);
assert.match(html, /serviceWorker\.register/);
assert.match(html, /navigator\.geolocation\.getCurrentPosition/);
assert.doesNotMatch(html, /watchPosition/);
assert.match(html, /Your coordinates were not sent to the room/);
assert.doesNotMatch(html, /radius:500|500 m bounded play area/);
assert.match(html, /No fixed-radius boundary is applied/);
assert.match(html, /GPS coordinates are never sent to other players/);
assert.match(html, /function assignTeams\(\)/);
assert.match(html, /function round\(action\)/);
assert.match(html, /function renderClock\(\)/);
assert.match(html, /id=[\"']zoneConsent[\"']/);
assert.match(html, /id=[\"']gameMap[\"']/);
assert.match(html, /OpenStreetMap contributors/);
assert.match(html, /function initMap\(\)/);
assert.match(html, /function drawPlayArea\(\)/);
assert.match(html, /state\.playArea/);
assert.match(html, /exact coordinates are not shared with the room/);

assert.match(html, /peer\.on\('call'/);
assert.match(html, /Notification\.requestPermission\(\)/);
console.log('PASS: JS syntax and feature-presence smoke tests.');
