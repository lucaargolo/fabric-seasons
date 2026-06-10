<p align="center"><img src="https://i.imgur.com/WszJUGI.png"></p>
<p align="center">A simple mod for Minecraft that adds seasons to the game, changing biomes as you play.</p>
<p align="center">
  <a title="Fabric API" href="https://github.com/FabricMC/fabric">
    <img src="https://i.imgur.com/Ol1Tcf8.png" width="151" height="50" />
  </a>
</p>
<p align="center">
  <a href="https://github.com/lucaargolo/fabric-seasons/actions"><img src="https://github.com/lucaargolo/fabric-seasons/workflows/Build/badge.svg"/></a>
  <a href="https://opensource.org/licenses/MPL-2.0"><img src="https://img.shields.io/badge/License-MPL%202.0-blue"></a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/fabric-seasons"><img src="http://cf.way2muchnoise.eu/versions/413523_latest.svg"></a>
</p>
<p align="center">
  <a href="https://www.curseforge.com/minecraft/mc-mods/fabric-seasons"><img src="http://cf.way2muchnoise.eu/full_413523_downloads.svg"></a>
  <a href="https://modrinth.com/mod/fabric-seasons"><img src="https://img.shields.io/badge/dynamic/json?color=00AF5C&logo=modrinth&label=modrinth&query=downloads&suffix=%20downloads&url=https://api.modrinth.com/v2/project/fabric-seasons"></a>
</p>

## Description

ServerSeasons adds four seasons to Minecraft, each lasting 28 in-game days (configurable). The current season is defined by the world time (using `/time set 0` will reset to day 1 of Spring). Each season has its own changes. Spring will match the original biome colors and biome behaviors.

The mod has 2 components:

- Server: If installed on the server, biomes will have different temperatures. This enables weather changes like snowing and block freezing in the winter. The server component also contains seasonal crops, allowing crops to grow at different speeds according to the season.
- Client: If installed on the client, biomes will have different colormaps according to the current season. This is just a visual change and it will not physically impact your world in any form.

To get the full experience, it is recommended that you install the mod on both the server and the client.

All biomes are grouped in 5 categories with the following characteristics:

- Permanently Frozen Biomes (`temp ≤ -0.51`): Permanently frozen year-round
- Usually Frozen Biomes (`temp ≤ 0.15`): Becomes ice free in the Summer
- Temperate Biomes (`temp ≤ 0.49`): Ice free in the Spring and Summer
- Usually Ice Free Biomes (`temp ≤ 0.79`): Only freezes in the Winter
- Ice Free Biomes (`temp > 0.79`): Ice free year-round, will also allow rain in the Winter

<p>
  <img src="https://i.imgur.com/NdYBkgC.gif">
  <img src="https://i.imgur.com/gH5seb5.gif">
  <img src="https://i.imgur.com/Qs600XR.gif">
</p>

Only overworld biomes can have this seasonality, which can be configured in `config/seasons.json`

*Please note that this mod melts / applies freezing blocks and snow **within simulation distance**. This means that anything outside is unaffected by the seasons unless if you travel to these blocks. It is currently not possible to globally melt or freeze blocks outside of this distance. It is possible to mitigate this by temporarily increasing the `randomTickSpeed`. Otherwise if you want to completely avoid this limitation, set `doTemperatureChanges` in the config file to `false`.*

## Configurations

ServerSeasons can be configured in your instance's `config` folder. The file is called `seasons.json`. To apply your own changes to the configuration, please restart your client.
| Config Variable    | Description | Default
| -------- | ------- | -------
| springLength / summerLength / fallLength / winterLength | The integer for the length of a season in "game ticks" (672000 game ticks == 33600 in-game seconds == 28 in-game days) | 672000
| startingSeason | The starting season of the year. Only applicable when `isSeasonLocked` is `false` and `isSeasonTiedWithSystemTime` is `false`. Valid values are `SPRING`, `SUMMER`, `FALL`, or `WINTER`.  | "SPRING"
| isSeasonLocked | Flag to lock the season | false
| lockedSeason    | The season to lock if `isSeasonLocked` is true. Valid values are `SPRING`, `SUMMER`, `FALL`, or `WINTER`.| "SPRING"
| dimensionAllowList | The list of dimensions to allow seasons. This may be useful for multiplayer. A valid value would be one in the format of `"<namespace>:<world_name>"`. | ["minecraft:overworld"]
| doTemperatureChanges | Flag that modifies precipitation and freezing on biomes. **Setting to false essentially disables any biome behavior or aesthetic changes year round.**  | true
| shouldSnowyBiomesMeltInSummer | Flag that determines if "Usually Freezing Biomes" should melt during Summer | true
| shouldIceNearWaterMelt | When true, ice will melt in an ice-free biome regardless of being placed by a player when it is beside a water source or flowing water. When false, default ice behavior occurs. | false
| biomeDenyList | The list of biomes that keep it's original temperature and freezing behavior year-round. A valid value would be one in the format of `"<namespace>:<biome_name>"`. | ["terralith:glacial_chasm", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean", "minecraft:ocean", "minecraft:deep_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean"]
| biomeForceSnowInWinterList | The list of biomes to force freezing during the winter season. A valid value would be one in the format of `"<namespace>:<biome_name>"`. | ["minecraft:plains", "minecraft:sunflower_plains", "minecraft:stony_peaks"]
| isSeasonTiedWithSystemTime | Flag that determines if the seasons should be synced to real world seasonal cycles | false
| isInNorthHemisphere | Only used if `isSeasonTiedWithSystemTime` is set to true. Determines if the player is situated in the North Hemisphere to properly calculate the seasons. | true
| isFallAndSpringReversed | By default, there are more biomes that snow over during Fall. Setting this to true will reverse it, so that more biomes snow over in the Spring compared to Fall. | false
| isSeasonMessingCrops | Flag that determines if the seasons should change the default growth rate | true
| isSeasonMessingBonemeal | Flag that determines if the seasons should change the default bonemeal behavior | false
| doCropsGrowNormallyUnderground | Flag that determines if crops underground should have default behavior. Underground is considered to be sky light level of 0. | false
| doAnimalsBreedInWinter | Flag that determines if animals should be able to breed during the winter | true
| notifyCompat | Flag that determines if a message should be sent when a mod has a compatibility addon and you do not have it installed | true
| debugCommandEnabled | Only useful in development environments | false

## Crop Growth

Crops will grow at **different speeds** depending on the current season.

<p>
  <img src="https://i.imgur.com/75gqPqS.png">
</p>

Each **individual crop** will have a **pre-configured growth speed** for one of the four seasons, this is controlled by a datapack and can be changed by the user if they want. 

<p>
  <img src="https://i.imgur.com/dR4OYPT.png">
</p>

Please refer to online tutorials on **how to make a datapack**. Make sure that your `pack_format` is of the correct version for your particular Minecraft Version. If you are lost, there is an example datapack found in the **pinned messages** of the Discord channel.

## Description

ServerSeasons adds four seasons to Minecraft, each lasting 28 in-game days (configurable). The current season is defined by the world time (using `/time set 0` will reset to day 1 of Spring). Each season has its own changes. Spring will match the original biome colors and biome behaviors.

The mod has 2 components:

- Server: If installed on the server, biomes will have different temperatures. This enables weather changes like snowing and block freezing in the winter. The server component also contains seasonal crops, allowing crops to grow at different speeds according to the season.
- Client: If installed on the client, biomes will have different colormaps according to the current season. This is just a visual change and it will not physically impact your world in any form.

To get the full experience, it is recommended that you install the mod on both the server and the client.

All biomes are grouped in 5 categories with the following characteristics:

- Permanently Frozen Biomes (`temp ≤ -0.51`): Permanently frozen year-round
- Usually Frozen Biomes (`temp ≤ 0.15`): Becomes ice free in the Summer
- Temperate Biomes (`temp ≤ 0.49`): Ice free in the Spring and Summer
- Usually Ice Free Biomes (`temp ≤ 0.79`): Only freezes in the Winter
- Ice Free Biomes (`temp > 0.79`): Ice free year-round, will also allow rain in the Winter

<p>
  <img src="https://i.imgur.com/NdYBkgC.gif">
  <img src="https://i.imgur.com/gH5seb5.gif">
  <img src="https://i.imgur.com/Qs600XR.gif">
</p>

Only overworld biomes can have this seasonality, which can be configured in `config/seasons.json`

*Please note that this mod melts / applies freezing blocks and snow **within simulation distance**. This means that anything outside is unaffected by the seasons unless if you travel to these blocks. It is currently not possible to globally melt or freeze blocks outside of this distance. It is possible to mitigate this by temporarily increasing the `randomTickSpeed`. Otherwise if you want to completely avoid this limitation, set `doTemperatureChanges` in the config file to `false`.*

## Configurations

ServerSeasons can be configured in your instance's `config` folder. The file is called `seasons.json`. To apply your own changes to the configuration, please restart your client.
| Config Variable    | Description | Default
| -------- | ------- | -------
| springLength / summerLength / fallLength / winterLength | The integer for the length of a season in "game ticks" (672000 game ticks == 33600 in-game seconds == 28 in-game days) | 672000
| isSeasonLocked | Flag to lock the season | false
| lockedSeason    | The season to lock if `isSeasonLocked` is true. Valid values are `SPRING`, `SUMMER`, `FALL`, or `WINTER`.| "SPRING"
| dimensionAllowList | The list of dimensions to allow seasons. This may be useful for multiplayer. A valid value would be one in the format of `"<namespace>:<world_name>"`. | ["minecraft:overworld"]
| doTemperatureChanges | Flag that modifies precipitation and freezing on biomes. **Setting to false essentially disables any biome behavior or aesthetic changes year round.**  | true
| shouldSnowyBiomesMeltInSummer | Flag that determines if "Usually Freezing Biomes" should melt during Summer | true
| shouldIceNearWaterMelt | When true, ice will melt in an ice-free biome regardless of being placed by a player when it is beside a water source or flowing water. When false, default ice behavior occurs. | false
| biomeDenyList | The list of biomes that keep it's original temperature and freezing behavior year-round. A valid value would be one in the format of `"<namespace>:<biome_name>"`. | ["terralith:glacial_chasm", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean", "minecraft:ocean", "minecraft:deep_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean"]
| biomeForceSnowInWinterList | The list of biomes to force freezing during the winter season. A valid value would be one in the format of `"<namespace>:<biome_name>"`. | ["minecraft:plains", "minecraft:sunflower_plains", "minecraft:stony_peaks"]
| isSeasonTiedWithSystemTime | Flag that determines if the seasons should be synced to real world seasonal cycles | false
| isInNorthHemisphere | Only used if `isSeasonTiedWithSystemTime` is set to true. Determines if the player is situated in the North Hemisphere to properly calculate the seasons. | true
| isFallAndSpringReversed | By default, there are more biomes that snow over during Fall. Setting this to true will reverse it, so that more biomes snow over in the Spring compared to Fall. | false
| isSeasonMessingCrops | Flag that determines if the seasons should change the default growth rate | true
| isSeasonMessingBonemeal | Flag that determines if the seasons should change the default bonemeal behavior | false
| doCropsGrowNormallyUnderground | Flag that determines if crops underground should have default behavior. Underground is considered to be sky light level of 0. | false
| doAnimalsBreedInWinter | Flag that determines if animals should be able to breed during the winter | true
| notifyCompat | Flag that determines if a message should be sent when a mod has a compatibility addon and you do not have it installed | true
| debugCommandEnabled | Only useful in development environments | false

## Crop Growth

Crops will grow at **different speeds** depending on the current season.

<p>
  <img src="https://i.imgur.com/75gqPqS.png">
</p>

Each **individual crop** will have a **pre-configured growth speed** for one of the four seasons, this is controlled by a datapack and can be changed by the user if they want. 

<p>
  <img src="https://i.imgur.com/dR4OYPT.png">
</p>

Please refer to online tutorials on **how to make a datapack**. Make sure that your `pack_format` is of the correct version for your particular Minecraft Version. If you are lost, there is an example datapack found in the **pinned messages** of the Discord channel.

## License
Distributed under the Mozilla Public License 2.0. See `LICENSE` for more information.

## Build
If you want to build this yourself, please clone the repository and execute `gradlew build` in the projects folder. 

Artifacts will be generated at `/build/libs`


