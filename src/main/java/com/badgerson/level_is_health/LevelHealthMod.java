package com.badgerson.level_is_health;

import net.fabricmc.api.ModInitializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class LevelHealthMod implements ModInitializer {
    public static final String MOD_ID = "level_is_health";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static LevelHealthConfig config = new LevelHealthConfig();

    private static String CONFIG_FILE = "config/level-is-health.json";

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.debug("Loading config...");

        File f = new File(CONFIG_FILE);
        if (f.exists() && !f.isDirectory()) {
            try {
                var reader = new FileReader(f);
                var gson = new Gson();
                config = gson.fromJson(reader, LevelHealthConfig.class);
                reader.close();
            } catch (Exception ex) {
                LOGGER.error("Error loading config: " + ex.toString());
            }
        }

        // now save the config..
        try {
            var writer = new FileWriter(CONFIG_FILE);
            var gson = new Gson();
            writer.write(gson.toJson(config));
            writer.close();
        } catch (Exception ex) {
            LOGGER.error("Error writing config: " + ex.toString());
        }
    }
}
