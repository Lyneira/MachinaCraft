package me.lyneira.MachinaFactory;

import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MachinaBlueprint;

class MachinaFactoryBlueprint {
    final MachinaBlueprint blueprint;
    final boolean validEndpoint;
    final boolean leverActivatable;
    
    MachinaFactoryBlueprint(MachinaBlueprint blueprint, Class<? extends Machina> type, boolean leverActivatable) {
        this.blueprint = blueprint;
        this.leverActivatable = leverActivatable;
        if (PipelineEndpoint.class.isAssignableFrom(type)) {
            validEndpoint = true;
        } else {
            validEndpoint = false;
        }
    }
}
