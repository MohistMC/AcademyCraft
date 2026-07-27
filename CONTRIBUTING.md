# Contributing to AcademyCraft

Thanks for your interest in AcademyCraft! This guide explains how to build, run, and contribute to the mod.

## Branches

| Branch | Purpose |
|---------|---------|
| `1.21.1` | **Default / active.** NeoForge 1.21.1 rewrite (currently `v0.0.1`, not yet released). All new work targets this branch. |
| `1.12.2` | Legacy stable line (Forge 1.12.2). Only critical fixes. |

> **Rule:** Never merge a feature branch directly into `1.21.1`. Always open a **Pull Request** and get it reviewed first.

## Environment

- **JDK 21** (Temurin recommended)
- Git
- Internet access (Gradle downloads dependencies on first build)

## Build & Run

```bash
git checkout 1.21.1
./gradlew build       # produces build/libs/AcademyCraft-neoforge-1.21.1.jar
./gradlew runClient   # run a NeoForge test client
./gradlew runServer   # run a test server
./gradlew gameTest    # run NeoForge GameTests (if any are registered)
```

## Ways to contribute

1. **Report bugs** — use the bug-report template. Include your Minecraft / NeoForge version, steps to reproduce, and logs.
2. **Suggest features / abilities** — use the feature-request template.
3. **Code** — pick an issue labeled `good first issue` or `help wanted`.
4. **Localization** — AcademyCraft already ships `en_us`, `ja_jp`, `ko_kr`, `ru_ru`, `zh_cn`, `zh_tw`. Help keep them in sync in `src/main/resources/assets/academy/lang/`.
5. **Docs** — improve the README, this file, or code comments.

## Pull Request guidelines

- Target the `1.21.1` branch.
- Keep PRs focused (one logical change).
- Follow the existing code style (Java, 4-space indent).
- Describe **what** changed and **why** in the PR template.
- If it fixes an issue, reference it (`Fixes #16`).
- Make sure `./gradlew build` passes in CI.

## Code of Conduct

Be respectful. We want AcademyCraft to be a welcoming project for players and contributors alike.

## License

AcademyCraft is licensed under **GPLv3**. By contributing, you agree your contributions are distributed under the same license.
