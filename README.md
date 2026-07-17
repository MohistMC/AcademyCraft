![Build](https://github.com/MohistMC/AcademyCraft/actions/workflows/gradle.yml/badge.svg)
![License](https://img.shields.io/badge/license-GPLv3-blue.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)
![Loader](https://img.shields.io/badge/loader-NeoForge-orange.svg)

![](https://raw.githubusercontent.com/MohistMC/AcademyCraft/1.21.1/blob/logo.png)

# AcademyCraft

A Minecraft mod about superability. The inspiration of AcademyCraft comes from [A Certain Scientific Railgun (とある科学の超電磁砲)](https://en.wikipedia.org/wiki/A_Certain_Scientific_Railgun) but the mod content is not limited of the background.

Original: https://github.com/LambdaInnovation/AcademyCraft

## 📌 Project Status

| Branch | Minecraft | Loader | Status |
|---------|-----------|--------|--------|
| `1.21.1` (default) | 1.21.1 | **NeoForge** | ⚠️ In progress (`v0.0.1`) — porting, **not yet released** |
| `1.12.2` | 1.12.2 | Forge | ✅ Stable (latest release **1.1.6**) |

> The `1.21.1` line is a from-scratch **NeoForge** rewrite and is **not published yet**.
> For a playable build today, use the `1.12.2` release. Once `1.21.1` reaches a milestone we will publish an alpha/beta on GitHub Releases (and CurseForge / Modrinth).

## Development planning

> Prioritize functional implementation, then consider beautification (UI, skill effects, etc.)
> 优先实现功能，再考虑美化(UI, 技能特效等等)

## Building from source

Requirements: **JDK 21** (Temurin recommended), Git, and internet access (for Gradle to download dependencies).

```bash
git clone https://github.com/MohistMC/AcademyCraft.git
cd AcademyCraft
git checkout 1.21.1

./gradlew build      # build the mod jar -> build/libs/*.jar
./gradlew runClient  # launch a NeoForge test client (userdev)
./gradlew runServer  # launch a test server
```

CI builds every push/PR to `1.21.1` via `.github/workflows/gradle.yml` and uploads a dev jar as a workflow artifact.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Issues labeled `good first issue` / `help wanted` are good entry points.

## Issue (Idea, Bug) Submission

Please go to [Issues](https://github.com/MohistMC/AcademyCraft/issues) and submit a new ticket.

## Misc

### Donation

You can support developement of AcademyCraft by donating. This will secure us more time to make the mod more intriguing!

You would also be able to be in our donator list, both on website and in-game :beer:

## License

All versions of AcademyCraft are licensed under [GPLv3](http://www.gnu.org/licenses/gpl.html).

And all versions of AcademyCraft are additionally licensed as following:

Prohibits any person, company, business, organization, etc. from selling AcademyCraft and its contents in any form, including but not limited to paid downloads (including but not limited to various legal currencies, virtual currency, game token, etc.) AcademyCraft's items, the sale of AcademyCraft ability within the game, etc.

Lambda Innovation retains the copyright, the right of authorship, the ownership, etc. of AcademyCraft, regardless of all agreements, and any provision that requires these rights or a part of them is deemed invalid.

Lambda Innovation reserves the right of final interpretation and reserves the right to deny all agreements to revoke all authorizations.

所有版本的AcademyCraft使用[GPLv3](http://www.gnu.org/licenses/gpl.html)协议。

并且所有版本的AcademyCraft同时附加有以下版权限制：

禁止任何个人、公司、企业、组织等以任何形式出售 AcademyCraft 及其内容，包括但不限于付费下载(包括但不限于各种法定货币、虚拟货币、虚拟币、游戏代币等)，游戏内出售 AcademyCraft 物品，游戏内出售 AcademyCraft 能力等。

LambdaInnovation对于AcademyCraft的著作权、署名权、拥有权、版权等无视一切协议而保留，任何要求这些权利或其中一部分的条款均视为无效。

LambdaInnovation保留最终解释权，并保留否定一切协议撤销一切授权的权利。

## Modpack permission

Yes. >)

## Regarding Toaru Magic Index

Many people have been asking questions about whether or how much the mod will be related to
the original story _A Certain Magic Index_. Our answer is that although AC is based on the
_Railgun_, which is a spinoff of _Index_, the mod will only focus on the science side of
the story, and thus just loosely related to _Index_.

The mod is dedicated to build an interesting experience evolved around the idea of **superability**,
that's really everything.
