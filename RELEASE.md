# Weather Dashboard - Release Process

## Overview

This document describes the release process for the Weather Dashboard Android application.

## Prerequisites

- GitHub repository access
- Git tag permissions
- Keystore file (for signed releases - optional but recommended)
- Android SDK 34

## Release Flow

### Automated Release (via Git Tag)

1. **Update Version Numbers**
   ```bash
   # Update versionName in app/build.gradle (e.g., versionName "1.0.0")
   # Update versionCode in app/build.gradle (e.g., versionCode 1)
   ```

2. **Update CHANGELOG.md**
   - Add release notes for the new version
   - Include new features, bug fixes, and breaking changes

3. **Commit Changes**
   ```bash
   git add CHANGELOG.md app/build.gradle
   git commit -m "Release v1.0.0"
   ```

4. **Push to Main Branch**
   ```bash
   git push origin main
   ```

5. **Create and Push Git Tag**
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0"
   git push origin v1.0.0
   ```

6. **GitHub Actions Automatically**
   - Triggers `release.yml` workflow
   - Builds release APK and AAB
   - Creates GitHub Release with artifacts
   - Uploads signed APK (if keystore configured)

### Manual Release (if needed)

1. **Build APK Locally**
   ```bash
   ./gradlew assembleRelease
   ```

2. **Build AAB for Play Store**
   ```bash
   ./gradlew bundleRelease
   ```

3. **Sign APK (if not signed during build)**
   ```bash
   jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
     -keystore keystore.jks \
     -storepass your-store-password \
     -keypass your-key-password \
     app/build/outputs/apk/release/app-release.apk \
     your-key-alias
   ```

## Versioning Strategy

- Follow [Semantic Versioning](https://semver.org/)
- Format: `vMAJOR.MINOR.PATCH`
  - **MAJOR**: Breaking changes, major feature additions
  - **MINOR**: New features, backward-compatible changes
  - **PATCH**: Bug fixes, small improvements

## Artifacts

- **APK**: `Weather-Dashboard-v{VERSION}.apk` - Direct installation
- **AAB**: `Weather-Dashboard-v{VERSION}.aab` - Play Store submission

## Pre-Release Checklist

- [ ] All CI tests pass
- [ ] Manual QA completed
- [ ] CHANGELOG.md updated
- [ ] Version numbers incremented
- [ ] No debug code or test data included
- [ ] ProGuard/R8 rules configured (for release builds)
- [ ] Keystore backup secured (for signed releases)

## Post-Release Tasks

- [ ] Monitor crash reports (Firebase Crashlytics or similar)
- [ ] Collect initial user feedback
- [ ] Track download metrics from GitHub Releases
- [ ] Submit AAB to Google Play Console (if publishing to Play Store)

## Rollback Procedure

If a critical issue is discovered:

1. **Unpublish Release** (if on GitHub)
   - Remove tag: `git push origin :refs/tags/v1.0.0`
   - Delete local tag: `git tag -d v1.0.0`

2. **Quick Fix**
   - Create hotfix branch
   - Bump PATCH version
   - Follow standard release process

3. **Communicate**
   - Issue announcement with workaround
   - Schedule fix release

## Keystore Management (Optional but Recommended)

### Setting up Keystore Secrets

1. **Encode Keystore to Base64**
   ```bash
   base64 keystore.jks > keystore.txt
   ```

2. **Add to GitHub Secrets**
   - `KEYSTORE_FILE`: Content of keystore.txt
   - `KEYSTORE_PASSWORD`: Keystore password
   - `KEY_ALIAS`: Signing key alias
   - `KEY_PASSWORD`: Key password

3. **Repository Settings → Secrets → Actions → New repository secret**

### Best Practices

- **Never commit keystore files**
- **Keep multiple backups** in secure, offline locations
- **Document passwords** in a secure password manager
- **Rotate keys periodically** (recommended annually)

## Troubleshooting

### Build Failures

1. Check `./gradlew clean build --stacktrace`
2. Verify Android SDK versions
3. Clear Gradle cache: `./gradlew clean --no-daemon`

### Signing Issues

1. Verify keystore password
2. Check key alias matches
3. Ensure keystore is not corrupted

### Release Workflow Not Triggering

1. Verify tag format: `v*.*.*` (e.g., v1.0.0)
2. Check GitHub Actions logs
3. Ensure workflow file syntax is valid

## Support

For issues related to the release process:
- Check GitHub Actions logs: `/actions` tab in repository
- Review this document
- Update README if procedure changes

---

**Last Updated**: 2026-03-07
**Maintainer**: DevOps Team