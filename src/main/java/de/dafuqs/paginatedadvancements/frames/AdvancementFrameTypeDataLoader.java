package de.dafuqs.paginatedadvancements.frames;

import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import de.dafuqs.paginatedadvancements.client.PaginatedAdvancementFrame;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AdvancementFrameTypeDataLoader extends SimpleJsonResourceReloadListener<PaginatedAdvancementFrame> {

    public static final String LOCATION = "advancement_frame_types";
    public static final ResourceLocation ID = PaginatedAdvancementsClient.locate(LOCATION);
    public static final AdvancementFrameTypeDataLoader INSTANCE = new AdvancementFrameTypeDataLoader();

    protected static final Map<ResourceLocation, PaginatedAdvancementFrame> ADVANCEMENT_TO_FRAME = new HashMap<>();

    public AdvancementFrameTypeDataLoader() {
        super(PaginatedAdvancementFrame.CODEC, FileToIdConverter.json(LOCATION));
    }


    @Override
    protected void apply(Map<ResourceLocation, PaginatedAdvancementFrame> prepared, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        for (Map.Entry<ResourceLocation, PaginatedAdvancementFrame> entry : prepared.entrySet()) {
            ResourceLocation id = entry.getKey();
            PaginatedAdvancementFrame frame = entry.getValue();
            frame.setIdBasedData(id);
            ADVANCEMENT_TO_FRAME.put(id, frame);
        }
    }


    public static @Nullable PaginatedAdvancementFrame getFrameForAdvancement(ResourceLocation id) {
        return ADVANCEMENT_TO_FRAME.getOrDefault(id, null);
    }
}