package de.dafuqs.paginatedadvancements.frames;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import de.dafuqs.paginatedadvancements.client.PaginatedAdvancementFrame;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AdvancementFrameTypeDataLoader extends SimpleJsonResourceReloadListener {

    public static final String LOCATION = "advancement_frame_types";
    public static final ResourceLocation ID = PaginatedAdvancementsClient.locate(LOCATION);
    public static final AdvancementFrameTypeDataLoader INSTANCE = new AdvancementFrameTypeDataLoader();

    protected static final Map<ResourceLocation, PaginatedAdvancementFrame> FRAMES = new HashMap<>();

    public AdvancementFrameTypeDataLoader() {
        super(new Gson(), LOCATION);
    }

    @Override
    protected @NotNull Map<ResourceLocation, JsonElement> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        return super.prepare(resourceManager, profiler);
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        prepared.forEach((identifier, jsonElement) -> {
            JsonObject object = jsonElement.getAsJsonObject();

            for (JsonElement frameEntry : object.get("frames").getAsJsonArray()) {
                JsonObject jsonObject = frameEntry.getAsJsonObject();


                String name = jsonObject.get("name").getAsString();
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), name);
                int itemOffsetX = GsonHelper.getAsInt(jsonObject, "item_offset_x", 0);
                int itemOffsetY = GsonHelper.getAsInt(jsonObject, "item_offset_y", 0);
                String formattingString = GsonHelper.getAsString(jsonObject, "formatting", "green");
                ChatFormatting formatting = ChatFormatting.getByName(formattingString);

                if (formatting == null) {
                    // green is the vanilla default for most AdvancementFrames
                    PaginatedAdvancementsClient.LOGGER.error("Formatting for frame '{}' is invalid: '{}'. Will use default 'green'", id, formattingString);
                    formatting = ChatFormatting.GREEN;
                }

                ResourceLocation textureObtained = ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), "advancements/" + name + "_obtained");
                ResourceLocation textureUnobtained = ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), "advancements/" + name + "_unobtained");

                PaginatedAdvancementFrame frame = new PaginatedAdvancementFrame(id, textureObtained, textureUnobtained, itemOffsetX, itemOffsetY, formatting);
                FRAMES.put(id, frame);
            }
        });
    }


    public static @Nullable PaginatedAdvancementFrame getFrameForAdvancement(ResourceLocation id) {
        return FRAMES.getOrDefault(id, null);
    }
}