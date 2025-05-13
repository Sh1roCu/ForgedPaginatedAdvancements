package de.dafuqs.paginatedadvancements;

import de.dafuqs.paginatedadvancements.config.PaginatedAdvancementsConfig;
import de.dafuqs.paginatedadvancements.frames.AdvancementFrameDataLoader;
import de.dafuqs.paginatedadvancements.frames.AdvancementFrameTypeDataLoader;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

public class PaginatedAdvancementsClient {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "paginatedadvancements";

    public static ConfigManager<PaginatedAdvancementsConfig> CONFIG_MANAGER;
    public static PaginatedAdvancementsConfig CONFIG;

    public static @Nonnull ResourceLocation locate(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static void onInitializeClient() {
        ConfigHolder<PaginatedAdvancementsConfig> configHolder = AutoConfig.register(PaginatedAdvancementsConfig.class, JanksonConfigSerializer::new);
        CONFIG_MANAGER = ((ConfigManager<PaginatedAdvancementsConfig>) configHolder);
        CONFIG = AutoConfig.getConfigHolder(PaginatedAdvancementsConfig.class).getConfig();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, screen) ->
                AutoConfig.getConfigScreen(PaginatedAdvancementsConfig.class, screen).get());
    }

    public static void registerResources() {
        IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
        manager.registerReloadListener(AdvancementFrameTypeDataLoader.INSTANCE);
        manager.registerReloadListener(AdvancementFrameDataLoader.INSTANCE);
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
