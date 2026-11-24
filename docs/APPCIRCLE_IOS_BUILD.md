# Appcircle iOS build guide

This repository contains an Xcode iOS project at `Manhunt-iOS/Manhunt.xcodeproj`.

## Appcircle profile

Create a new **iOS** build profile and connect it to this GitHub repository:

- Repository: `Selim-Mostafa/Manhunt-Challenge`
- Project: `Manhunt-iOS/Manhunt.xcodeproj`
- Scheme: `Manhunt`
- Bundle identifier: `com.selimallamie.manhunt`
- Deployment target: iOS 16.0
- Shared scheme: `Manhunt-iOS/Manhunt.xcodeproj/xcshareddata/xcschemes/Manhunt.xcscheme`

In Appcircle, use **Configurations → New → Config → Autofill** and verify that the project path and shared scheme above are selected. Choose an installed stable Xcode version compatible with the project. The default iOS workflow should use the device archive/export build step.

## Signing and output

- An unsigned build can be used to check whether the project compiles, but Appcircle documents that it produces an `.xcarchive`, not an installable `.ipa`.
- To export an installable IPA, configure a valid Apple signing certificate and provisioning profile for `com.selimallamie.manhunt` in Appcircle's Signing settings. App Store/TestFlight distribution and direct device testing use different provisioning choices.
- Do not put Apple passwords, private keys, certificates, provisioning profiles, or API keys in this repository. Upload signing assets only through Appcircle's protected Signing Identities / configuration interface.
- After saving the configuration, select the connected branch and start the iOS build. Download the IPA from the successful build's artifacts.

## Current project scope

The current iOS target is a SwiftUI shell containing a WKWebView that loads the hosted Manhunt website. Appcircle can compile this Xcode project, but building it does not implement native game mechanics, offline features, live location, or native calling. Those require separate product implementation and testing.
