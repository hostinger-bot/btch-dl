# Security Policy

## Supported Versions

Only the latest published version receives security fixes.

| Version | Supported |
|---------|-----------|
| 1.2.x (latest) | ✅ |
| < 1.2.0 | ❌ |

## Reporting a Vulnerability

**Do not open a public GitHub issue for security vulnerabilities.**

Report privately via GitHub's built-in security advisory:  
**https://github.com/hostinger-bot/btch-dl/security/advisories/new**

Include in your report:
- A clear description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (optional)

You can expect an initial response within **72 hours**. If the vulnerability is confirmed, a patch will be prioritized for the next release.

## Scope

This repository contains the Android application and the Ktor client SDK. Security concerns most relevant to this project:

- Malicious payloads returned by the backend (`backend1.tioo.eu.org`) passed unsanitized to the UI or client
- Dependency vulnerabilities (reported via Dependabot)
- Build pipeline or supply-chain attacks (e.g. compromised GitHub Actions build)

Out of scope: vulnerabilities in the downstream platforms (Instagram, TikTok, YouTube, etc.) themselves.

## Dependencies

Dependency security is monitored automatically via **Dependabot**. If you discover a vulnerability in a dependency, please report it to that package's maintainer and open a Dependabot alert here if applicable.

## Attribution

Responsible disclosure is appreciated. Reporters of valid, confirmed vulnerabilities will be credited in the release notes unless anonymity is requested.
