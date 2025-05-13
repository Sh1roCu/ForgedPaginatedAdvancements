package de.dafuqs.paginatedadvancements.frames;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class AdvancementFrameDataLoader extends JsonReloadListener {

    public static final String LOCATION = "advancement_frames";
    public static final ResourceLocation ID = PaginatedAdvancementsClient.locate(LOCATION);
    public static final AdvancementFrameDataLoader INSTANCE = new AdvancementFrameDataLoader();

    protected static final Map<ResourceLocation, FrameWrapper> ADVANCEMENT_FRAMES = new HashMap<>();

    public AdvancementFrameDataLoader() {
        super(new Gson(), LOCATION);
    }

    @Override
    protected @Nonnull Map<net.minecraft.util.ResourceLocation, JsonElement> prepare(@Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
        return super.prepare(resourceManager, profiler);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler profilerFiller) {
        prepared.forEach((identifier, jsonElement) -> {
            for (JsonElement entry : jsonElement.getAsJsonArray()) {
                JsonObject jsonObject = entry.getAsJsonObject();
                ResourceLocation advancement = ResourceLocation.tryParse(jsonObject.get("advancement").getAsString());
                ResourceLocation frame = ResourceLocation.tryParse(jsonObject.get("frame").getAsString());

                @Nullable FrameWrapper frameWrapper = FrameWrapper.of(frame);
                if (frameWrapper == null) {
                    PaginatedAdvancementsClient.LOGGER.error("Advancement Frame '" + frame + "' for advancement  '" + advancement + "' is unknown");
                } else {
                    ADVANCEMENT_FRAMES.put(advancement, frameWrapper);
                }
            }
        });
    }

    public static @Nullable FrameWrapper get(ResourceLocation id) {
        return ADVANCEMENT_FRAMES.getOrDefault(id, null);
    }
}