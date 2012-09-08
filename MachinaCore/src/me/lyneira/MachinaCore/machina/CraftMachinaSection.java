package me.lyneira.MachinaCore.machina;

/**
 * Implementing class for MachinaSection. Sections have an offset (defaulting to 0,0,0) from
 * their parent section, so that different parts of a machina can move in
 * relation to each other. If the offset of a section changes, it will
 * change the effective location of all the blocks that fall under it, including
 * those belonging to child sections.
 * 
 * @author Lyneira
 */
class CraftMachinaSection implements MachinaSection {
    // An offset from the parent.
}
