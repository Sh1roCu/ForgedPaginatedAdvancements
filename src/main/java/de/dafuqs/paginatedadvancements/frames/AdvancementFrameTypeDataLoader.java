package de.dafuqs.paginatedadvancements.frames;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import de.dafuqs.paginatedadvancements.client.PaginatedAdvancementFrame;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class AdvancementFrameTypeDataLoader extends JsonReloadListener {

    public static final String LOCATION = "advancement_frame_types";
    public static final ResourceLocation ID = PaginatedAdvancementsClient.locate(LOCATION);
    public static final AdvancementFrameTypeDataLoader INSTANCE = new AdvancementFrameTypeDataLoader();

    protected static final Map<ResourceLocation, PaginatedAdvancementFrame> FRAMES = new HashMap<>();

    public AdvancementFrameTypeDataLoader() {
        super(new Gson(), LOCATION);
    }

    @Override
    protected @Nonnull Map<ResourceLocation, JsonElement> prepare(@Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
        return super.prepare(resourceManager, profiler);
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler profilerFiller) {
        prepared.forEach((identifier, jsonElement) -> {
            JsonObject object = jsonElement.getAsJsonObject();
            ResourceLocation textureSheet = new ResourceLocation(identifier.getNamespace(), object.get("texture_sheet").getAsString());

            for (JsonElement frameEntry : object.get("frames").getAsJsonArray()) {
                JsonObject jsonObject = frameEntry.getAsJsonObject();
                ResourceLocation name = new ResourceLocation(identifier.getNamespace(), jsonObject.get("name").getAsString());
                int u = jsonObject.get("x").getAsInt();
                int v = jsonObject.get("y").getAsInt();
                int itemOffsetX = JSONUtils.getAsInt(jsonObject, "item_offset_x", 0);
                int itemOffsetY = JSONUtils.getAsInt(jsonObject, "item_offset_y", 0);
                String formattingString = JSONUtils.getAsString(jsonObject, "formatting", "green");
                TextFormatting formatting = TextFormatting.getByName(formattingString);

                if (formatting == null) {
                    // green is the vanilla default for most AdvancementFrames
                    PaginatedAdvancementsClient.LOGGER.error("Formatting for frame '" + name + "' is invalid: '" + formattingString + "'. Will use default 'green'");
                    formatting = TextFormatting.GREEN;
                }

                PaginatedAdvancementFrame frame = new PaginatedAdvancementFrame(name, textureSheet, u, v, itemOffsetX, itemOffsetY, formatting);
                FRAMES.put(name, frame);
            }
        });
    }


    public static @Nullable PaginatedAdvancementFrame getFrameForAdvancement(ResourceLocation id) {
        return FRAMES.getOrDefault(id, null);
    }
}