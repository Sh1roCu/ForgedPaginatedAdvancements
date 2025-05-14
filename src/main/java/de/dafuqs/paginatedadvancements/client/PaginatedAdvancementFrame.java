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
    private final int textureV;
    private final int textureU;
    // protected final Style titleStyle;
    private final ChatFormatting titleFormat;
    protected final Component toastText;
    /*    protected ResourceLocation textureObtained;
        protected ResourceLocation textureUnobtained;*/
    private final ResourceLocation textureSheet;

    public PaginatedAdvancementFrame(ResourceLocation id, ResourceLocation textureSheet, int texU, int texV, int itemOffsetX, int itemOffsetY, /*Style titleStyle*/ ChatFormatting formatting) {
        this.id = id;
        //this.textureObtained = textureObtained; //new ResourceLocation(id.getNamespace(), "advancements/" + id.getPath() + "_obtained");
        //this.textureUnobtained = textureUnobtained; //new ResourceLocation(id.getNamespace(), "advancements/" + id.getPath() + "_unobtained");
        this.itemOffsetX = itemOffsetX;
        this.itemOffsetY = itemOffsetY;
        this.textureU = texU;
        this.textureV = texV;
        //this.titleStyle = titleStyle;
        this.titleFormat = formatting;
        this.toastText = Component.translatable("advancements.toast." + id);
        this.textureSheet = textureSheet;
    }

/*    public void setIdBasedData(ResourceLocation id) {
        this.toastText = Component.translatable("advancements.toast." + id);
        this.textureObtained = new ResourceLocation(id.getNamespace(), "advancements/" + id.getPath() + "_obtained");
        this.textureUnobtained = new ResourceLocation(id.getNamespace(), "advancements/" + id.getPath() + "_unobtained");
    }*/

    public ResourceLocation getId() {
        return id;
    }

    public ResourceLocation getTextureSheet() {
        return this.textureSheet;
    }
/*    public ResourceLocation getTextureObtained() {
        return this.textureObtained;
    }

    public ResourceLocation getTextureUnobtained() {
        return this.textureUnobtained;
    }*/

  /*  public Style getTitleStyle() {
        return this.titleStyle;
    }*/

    public int getTextureU() {
        return this.textureU;
    }

    public int getTextureV() {
        return this.textureV;
    }

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
