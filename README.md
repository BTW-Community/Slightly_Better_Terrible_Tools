# Slightly Better Terrible Tools

A Better Than Wolves Community Edition addon that makes early-game tools more viable, intuitive, and interesting.

## Overview

Slightly Better Terrible Tools (SBTT) enhances the usability of BTW's early-game tools—particularly the pointy stick, sharp stone, stone shovel, and clubs—by improving their efficiency and adding new block conversion mechanics. The addon makes the progression from primitive to advanced tools feel smoother while maintaining BTW's hardcore philosophy.

## Features

### Tool Efficiency Improvements

**Less Painfully Slow**: The crappiest tools (including bare hands) have a speed boost where it makes sense.

**No Wasted Uses**: If a tool does not help, it will not be damaged on block-break.  This is primarily a convenience
feature to prevent tedious item swapping, secondarily to telegraph purpose(s) of an item.

**Bare Hands and Non-Tools**:
- Slight speed boost on log harvesting

**Pointy Stick (a.k.a Wood Chisel)**
- Slight speed boost on log and stone harvesting
- Speed boost on prying up loose cobblestone/brick blocks
- Able to loosen a single block of dirt or sparse grass
- 2 extra uses to add value and incentivize experimentation

**Sharp Stone (a.k.a. Stone Chisel)**
- Slight speed boost on log and stone harvesting
- Shatters ice, glass, and other breakables faster
- Cuts grass in stages with a VERY low chance for hemp seeds
- Chips through the final stages of rough stone at half speed (as opposed to zero boost)

**Stone Shovel**
- No longer disturbs neighboring dirt/grass blocks when digging.
- Can firm up loose dirt-like blocks instantly by right-clicking (costs 1 durability).
- 50% more durability (encourages digging beyond just clay)
- 50% faster on appropriate materials (improved to incentivize use in context)

**Clubs (Wood & Bone)**
- Can firm up loose dirt-like blocks SLOWLY (costs 2 durability)

**Changes to Higher Level Tools (Contextually-Necessary)**
- Iron and better shovels can also firm-up loose dirt
- Iron chisel and diamond chisel efficiency boosts toward:
    - Loose masonry blocks
    - Solid stone-like blocks
    - Shatterable blocks
    - Web blocks
- Iron and diamond chisels now harvest whole ores
  - This was done to match their use as viable diamond-harvesters already.
  - It gives you a reason to use them up and hints at their existing gem-harvesting use.

**Planned Changes/Features**
- Add a tiered damage system to shovels and axes to make you not as hesitant to use them on soft, easy materials.
- Allow higher tier shovels to pack dirt (2:1 ratio for firm dirt and packed earth) instantly via right-click
- Allow bone club to SLOWLY pack dirt by holding left click (costs 2 durability)

### Block Changes

**Loose Ground Digs Easier**: Loose dirt, loose grass, sand, and gravel are faster to dig up in general.
- Relative speeds now (large to small boost: loose-dirt/grass > sand > gravel > firm-dirt/grass.

**Restored Grass Block Disturbance**
- If a dirt block below grass is destroyed by an improper tool (e.g., bare hands), the grass turns to loose dirt and falls.
- It was removed from the standard game because we didn't have enough options to avoid it.
- It is an interesting feature, and now it's not as annoying!
- Remember, you can loosen the dirt below with a pointy stick, and stone shovels no longer disturb.

**New Recipes**
- You can now make regular dirt out of loose dirt and a slime ball.
  - This is primarily to use Slime Balls for something else beyond sticky pistons, and
    because we needed a way to patch up holes in ceilings still.
  - Specifically Recipes:
      - 1 Loose Dirt Block + 1 Slime Ball --> 1 Dirt Block
      - 2 Loose Dirt Slabs + 1 Slime Ball --> 1 Dirt Block
      - 8 Dirt Piles + 1 Slime Ball --> 1 Dirt Block

### Unrelated Fixes

- Modifies the Better Than Wolves title logo to fix the kerning.

## Installation

1. Install Better Than Wolves: Community Edition 3.0.0 + Legacy Fabric by following along with the instructions on the [wiki](https://wiki.btwce.com/view/3.0.0_Beta).
2. Download this addon's JAR file from the Releases page.
3. Place the addon JAR file in your `.minecraft/mods` folder
4. Launch Minecraft, and it should display to chat on world load.

## Compatibility

- **Required**: Better Than Wolves CE 3.0.0
- **Mod Loader**: Fabric/Mixin based (Packaged with the BTW Instance)
- Uses primarily Mixin injections for compatibility/maintainability.
- This currently won't make changes to any blocks or tools added by other addons, with the exception that it will probably prevent wasted durability.  Most of the changes I've made target specific, existing blocks and items from the base game, without overriding (but scope-limited).

## License
This project is released under the [BSD Zero-Clause License](LICENSE).  
You’re free to use, modify, and share it however you see fit.

---

## Credits
- **Addon author**: Abigail Read
- **Better Than Wolves**: Created by *FlowerChild*, continued by the BTW Community
- Thanks to the **Legacy Fabric team** for keeping classic modding alive.

---
"A [good] game is a series of interesting choices." &ensp;– Sid Meier
</br><small>
[wikiquote](https://en.wikiquote.org/wiki/Sid_Meier)
</small>

