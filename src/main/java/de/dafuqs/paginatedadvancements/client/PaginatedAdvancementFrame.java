package de.dafuqs.paginatedadvancements.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PaginatedAdvancementFrame {

/*    public static final Codec<PaginatedAdvancementFrame> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.intRange(-16, 16).optionalFieldOf("item_offset_x", 0).forGetter(PaginatedAdvancementFrame::getItemOffsetX),
            Codec.intRange(-16, 16).optionalFieldOf("item_offset_y", 0).forGetter(PaginatedAdvancementFrame::getItemOffsetY),
            Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY.applyFormat(ChatFormatting.GREEN)).forGetter(PaginatedAdvancementFrame::getTitleStyle)
    ).apply(instance, PaginatedAdvancementFrame::new));*/

    private final ResourceLocation id;
    protected final int itemOffsetX;
    protected final int itemOffsetY;
    // protected final Style titleStyle;
    private final ChatFormatting titleFormat;
    protected Component toastText;
    protected ResourceLocation textureObtained;
    protected ResourceLocation textureUnobtained;

    public PaginatedAdvancementFrame(ResourceLocation id, ResourceLocation textureObtained, ResourceLocation textureUnobtained, int itemOffsetX, int itemOffsetY, /*Style titleStyle*/ ChatFormatting formatting) {
        this.id = id;
        this.textureObtained = textureObtained; //ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "advancements/" + id.getPath() + "_obtained");
        this.textureUnobtained = textureUnobtained; //ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "advancements/" + id.getPath() + "_unobtained");
        this.itemOffsetX = itemOffsetX;
        this.itemOffsetY = itemOffsetY;
        //this.titleStyle = titleStyle;
        this.titleFormat = formatting;
        this.toastText = Component.translatable("advancements.toast." + id);
    }

/*    public void setIdBasedData(ResourceLocation id) {
        this.toastText = Component.translatable("advancements.toast." + id);
        this.textureObtained = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "advancements/" + id.getPath() + "_obtained");
        this.textureUnobtained = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "advancements/" + id.getPath() + "_unobtained");
    }*/

    public ResourceLocation getId() {
        return id;
    }

    public ResourceLocation getTextureObtained() {
        return this.textureObtained;
    }

    public ResourceLocation getTextureUnobtained() {
        return this.textureUnobtained;
    }

  /*  public Style getTitleStyle() {
        return this.titleStyle;
    }*/

    public ChatFormatting getFormatting() {
        return titleFormat;
    }

    public Component getToastText() {
        return this.toastText;
    }

    public int getItemOffsetX() {
        return this.itemOffsetX;
    }

    public int getItemOffsetY() {
        return this.itemOffsetY;
    }

}
