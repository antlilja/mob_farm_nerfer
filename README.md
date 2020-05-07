# Mob Farm Nerfer

## Features:

- Removes drops from mobs that have taken fall damage over a specified threshold.
- Removes drops from enemies that are in an are that's considered crowded.
- Removes drops from hostile entities that can't reach the attacking player.
 

## Options:

Options can be specified in a *mob_farm_nerfer.properties* file.

- *fall_damage_threshold* specifies the percentage of fall damage that trigers an entity to not drop loot or xp.
- *crowding_threshold* specifies the amount of mobs in an area that is considered crowded.
- *crowding_radius* specifies the radius of the area that entities look in around them to find the amount of entities.
- *max_path_checking_distance* specifies maximum distance the mod will try to pathfind to the player.

 

## Example file with default values:

```properties
fall_damage_threshold = 0.5
crowding_threshold = 5
crowding_radius = 3
max_path_checking_distance = 25
```
