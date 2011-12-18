MachinaCraft
============

MachinaCraft is a framework for machines made up out of blocks, activated by
right-clicking on a lever. On its own it does nothing, but other plugins can use
it to let players build a Machina that they can activate.

General information about the plugin, comments page and issue tracker can be
found at the [BukkitDev project page][project].

[project]: http://dev.bukkit.org/server-mods/machinacraft

Compiling
---------

You'll need some knowledge of how to create a bukkit plugin with Eclipse.
If you're new to plugin writing, the Bukkit wiki has a [tutorial][] that's a
good starting point.

[tutorial]: http://wiki.bukkit.org/Plugin_Tutorial

File -> Import... -> General -> Existing Projects into Workspace, select the
MachinaCraft root folder. Repeat this process for each plugin folder contained
in it. Eclipse should automatically compile each plugin when it's imported.

Building the Plugins
--------------------
A rudimentary ant build system is included that can take care of packaging the
plugins into jarfiles. In the MachinaCraft project, rightclick on build.xml and
select Run As -> Ant Build (without the ...)

The jar files will appear in a 'jar' folder under the MachinaCraft root folder.
Advanced: Create a new builder for the MachinaCraft project under properties so
that you can just select the project and in the top menu choose Project ->
 Build Project.

Developing a Machina Plugin
---------------------------
When you've created a new java plugin project, right click your new project and
choose Properties. Go to Java Build Path -> Projects tab, and add MachinaCore
to the list.