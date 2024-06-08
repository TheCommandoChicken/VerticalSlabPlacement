package com.verticalslabplacement.util;

import net.minecraft.util.StringIdentifiable;

public enum ModAxis implements StringIdentifiable {Y, X, Z;

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
