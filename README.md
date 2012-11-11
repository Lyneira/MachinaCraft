MachinaCraft
============

MachinaCraft is a framework for machines made up out of blocks, activated by
right-clicking on a lever. On its own it does nothing, but other plugins can use
it to let players build a Machina that they can activate.

General information about the plugin, comments page and issue tracker can be
found at the [BukkitDev project page][project].

[project]: http://dev.bukkit.org/server-mods/machinacraft

Getting the Source
------------------

You'll need some knowledge of how to create a bukkit plugin with Eclipse.
If you're new to plugin writing, the Bukkit wiki has a [tutorial][] that's a
good starting point.

[tutorial]: http://wiki.bukkit.org/Plugin_Tutorial

If you've downloaded the project as a zip, do the following:

File -> Import... -> General -> Existing Projects into Workspace

If you're getting the source from Github:

File -> Import -> Git -> Projects from Git

Once you've imported the project, create a new Java project and select the
MachinaCore folder inside your project folder. Don't forget to add your
bukkit.jar to the build path.

Repeat this process for each plugin folder you want to work on, and also add
the MachinaCore project to the build path for sub-plugins.

Eclipse should automatically compile each plugin when it's imported.

Building the Plugins
--------------------
A rudimentary ant build system is included that can take care of packaging the
plugins into jarfiles. In the MachinaCraft project, rightclick on build.xml and
select Run As -> Ant Build (without the ...)

The jar files will appear in a 'jar' folder under the MachinaCraft root folder.
Advanced: Create a new builder for the MachinaCraft project under properties so
that you can just select the project and in the top menu choose Project ->
 Build Project.

Creating a new Machina Plugin
-----------------------------
When you've created a new java plugin project, make sure you add MachinaCore to
the build path. For an example on how to make a working Machina plugin you can
look at the source code for the existing plugins.