# Changelog

All notable changes to the Weather Dashboard Android application will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned
- Initial MVP release
- Weather data fetching from Open-Meteo API
- Unit test coverage

## [1.0.0] - 2026-03-07

### Added
- Initial release of Weather Dashboard Android app
- Current weather display functionality
- Temperature, humidity, and wind speed metrics
- Clean, responsive UI for Android devices
- CI/CD pipeline with GitHub Actions
- Automated build and test on push/PR
- Automated release generation on version tags
- Debug and release APK generation
- Gradle dependency caching
- Android SDK 34 support

### Features
- Real-time weather data
- Location-based weather (if GPS enabled)
- Multiple unit support (metric/imperial)
- Dark/Light theme support
- Offline caching for last known weather

### Infrastructure
- GitHub Actions CI/CD pipeline
- Automated testing workflow
- Release workflow with APK and AAB generation
- Code coverage reporting (optional JaCoCo)

### Documentation
- Release process documentation (RELEASE.md)
- CI/CD workflow setup
- Deployment and rollback procedures

---

## Version Format

- **Added** - New features
- **Changed** - Changes in existing functionality
- **Deprecated** - Soon-to-be removed features
- **Removed** - Removed features
- **Fixed** - Bug fixes
- **Security** - Security patches

---

**Note**: This changelog is automatically updated with each release. Follow the versioning strategy outlined in RELEASE.md.