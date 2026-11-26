# Manhunt Challenge — GitHub-hosted iPhone PWA

This is the actual Manhunt game frontend, hosted by GitHub Pages. It no longer redirects to the previous hosted-game provider.

## Install on iPhone
1. Open the GitHub Pages URL in Safari.
2. Tap Share.
3. Choose Add to Home Screen.
4. Open Manhunt from the Home Screen.

## Multiplayer and calls
- A host creates a six-character room code.
- Other players join using that code.
- The host can assign Hunter/Hider teams and control the round state.
- Voice calls use browser microphone permission only when a player starts or accepts a call.
- Peer-to-peer connections use PeerJS signalling infrastructure; each player needs a network connection and the host must remain connected for the room to stay available.

## Location and safety
This game uses fictional team zones only. It does not request GPS permission, collect precise location, or show real-world player positions. Play only in agreed, supervised areas.

## Notifications and limitations
The app can show in-app alerts and browser notifications while supported by the browser. GitHub Pages is static hosting and cannot send reliable background push notifications by itself. A push provider/backend would be needed for that feature.

## Hosting
The GitHub Actions workflow deploys the iphone-pwa folder to:
https://selim-mostafa.github.io/Manhunt-Challenge/
