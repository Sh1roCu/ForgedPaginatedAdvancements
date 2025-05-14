package cn.sh1rocu.paginatedadvancements;

import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import de.dafuqs.paginatedadvancements.config.PaginatedAdvancementsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(PaginatedAdvancementsClient.MOD_ID)
public class PaginatedAdvancements {
    @SuppressWarnings("deprecation")
    public PaginatedAdvancements() {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            ModContainer modContainer = FMLJavaModLoadingContext.get().getContainer();
            ConfigHolder<PaginatedAdvancementsConfig> configHolder = AutoConfig.register(PaginatedAdvancementsConfig.class, JanksonConfigSerializer::new);
            PaginatedAdvancementsClient.CONFIG_MANAGER = ((ConfigManager<PaginatedAdvancementsConfig>) configHolder);
            PaginatedAdvancementsClient.CONFIG = AutoConfig.getConfigHolder(PaginatedAdvancementsConfig.class).getConfig();
            modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((client, screen) ->
                    AutoConfig.getConfigScreen(PaginatedAdvancementsConfig.class, screen).get()));
            bus.addListener(PaginatedAdvancementsClient::registerResources);
        }
    }
}
