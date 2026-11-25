# 🍀 Lucky Clover Reborn

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)
![Mod Loaders](https://img.shields.io/badge/Mod_Loaders-NeoForge_%7C_Fabric-orange.svg)
![Dependencies](https://img.shields.io/badge/Dependencies-None_(Standalone)-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)
![Java](https://img.shields.io/badge/Java-21-red.svg)

**Lucky Clover Reborn** is a modern reimagining of the classic *Magic Clover* mod popularized during the golden age of Minecraft YouTube challenges (*Lucky Boss Challenges*).

Break wild grass in survival to discover the legendary four-leaf clover, then right-click anywhere to roll the dice of destiny!

![Banner](https://i.imgur.com/HxtnVuM.png)

---

## ✨ Features

- 🌿 **Survival Grass Harvesting**: Breaking wild grass, tall grass, ferns, or large ferns in survival mode gives a rare chance (1 in 200 by default) to drop a Lucky Clover.
- 🎲 **Unlimited Modpack Compatibility**: Unlike other lucky block mods with hardcoded item tables, Lucky Clover Reborn queries Minecraft's central registry (`Registries.ITEM`) dynamically. It can roll and drop **any item from any mod** installed in your modpack (Create, Twilight Forest, Mekanism, etc.)!
- 🛡️ **Zero-Crash Safety Guard**: Built with multi-pass instantiation protection. If an external mod has broken or abstract items, they are safely caught and skipped without crashing the game or corrupting your world.
- 🎯 **Freedom of Activation**: Right-click (Mouse 2) anywhere—whether looking into the open sky, at the horizon, or directly at a block. The clover activates instantly, ejecting the rolled loot forward with classic celebratory sounds and emerald particles!
- ⚡ **The 5 Classic Hazard Events**: A calibrated 12% chance to trigger an adrenaline-pumping hazard instead of an item:
  - ⚡ **Lightning Strike**: Summons a lightning bolt with thunder sound effects.
  - 💥 **Surprise Creeper**: Spawns right in front of you (with a chance to be a Charged Creeper!).
  - 🧟 **Armed Zombie Horde**: Spawns armored zombies equipped with iron swords and helmets.
  - 💀 **Sniper Skeleton**: Spawns a skeleton equipped with a Power II enchanted bow.
  - 💣 **Primed TNT**: Drops an activated TNT block with a short 2.5-second fuse ticking at your feet!
- 📦 **Zero Heavy Dependencies**: Pure, native support for **NeoForge** and **Fabric** (only requires standard Fabric API). No companion frameworks like Architectury API required!
- 🎨 **Authentic Pixel Art**: Features the iconic 16x16 emerald sprite from the classic era with full enchanted glint.

---

## 📦 Versions

| Minecraft | Mod Version | Loaders | Java | Branch |
| :--- | :--- | :--- | :--- | :--- |
| **1.21.1** | `1.1.0` | NeoForge, Fabric | Java 21 | [`main`](https://github.com/GabehEPW/Lucky-Clover-Reborn/tree/main) |
| **1.20.1** | `1.0.0` | Forge, Fabric | Java 17 | [`1.20.1`](https://github.com/GabehEPW/Lucky-Clover-Reborn/tree/1.20.1) |

---

## 📖 How to Play

### Survival Mode
1. Explore open plains, forests, or hills and start harvesting wild grass or ferns with your hand or tools.
2. Listen for the experience orb sound effect—a **Lucky Clover** has dropped!
3. Hold the Lucky Clover in your hand and **right-click**.
4. Enjoy your random reward—or run for your life if disaster strikes!

### Creative Mode
The mod adds a dedicated **Lucky Clover Reborn** creative tab displaying the clover with its custom icon. It is also searchable in the vanilla **Tools & Utilities** and **Ingredients** categories.

---

## ⚙️ Configuration Guide

The mod automatically generates a configuration file located at:  
`config/lucky_clover_reborn-common.toml`

Modpack creators can fully customize every drop rate, hazard toggle, and blacklist item IDs to tailor the experience:

```toml
# General settings for Lucky Clover Reborn
[general]
    # Chance of dropping a Lucky Clover when breaking grass or ferns.
    # Default: 0.005 (0.5% or 1 in 200). Example: 0.02 for 2%, 0.05 for 5%.
    # Range: 0.0 ~ 1.0
    grassDropChance = 0.005

# Item roll settings
[clover]
    # Chance of triggering a bad luck hazard instead of dropping an item.
    # Default: 0.12 (12%). Set to 0.0 to disable all hazards for 100% item drops!
    # Range: 0.0 ~ 1.0
    badLuckChance = 0.12

    # Maximum stack size when a stackable item is rolled.
    # Range: 1 ~ 64
    maxStackCount = 8

    # List of item registry IDs that should NEVER be dropped by the Lucky Clover.
    # Modpack creators can add any broken, creative-only, or game-breaking items here.
    itemBlacklist = [
        "minecraft:bedrock",
        "minecraft:barrier",
        "minecraft:structure_void",
        "minecraft:command_block"
    ]

# Individual hazard event toggles
[hazards]
    # Enable Lightning Bolt hazard event.
    enableLightning = true

    # Enable Surprise Creeper hazard event.
    enableCreeper = true

    # Chance for the creeper to be electric/charged. Default: 0.15 (15%).
    # Range: 0.0 ~ 1.0
    creeperChargedChance = 0.15

    # Enable Armed Zombie Horde hazard event.
    enableZombieHorde = true

    # Enable Sniper Skeleton hazard event.
    enableSniperSkeleton = true

    # Enable Primed TNT hazard event.
    enablePrimedTnt = true

    # Fuse duration in ticks for primed TNT (20 ticks = 1 second). Default: 50 (2.5s).
    # Range: 10 ~ 200
    tntFuseTicks = 50
```

---

## 🛠️ Building from Source

To build the mod from source, clone the repository and run Gradle:

```bash
git clone https://github.com/GabehEPW/Lucky-Clover-Reborn.git
cd Lucky-Clover-Reborn
./gradlew build
```

The compiled standalone `.jar` files will be generated in:  
- `neoforge/build/libs/lucky_clover_reborn-neoforge-1.21.1-1.1.0.jar`  
- `fabric/build/libs/lucky_clover_reborn-fabric-1.21.1-1.1.0.jar`

---

## 📜 License

This project is licensed under the **MIT License**.

Copyright © 2026 **GabehEPW**

*Special thanks to **konwboj** for the original classic Magic Clover mod concept.*
