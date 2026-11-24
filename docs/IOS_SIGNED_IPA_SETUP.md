# Creating an installable iOS IPA

The `iOS signed installable IPA` workflow exports an Ad Hoc signed IPA. It requires an Apple signing certificate and a provisioning profile that includes the target iPhone's UDID.

## Required GitHub Actions secrets

In the repository, open **Settings → Secrets and variables → Actions → New repository secret** and add:

- `IOS_CERTIFICATE_BASE64`: Base64 text of the Apple Distribution `.p12` certificate.
- `IOS_CERTIFICATE_PASSWORD`: Password used when exporting that `.p12`.
- `IOS_PROVISIONING_PROFILE_BASE64`: Base64 text of the Ad Hoc `.mobileprovision` profile.
- `IOS_TEAM_ID`: Apple Developer Team ID (the profile's application identifier prefix must match).

The certificate and profile must belong to the same Apple Developer team. The profile must match bundle ID `com.selimallamie.manhunt`, and must include the iPhone UDID on which the IPA will be installed. An Ad Hoc profile is for registered devices; it is not a public App Store distribution.

Do not commit certificates, profiles, passwords, or private keys to the repository, and do not send them in chat. Use GitHub Actions secrets only.

## Run it

After the four secrets are configured, open **Actions → iOS signed installable IPA → Run workflow**. Download the `Manhunt-iOS-installable` artifact from the successful run.

If the signing materials are not configured, the workflow intentionally stops with a clear missing-secret error instead of producing a misleading unsigned IPA.
