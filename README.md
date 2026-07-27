![Build](https://github.com/MohistMC/AcademyCraft/actions/workflows/gradle.yml/badge.svg)
![License](https://img.shields.io/badge/license-GPLv3-blue.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)
![Loader](https://img.shields.io/badge/loader-NeoForge-orange.svg)

![](https://raw.githubusercontent.com/MohistMC/AcademyCraft/1.21.1/blob/logo.png)

# AcademyCraft

> A Minecraft mod about superability. The inspiration of AcademyCraft comes from [A Certain Scientific Railgun (とある科学の超電磁砲)](https://en.wikipedia.org/wiki/A_Certain_Scientific_Railgun) but the mod content is not limited of the background.

原作 / Original: [LambdaInnovation/AcademyCraft](https://github.com/LambdaInnovation/AcademyCraft)
本分支 / This fork: NeoForge **1.21.1** 重制版（Java 21），由 [MohistMC](https://github.com/MohistMC/AcademyCraft) 维护，[ghshhf](https://github.com/ghshhf/AcademyCraft) 参与开发。

## 📌 Project Status

| Branch | Minecraft | Loader | Status |
|---------|-----------|--------|--------|
| `1.21.1` (default) | 1.21.1 | **NeoForge** | ⚠️ In progress (`v0.0.4`) — alpha released |
| `1.12.2` | 1.12.2 | Forge | ✅ Stable (latest release **1.1.6**) |

> The `1.21.1` line is a from-scratch **NeoForge** rewrite. An alpha (`v0.0.4`) is published on GitHub Releases; for the latest stable experience, `1.12.2` remains the recommended playable build.

## Development planning

> Prioritize functional implementation, then consider beautification (UI, skill effects, etc.)
> 优先实现功能，再考虑美化(UI, 技能特效等等)

## 已实现功能 / Features

本分支（`1.21.1`，版本 `0.0.4`）目前已实现的核心系统：

- **能力系统 Ability System**：CP（能力点）、过载（Overload）、玩家等级、技能冷却，以及 4 套预设（每套 4 个技能槽）的能力配置。
- **技能系统 Skill System**：通过开发机 / 便携式开发机学习技能，包含熟练度与前置依赖；当前能力分类包含 **超电磁炮 Electromaster**，技能有：蓄力（Charging）、超电磁炮（Railgun）、电弧生成（ArcGen）、雷击（ThunderBolt）、雷爆（ThunderClap）、磁悬浮移动（MagMovement）、地雷探测（MineDetect）、身体强化（BodyIntensify）；并附带熔毁（Meltdown）能量光束等特效。
- **开发机 & 便携式开发机 Developer Machine / Portable**：消耗 IF 能量来学习技能。
- **能量系统 Energy (IF)**：IF 能量的存储与传输，为开发机与各类机器供能。
- **发电机 Generators**：太阳能发生机、风力发生机（含旋转风扇）。
- **数据终端 & 节点网络 Data Terminal & Node Network**：终端安装、应用（App）、媒体（Media）以及节点系统（名称 / 密码 / 鉴权）——本分支近期完善的模块。
- **传送使 Teleporter**：基础传送功能。
- **御坂网络 Misaka Network**：御坂编号等设定还原。
- **媒体 Media**：如 `only_my_railgun` 等动画原声轨。
- **教程 Tutorial**：新手引导流程。
- **本地化 Localization**：简体中文（zh_cn）、繁体中文（zh_tw）、英文（en_us）、日文（ja_jp）、韩文（ko_kr）、俄文（ru_ru）。

> 说明：本重制版仍处于早期（alpha）阶段，经典系统的完整度会随版本推进逐步提高。欢迎通过 Issue / PR 反馈缺失内容。

---

## 构建 / Build

环境要求 / Requirements: **JDK 21**（推荐 Temurin）、Git。

```bash
# 1. 克隆仓库
git clone https://github.com/MohistMC/AcademyCraft.git
cd AcademyCraft

# 2. 切到 1.21.1 分支
git checkout 1.21.1

# 3. 编译（产物在 build/libs/ 下，形如 academy-1.21.1-0.0.4.jar）
./gradlew build        # Windows 也可使用 gradlew.bat build

# 4.（可选）本地运行客户端 / 服务端进行调试
./gradlew runClient
./gradlew runServer
```

目标运行环境：Minecraft **1.21.1** + NeoForge **21.1.232**。

---

## 贡献 / Contributing

1. Fork 本仓库到你的 GitHub 账号。
2. 基于 `1.21.1` 分支新建特性分支（如 `feat/xxx` 或 `fix/xxx`）。
3. 编写代码并通过 `./gradlew build` 自检。
4. 向 `MohistMC/AcademyCraft` 的 `1.21.1` 分支提交 Pull Request，在描述中说明改动与测试结果。

代码规范：保持与现有包结构一致（`com.mohistmc.academy`），新增网络包需同步在 `AcademyCraft#registerPayloads` 注册。

问题反馈 / Issue：请到 [Issues](https://github.com/MohistMC/AcademyCraft/issues) 提交（Bug 或 功能构想均可）。

---

## 社区 / Community

- Discord：（待维护者补充 / TODO: add invite link）
- QQ 群：（待维护者补充 / TODO: add group number）
- 讨论请优先使用 GitHub Issues / Discussions。

---

## 配置 / Configuration

服务端配置（`academy-server.toml`）与客户端配置（`academy-client.toml`）在首次启动后生成于 `config/` 目录。

服务端关键项：

- `energy.energyMultiplier`：全局能量产出倍率（默认 1.0）。
- `skill.skillDamageMultiplier` / `skill.skillRangeMultiplier`：技能伤害 / 范围倍率。
- `skill.pvpEnabled`：技能是否可以伤害其他玩家（默认 true）。
- `crossServerSync`（跨服同步，默认 **false**）：开启后，玩家能力数据会额外写入一个**共享目录**（见 `crossServerSyncDir`）按 UUID 存储，并在登录时读回。用于 BungeeCord / Velocity 等代理网络下多后端服务器共享能力数据（详见下方「跨服能力同步」）。

客户端关键项（HUD / 音效）：`showHud`、`showCpBar`、`cpBarX/Y`、`showChargingHud`、`showKeyHints`、`enableSkillSounds`。

### 跨服能力同步 / Cross-server ability sync

NeoForge 的玩家附件（attachment）默认只保存在各后端服务器自己的 `playerdata` 中，**不会**随 BungeeCord / Velocity 跨服传送自动迁移，因此跨服后会出现「能力丢失」现象（见 Issue #16）。

开启 `crossServerSync = true` 并配置 `crossServerSyncDir` 指向一个**多后端共享的文件系统路径**（例如容器化代理集群挂载的共享卷）后，模组会在玩家登出时把能力数据写入该目录、登录时读回，从而实现跨服保留。若你的代理网络不共享文件系统，则需要改用共享数据库（MySQL 等）方案——这部分留作后续扩展，欢迎贡献。

---

## 捐赠 / Donation

You can support developement of AcademyCraft by donating. This will secure us more time to make the mod more intriguing! You would also be able to be in our donator list, both on website and in-game.

## 许可 / License

All versions of AcademyCraft are licensed under [GPLv3](http://www.gnu.org/licenses/gpl.html).

并且所有版本的 AcademyCraft 同时附加有以下版权限制：禁止任何个人、公司、企业、组织等以任何形式出售 AcademyCraft 及其内容，包括但不限于付费下载、游戏内出售 AcademyCraft 物品或能力等。Lambda Innovation 保留著作权、署名权、拥有权及最终解释权。

## Modpack permission

Yes.

## Regarding Toaru Magic Index

The mod is based on the _Railgun_ (a spinoff of _Index_) and focuses on the science side of the story, only loosely related to _Index_. It is dedicated to building an interesting experience around the idea of **superability**.
