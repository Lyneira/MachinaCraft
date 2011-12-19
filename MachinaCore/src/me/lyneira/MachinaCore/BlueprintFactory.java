package me.lyneira.MachinaCore;

import java.util.ArrayList;
import java.util.List;

public class BlueprintFactory {
    final List<ModuleFactory> modules;
    
    public BlueprintFactory(int initialCapacity) {
        modules = new ArrayList<ModuleFactory>(initialCapacity);
    }
    
    public ModuleFactory newModule() {
        ModuleFactory newModule = new ModuleFactory(modules.size());
        modules.add(newModule);
        return newModule;
    }
}
