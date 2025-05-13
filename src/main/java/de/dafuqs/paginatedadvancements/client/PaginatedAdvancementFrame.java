package de.dafuqs.paginatedadvancements.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class PaginatedAdvancementFrame {

/*    public static final Codec<PaginatedAdvancementFrame> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.intRange(-16, 16).optionalFieldOf("item_offset_x", 0).forGetter(PaginatedAdvancementFrame::getItemOffsetX),
            Codec.intRange(-16, 16).optionalFieldOf("item_offset_y", 0).forGetter(PaginatedAdvancementFrame::getItemOffsetY),
            Style.Serializer.CODEC.optionalFieldOf("style", Style.EMPTY.applyFormat(TextFormatting.GREEN)).forGetter(PaginatedAdvancementFrame::getTitleStyle)
    ).apply(instance, PaginatedAdvancementFrame::new));*/

    private final ResourceLocation id;
    private final ResourceLocation textureSheet;
    private final int textureV;
    private final int textureU;
    protected final int itemOffsetX;
    protected final int itemOffsetY;
    // protected final Style titleStyle;
    private final TextFormatting titleFormat;
    protected ITextComponent toastText;
    //protected ResourceLocation textureObtained;
    //protected ResourceLocation textureUnobtained;

    public PaginatedAdvancementFrame(ResourceLocation id, ResourceLocation textureSheet, int texU, int texV, int itemOffsetX, int itemOffsetY, /*Style titleStyle*/ TextFormatting formatting) {
        this.id = id;
        //this.textureObtained = textureObtained; //ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "advancements/" + id.getPath() + "_obtained");
        //this.textureUnobtained = textureUnobtained; //ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "advancements/" + id.getPath() + "_unobtained");
        this.textureSheet = textureSheet;
        this.textureU = texU;
        this.textureV = texV;
        this.itemOffsetX = itemOffsetX;
        this.itemOffsetY = itemOffsetY;
        //this.titleStyle = titleStyle;
        this.titleFormat = formatting;
        this.toastText = new TranslationTextComponent("advancements.toast." + id);
    }

/*    public void setIdBasedData(ResourceLocation id) {
        this.toastText = Component.translatable("advancements.toast." + id);
        this.textureObtained = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "advancements/" + id.getPath() + "_obtained");
        this.textureUnobtained = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "advancements/" + id.getPath() + "_unobtained");
    }*/

    public ResourceLocation getId() {
        return id;
    }

/*    public ResourceLocation getTextureObtained() {
        return this.textureObtained;
    }*/

/*
    public ResourceLocation getTextureUnobtained() {
        return this.textureUnobtained;
    }
*/

  /*  public Style getTitleStyle() {
        return this.titleStyle;
    }*/

    public ResourceLocation getTextureSheet() {
        return this.textureSheet;
    }

    public int getTextureU() {
        return this.textureU;
    }

    public int getTextureV() {
        return this.textureV;
    }

    public TextFormatting getFormatting() {
        return titleFormat;
    }

    public ITextComponent getToastText() {
        return this.toastText;
    }

    public int getItemOffsetX() {
        return this.itemOffsetX;
    }

    public int getItemOffsetY() {
        return this.itemOffsetY;
    }

}
