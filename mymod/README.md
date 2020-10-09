# ![CC: Tweaked](doc/logo.png)
[![Current build status](https://github.com/SquidDev-CC/CC-Tweaked/workflows/Build/badge.svg)](https://github.com/SquidDev-CC/CC-Tweaked/actions "Current build status") [![Download CC: Tweaked on CurseForge](http://cf.way2muchnoise.eu/title/cc-tweaked.svg)](https://minecraft.curseforge.com/projects/cc-tweaked "Download CC: Tweaked on CurseForge")

CC: Tweaked is a fork of [ComputerCraft](https://github.com/dan200/ComputerCraft), adding programmable computers,
turtles and more to Minecraft.

## What?
ComputerCraft has always held a fond place in my heart: it's the mod which really got me into Minecraft, and it's the
mod which has kept me playing it for many years. However, development of the original mod has slowed, as the original
developers have had less time to work on the mod, and moved onto other projects and commitments.

CC: Tweaked (or CC:T for short) is an attempt to continue ComputerCraft's legacy. It's not intended to be a competitor
to CC, nor do I want to take it in a vastly different direction to the original mod. Instead, CC:T focuses on making the
ComputerCraft experience as _solid_ as possible, ironing out any wrinkles that may have developed over time.

## Features
CC: Tweaked contains all the features of the latest version of ComputerCraft, as well as numerous fixes, performance
improvements and several nifty additions. I'd recommend checking out [the releases page](https://github.com/SquidDev-CC/CC-Tweaked/releases)
to see the full set of changes, but here's a couple of the more interesting additions:

 - Improvements to the `http` library, including websockets, support for other HTTP methods (`PUT`, `DELETE`, etc...)
   and configurable limits on HTTP usage.
 - Full-block wired modems, allowing one to wrap non-solid peripherals (such as turtles, or chests if Plethora is
   installed).
 - Pocket computers can be held like maps, allowing you to view the screen without entering a GUI.
 - Printed pages and books can be placed in item frames and held like maps.
 - Several profiling and administration tools for server owners, via the `/computercraft` command. This allows operators
   to track which computers are hogging resources, turn on and shutdown multiple computers at once and interact with
   computers remotely.
 - Closer emulation of standard Lua, adding the `debug` and `io` libraries. This also enables seeking within binary
   files, meaning you don't need to read large files into memory.
 - Allow running multiple computers on multiple threads, reducing latency on worlds with many computers.

## Relation to CCTweaks?
This mod has nothing to do with CCTweaks, though there is no denying the name is a throwback to it. That being said,
several features have been included, such as full block modems, the Cobalt runtime and map-like rendering for pocket
computers.

## Contributing
Any contribution is welcome, be that using the mod, reporting bugs or contributing code. If you want to get started
developing the mod, [check out the instructions here](CONTRIBUTING.md#developing).

## Community
If you need help getting started with CC: Tweaked, want to show off your latest project, or just want to chat about
ComputerCraft we have a [forum](https://forums.computercraft.cc/) and [Discord guild](https://discord.computercraft.cc)!
There's also a fairly populated, albeit quiet [IRC channel](http://webchat.esper.net/?channels=computercraft), if that's
more your cup of tea.

I'd generally recommend you don't contact me directly (email, DM, etc...) unless absolutely necessary (i.e. in order to
report exploits). You'll get a far quicker response if you ask the whole community!

## Using
CC: Tweaked is hosted on my maven repo, and so is relatively simple to depend on. You may wish to add a soft (or hard)
dependency in your `mods.toml` file, with the appropriate version bounds, to ensure that API functionality you depend
on is present.

```groovy
repositories {
  maven { url 'https://squiddev.cc/maven/' }
}

dependencies {
  implementation fg.deobf("org.squiddev:cc-tweaked-${mc_version}:${cct_version}")
}
```

You should also be careful to only use classes within the `dan200.computercraft.api` package. Non-API classes are
subject to change at any point. If you depend on functionality outside the API, file an issue, and we can look into
exposing more features.
