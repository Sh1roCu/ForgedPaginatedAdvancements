package de.dafuqs.paginatedadvancements;

import de.dafuqs.paginatedadvancements.config.PaginatedAdvancementsConfig;
import de.dafuqs.paginatedadvancements.frames.AdvancementFrameDataLoader;
import de.dafuqs.paginatedadvancements.frames.AdvancementFrameTypeDataLoader;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PaginatedAdvancementsClient {

    public static final Logger LOGGER = LoggerFactory.getLogger("PaginatedAdvancements");
    public static final String MOD_ID = "paginatedadvancements";

    public static ConfigManager<PaginatedAdvancementsConfig> CONFIG_MANAGER;
    public static PaginatedAdvancementsConfig CONFIG;

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ResourceLocation locate(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void registerResources(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(AdvancementFrameTypeDataLoader.INSTANCE);
        event.registerReloadListener(AdvancementFrameDataLoader.INSTANCE);
    }

    public static void saveSelectedTab(ResourceLocation tabResourceLocation) {
        if (CONFIG.SaveLastSelectedTab) {
            CONFIG.LastSelectedTab = tabResourceLocation.toString();
            CONFIG_MANAGER.save();
        }
    }

    public static void pinTab(ResourceLocation tabResourceLocation) {
        String identifierString = tabResourceLocation.toString();
        if (!CONFIG.PinnedTabs.contains(identifierString)) {
            CONFIG.PinnedTabs.add(identifierString);
            CONFIG_MANAGER.save();
        }
    }

    public static void unpinTab(ResourceLocation tabResourceLocation) {
        String identifierString = tabResourceLocation.toString();
        if (CONFIG.PinnedTabs.contains(identifierString)) {
            CONFIG.PinnedTabs.remove(identifierString);
            CONFIG_MANAGER.save();
        }
    }

    public static boolean isPinned(ResourceLocation tabResourceLocation) {
        return CONFIG.PinningEnabled && CONFIG.PinnedTabs.contains(tabResourceLocation.toString());
    }

    public static boolean hasPins() {
        return CONFIG.PinningEnabled && !CONFIG.PinnedTabs.isEmpty();
    }

    public static List<String> getPinnedTabs() {
        return CONFIG.PinnedTabs;
    }

    public static int getPinIndex(ResourceLocation tabResourceLocation) {
        return CONFIG.PinnedTabs.indexOf(tabResourceLocation.toString());
    }

}
