# 贡献指南（中文）/ Contributing in Chinese

本文件是 `CONTRIBUTING.md` 的中文版，面向以中文为主要交流语言的贡献者。两者内容一致，优先阅读英文原版以获得最新信息。

AcademyCraft 是一个关于"超能力"的 Minecraft 模组，灵感来自《某科学的超电磁炮》，原作 LambdaInnovation，现由 MohistMC 维护。

## 分支 / Branches

| 分支 | 用途 |
|---|---|
| `1.21.1` | **默认/活跃分支**。NeoForge 1.21.1 重写版，所有新工作都针对此分支。 |
| `1.12.2` | 旧版稳定线（Forge 1.12.2），仅接受关键修复。 |

> **规则**：不要直接把特性分支合并进 `1.21.1`，务必通过 **Pull Request** 评审。

## 环境 / Environment

- **JDK 21**（推荐 Temurin）
- Git
- 首次构建需联网（Gradle 下载依赖）

## 构建与运行 / Build & Run

```bash
git checkout 1.21.1
./gradlew build       # 产物 build/libs/AcademyCraft-neoforge-1.21.1.jar
./gradlew runClient   # 启动 NeoForge 测试客户端
./gradlew runServer   # 启动测试服务端
```

## 如何贡献 / Ways to contribute

1. **报 Bug** — 使用 Bug 报告模板，附上 MC/NeoForge 版本、复现步骤与日志。
2. **提想法/能力** — 使用想法（feature-request）模板。
3. **写代码** — 认领标注 `good first issue` 或 `help wanted` 的 Issue。
4. **本地化** — 仓库已含 `en_us / ja_jp / ko_kr / ru_ru / zh_cn / zh_tw`，请在 `src/main/resources/assets/academy/lang/` 保持同步。
5. **文档** — 改进 README、CONTRIBUTING 或代码注释。

## 认领任务 / Claiming work

查看仓库根目录的 **`ROADMAP.md`**（1.21.1 功能对齐清单），找标记为"部分/待办"的条目，在对应 Issue 或评论里说明"我来做 X"，避免重复劳动。

## PR 规范 / PR guidelines

- 目标分支必须是 `1.21.1`。
- 一个 PR 只做一件事。
- 遵循现有 Java 代码风格（4 空格缩进）。
- 在 PR 模板中说明"改了什么 / 为什么"。
- 修复 Issue 时引用它（`Fixes #16`）。
- 确保 `./gradlew build` 在 CI 通过。

## 许可 / License

AcademyCraft 以 **GPLv3** 授权（并附加禁止商业化条款，见 `LICENSE` 与 README）。提交即表示你同意贡献在相同许可下分发。
