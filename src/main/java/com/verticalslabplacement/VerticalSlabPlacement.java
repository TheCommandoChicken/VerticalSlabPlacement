package com.verticalslabplacement;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticalSlabPlacement implements ModInitializer {
	public static final String MOD_ID = "assets/vertical_slab_placement";
    public static final Logger LOGGER = LoggerFactory.getLogger("assets/vertical_slab_placement");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}
