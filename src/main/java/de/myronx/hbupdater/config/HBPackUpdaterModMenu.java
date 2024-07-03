package de.myronx.hbupdater.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HBPackUpdaterModMenu {
    public static Screen create(Screen parent) {

        HBPackUpdaterConfig config = HBPackUpdaterConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("Config"))
                .setSavingRunnable(config::save);

        ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Automatische Updates"), config.enableUpdates)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableUpdates = newValue)
                .build());

        return builder.build();

    }
}
