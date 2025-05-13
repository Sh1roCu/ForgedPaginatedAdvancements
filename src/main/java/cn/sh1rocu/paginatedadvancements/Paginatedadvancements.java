package cn.sh1rocu.paginatedadvancements;

import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PaginatedAdvancementsClient.MOD_ID)
public class PaginatedAdvancements {
    public PaginatedAdvancements() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        PaginatedAdvancementsClient.registerResources();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        PaginatedAdvancementsClient.onInitializeClient();
    }
}
