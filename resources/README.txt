== Installation ==
* Place MachinaCore.jar and the .jar files for the plugins you would like in
your server's plugin folder.
* Servers without permissions:
  Add the contents of permissions.yml to your server's permissions.yml to allow
  all players access.
* Servers with a permissions plugin:
  You can use the above step to conveniently control access to all basic
  machinacraft functionality via the 'machinacraft' permission. Then, either:
  * Be sure to set the default to 'op' in permissions.yml
  * Exclude the 'machinacraft' permission from players you don't want to have
    access.

== MachinaRedstoneBridge ==
This plugin allows a redstone signal to activate a machina. If you do not
particularly want this feature, it's recommended not to install it.

If you do install it, here's what you need to know: Because it's not possible
to determine the player who activated the machina, this plugin creates a fake
player to be its owner.

The fake player will only have the permissions that are defined in the plugin's
config.yml. This would need to include permissions to access inventories, break
blocks and place blocks if block or inventory protection plugins are used.

== Help ==
For additional help, see the project's main page at:

http://dev.bukkit.org/server-mods/machinacraft/
