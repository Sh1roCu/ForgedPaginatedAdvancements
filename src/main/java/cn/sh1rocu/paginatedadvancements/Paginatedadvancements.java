package cn.sh1rocu.paginatedadvancements;

import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = PaginatedAdvancementsClient.MOD_ID, dist = Dist.CLIENT)
public class PaginatedAdvancements {
    public PaginatedAdvancements(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(PaginatedAdvancementsClient::registerResources);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        PaginatedAdvancementsClient.onInitializeClient();
    }
}