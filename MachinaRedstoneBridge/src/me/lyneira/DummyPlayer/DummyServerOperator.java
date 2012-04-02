package me.lyneira.DummyPlayer;

import org.bukkit.permissions.ServerOperator;

public class DummyServerOperator implements ServerOperator {
    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean arg0) {
    }
}
