# Uproot
Uproot is a dirt-simple veinminer mod for Minecraft 1.16.4. It was made when I couldn't find another mod to add this functionality, so I decided to do it myself.
Uproot is *incredibly* simple, with a minimal configuration interface to make it as hassle-free as possible.

## Usage
When breaking a block, hold the sneak key. If the tool being used is on the tool list, and the block being mined is on the block list, and the tool would normally be effective on the block, the block and all connected blocks of the same time will be broken, and XP will be dropped if applicable. Tools will also take damage as appropriate. 

## Commands
 - /uproot reload - Reloads the current config file for Uproot
 - /uproot blocklist - View the current list of blocks that can be veinmined
 - /uproot toollist - View the current list of tools that can veinmine
 - /uproot blocklist add/remove <block> - Add or remove a block from the blocklist and update the config
 - /uproot toolist add/remove <tool> - Add or remove a tool from the toollist and update the config
 - /uproot maxsize - View the maximum vein size 
 - /uproot maxsize <max vein size> - Update the maximum number of blocks that can be mined at once

## Known Bugs
 - Running the add/remove commands without including the item namespace results in an error in chat. However, for items in the ``minecraft:`` namespace, the addition/removal should still be a success.

## Support
Because Uproot tries to determine if a tool should work on a block dynamically, it may not work on certain blocks. If this is the case, open an issue and I'll get to it ASAP.
I don't have any plans to update Uproot to later Minecraft versions at the time, but if people end up enjoying the mod then it I'll consider it.
