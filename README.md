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

There's no build system included for now, so you'll need to import it as a
project into Eclipse. Add a current bukkit snapshot jar as an external library,
then right click on your project and choose Export... -> Java -> Jar File.
Make sure only the plugin.yml and src items are ticked, choose your destination
file and click Finish!

Developing a Machina Plugin
---------------------------

Add MachinaCraft as a project to your Eclipse workspace, then right click on
your new plugin and choose Properties. Go to Java Build Path -> Projects tab,
and add MachinaCraft to the list here.