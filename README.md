# Softban Plugin

## Overview

The Softban plugin for Bukkit/Spigot servers allows administrators to apply varying levels of "soft bans" to players. This plugin implements a graduated punishment system without permanently banning players from the server.

## Features

- Set softban levels (1-5) for online and offline players
- Remove softbans from players
- Tab completion for commands and player names
- Confirmation system for actions on offline players
- Graduated punishment system

## Installation

1. Download the Softban.jar file
2. Place it in your server's `plugins` folder
3. Restart your server or run `/reload confirm`

## Commands

### Set Softban
```
/softban set <username> <level>
```

- Sets the softban level for a player
- `<username>`: The player's username
- `<level>`: A number between 1 and 5

### Remove Softban
```
/softban remove <username>
```
- Removes the softban from a player
- `<username>`: The player's username

## Punishment System

The Softban plugin implements the following punishments based on the softban level:

1. **Increased Damage Taken**: Players take 10% more damage per softban level.
2. **Decreased Damage Dealt**: Players deal 10% less damage per softban level.
3. **Reduced Movement Speed**: Player's velocity is reduced by 10% per softban level.
4. **Resource Gathering Penalty**: Chance to not drop items when breaking blocks.
5. **XP Gain Reduction**: Reduces XP gained from activities.
6. **Hunger Depletion**: Increases the rate of hunger depletion.
7. **Item Durability Loss**: Increases damage to items when used.
8. **Inventory Restrictions**: Blocks a number of inventory slots with barrier blocks.
9. **Night Vision Impairment**: Applies blindness effect when sneaking.

These punishments scale with the softban level, becoming more severe as the level increases.

## Usage Examples

1. Set a softban level for an online player:
```
/softban set Steve 3
```
This will cause Steve to take 30% more damage, deal 30% less damage, and move 30% slower.

2. Set a softban level for an offline player:
```
/softban set Alex 2
```

(You'll need to confirm this action within 15 seconds)

3. Remove a softban:
```
   /softban remove Steve
```

## Permissions

- `softban.admin`: Allows use of all softban commands

## Confirmation for Offline Players

When setting or removing a softban for an offline player, you'll need to confirm the action:

- For in-game players: Click the confirmation message in chat
- For console: Run the exact same command again within 15 seconds

## Notes for Developers

- The main plugin class is `Softban`
- The command handler is implemented in `SoftbanCommand`
- Ensure you have methods `handleOnlinePlayer` and `handleOfflinePlayer` in your `Softban` class to process the softban actions

## Support

If you encounter any issues or have questions, please open an issue on our GitHub repository or contact the plugin author.

## License

MIT