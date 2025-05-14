package de.dafuqs.paginatedadvancements.frames;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AdvancementFrameDataLoader extends SimpleJsonResourceReloadListener {

    public static final String LOCATION = "advancement_frames";
    public static final ResourceLocation ID = PaginatedAdvancementsClient.locate(LOCATION);
    public static final AdvancementFrameDataLoader INSTANCE = new AdvancementFrameDataLoader();

    protected static final Map<ResourceLocation, FrameWrapper> ADVANCEMENT_FRAMES = new HashMap<>();

    public AdvancementFrameDataLoader() {
        super(new Gson(), LOCATION);
    }

    @Override
    protected @NotNull Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        return super.prepare(resourceManager, profiler);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
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