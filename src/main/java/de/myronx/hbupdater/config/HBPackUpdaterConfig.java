package de.myronx.hbupdater.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HBPackUpdaterConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().resolve("hbupdater.json").toString());
    public boolean enableUpdates = true;
    private static final HBPackUpdaterConfig INSTANCE = new HBPackUpdaterConfig();

    public static HBPackUpdaterConfig getInstance() {
        return INSTANCE;
    }

    public void load() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                HBPackUpdaterConfig config = GSON.fromJson(reader, HBPackUpdaterConfig.class);
                this.enableUpdates = config.enableUpdates;
            } catch (IOException | JsonIOException e) {
                e.printStackTrace();
            }
        } else {
            save(); // Create default config if file does not exist
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}