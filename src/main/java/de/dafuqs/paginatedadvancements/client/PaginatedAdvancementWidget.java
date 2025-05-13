package de.dafuqs.paginatedadvancements.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.paginatedadvancements.frames.AdvancementFrameDataLoader;
import de.dafuqs.paginatedadvancements.frames.FrameWrapper;
import de.dafuqs.paginatedadvancements.mixin.AdvancementWidgetAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import net.minecraft.client.gui.advancements.AdvancementState;
import net.minecraft.client.gui.advancements.AdvancementTabGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class PaginatedAdvancementWidget extends AdvancementEntryGui {

    private static final ResourceLocation VANILLA_WIDGETS_TEXTURE = new ResourceLocation("textures/gui/advancements/widgets.png");
    protected List<IReorderingProcessor> description;

    protected @Nullable FrameWrapper frameWrapper;
    private int debugScrollAmount;

    public PaginatedAdvancementWidget(AdvancementTabGui tab, Minecraft client, Advancement advancement, DisplayInfo display) {
        super(tab, client, advancement, display);

        frameWrapper = AdvancementFrameDataLoader.get(((AdvancementWidgetAccessor) this).getAdvancement().getId());
        FrameWrapper frameWrapper = AdvancementFrameDataLoader.get(advancement.getId());
        if (frameWrapper instanceof FrameWrapper.PaginatedFrameWrapper) {
            int requirementCount = advancement.getMaxCriteraRequired();
            int k = requirementCount > 1 ? client.font.width("  ") + client.font.width("0") * String.valueOf(requirementCount).length() * 2 + client.font.width("/") : 0;
            IReorderingProcessor title = LanguageMap.getInstance().getVisualOrder(client.font.substrByWidth(display.getTitle(), 163));
            int l = 29 + client.font.width(title) + k;
            this.description = LanguageMap.getInstance().getVisualOrder(((AdvancementWidgetAccessor) this).invokeWrapDescription(TextComponentUtils.mergeStyles(display.getDescription().copy(), Style.EMPTY.withColor(frameWrapper.getFormatting())), l));
            IReorderingProcessor orderedText;
            for (Iterator<IReorderingProcessor> var9 = this.description.iterator(); var9.hasNext(); l = Math.max(l, client.font.width(orderedText))) {
                orderedText = var9.next();
            }
        }
    }

    @Override
    public void draw(@Nonnull MatrixStack matrixStack, int x, int y) {
        AdvancementWidgetAccessor accessor = (AdvancementWidgetAccessor) this;

        if (!accessor.getDisplay().isHidden() || accessor.getProgress() != null && accessor.getProgress().isDone()) {
            float f = accessor.getProgress() == null ? 0.0F : accessor.getProgress().getPercent();
            AdvancementState advancementObtainedStatus;
            if (f >= 1.0D) {
                advancementObtainedStatus = AdvancementState.OBTAINED;
            } else {
                advancementObtainedStatus = AdvancementState.UNOBTAINED;
            }

            ResourceLocation advancementID = accessor.getAdvancement().getId();
            @Nullable FrameWrapper frameWrapper = AdvancementFrameDataLoader.get(advancementID);
            if (frameWrapper == null) {
                Minecraft.getInstance().getTextureManager().bind(VANILLA_WIDGETS_TEXTURE);
                blit(matrixStack, x + accessor.getX() + 3, y + accessor.getY(), accessor.getDisplay().getFrame().getTexture(), 128 + advancementObtainedStatus.getIndex() * 26, 26, 26);
                matrixStack.pushPose();
                RenderSystem.pushMatrix();
                RenderSystem.multMatrix(matrixStack.last().pose());
                RenderHelper.turnBackOn();
                Minecraft.getInstance().getItemRenderer().renderGuiItem(accessor.getDisplay().getIcon(), x + accessor.getX() + 8, y + accessor.getY() + 5);
                RenderHelper.turnOff();
                RenderSystem.popMatrix();
                matrixStack.popPose();
            } else {
                Minecraft.getInstance().getTextureManager().bind(frameWrapper.getTextureSheet());
                blit(matrixStack, x + accessor.getX() + 3, y + accessor.getY(), frameWrapper.getTextureU(), frameWrapper.getTextureV() + advancementObtainedStatus.getIndex() * 26, 26, 26);
                matrixStack.pushPose();
                RenderSystem.pushMatrix();
                RenderSystem.multMatrix(matrixStack.last().pose());
                RenderHelper.turnBackOn();
                Minecraft.getInstance().getItemRenderer().renderGuiItem(accessor.getDisplay().getIcon(), x + accessor.getX() + 8 + frameWrapper.getItemOffsetX(), y + accessor.getY() + 5 + frameWrapper.getItemOffsetY());
                RenderHelper.turnOff();
                RenderSystem.popMatrix();
                matrixStack.popPose();
            }
        }

        for (AdvancementEntryGui advancementWidget : accessor.getChildren()) {
            advancementWidget.draw(matrixStack, x, y);
        }
    }

    @Override
    public void drawHover(@Nonnull MatrixStack matrixStack, int originX, int originY, float alpha, int x, int y) {
        AdvancementWidgetAccessor accessor = (AdvancementWidgetAccessor) this;
        FontRenderer textRenderer = Minecraft.getInstance().font;

        List<IReorderingProcessor> description = this.description == null ? accessor.getDescription() : this.description;

        boolean shouldRenderToTheLeft = x + originX + accessor.getX() + accessor.getWidth() + 26 >= accessor.getTab().getScreen().width;
        String string = accessor.getProgress() == null ? null : accessor.getProgress().getProgressText();
        int i = string == null ? 0 : textRenderer.width(string);
        int var10000 = 113 - originY - accessor.getY() - 26;
        int var10002 = description.size();

        boolean bl2 = var10000 <= 6 + var10002 * 9;
        float f = accessor.getProgress() == null ? 0.0F : accessor.getProgress().getPercent();
        int j = MathHelper.floor(f * (float) accessor.getWidth());
        AdvancementState advancementObtainedStatus;
        AdvancementState advancementObtainedStatus2;
        AdvancementState advancementObtainedStatus3;
        if (f >= 1.0D) {
            j = accessor.getWidth() / 2;
            advancementObtainedStatus = AdvancementState.OBTAINED;
            advancementObtainedStatus2 = AdvancementState.OBTAINED;
            advancementObtainedStatus3 = AdvancementState.OBTAINED;
        } else if (j < 2) {
            j = accessor.getWidth() / 2;
            advancementObtainedStatus = AdvancementState.UNOBTAINED;
            advancementObtainedStatus2 = AdvancementState.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementState.UNOBTAINED;
        } else if (j > accessor.getWidth() - 2) {
            j = accessor.getWidth() / 2;
            advancementObtainedStatus = AdvancementState.OBTAINED;
            advancementObtainedStatus2 = AdvancementState.OBTAINED;
            advancementObtainedStatus3 = AdvancementState.UNOBTAINED;
        } else {
            advancementObtainedStatus = AdvancementState.OBTAINED;
            advancementObtainedStatus2 = AdvancementState.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementState.UNOBTAINED;
        }

        int k = accessor.getWidth() - j;
        Minecraft.getInstance().getTextureManager().bind(VANILLA_WIDGETS_TEXTURE);
        RenderSystem.enableBlend();
        int l = originY + accessor.getY();
        int startX;
        if (shouldRenderToTheLeft) {
            startX = originX + accessor.getX() - accessor.getWidth() + 26 + 6;
        } else {
            startX = originX + accessor.getX();
        }

        int n = 32 + description.size() * 9;
        if (!description.isEmpty()) {
            if (bl2) {
                render9Sprite(matrixStack, startX, l + 26 - n, accessor.getWidth(), n, 10, 200, 26, 0, 52);
            } else {
                render9Sprite(matrixStack, startX, l, accessor.getWidth(), n, 10, 200, 26, 0, 52);
            }
        }

        blit(matrixStack, startX, l, 0, advancementObtainedStatus.getIndex() * 26, j, 26);
        blit(matrixStack, startX + j, l, 200 - k, advancementObtainedStatus2.getIndex() * 26, k, 26);

        if (this.frameWrapper == null) {
            blit(matrixStack, originX + accessor.getX() + 3, originY + accessor.getY(), accessor.getDisplay().getFrame().getTexture(), 128 + advancementObtainedStatus3.getIndex() * 26, 26, 26);
        } else {
            Minecraft.getInstance().getTextureManager().bind(this.frameWrapper.getTextureSheet());
            blit(matrixStack, originX + accessor.getX() + 3, originY + accessor.getY(), this.frameWrapper.getTextureU(), this.frameWrapper.getTextureV() + advancementObtainedStatus3.getIndex() * 26, 26, 26);
            Minecraft.getInstance().getTextureManager().bind(VANILLA_WIDGETS_TEXTURE);
        }

        if (shouldRenderToTheLeft) {
            textRenderer.drawShadow(matrixStack, accessor.getTitle(), (float) (startX + 5), (float) (originY + accessor.getY() + 9), -1);
            if (string != null) {
                textRenderer.drawShadow(matrixStack, string, (float) (originX + accessor.getX() - i), (float) (originY + accessor.getY() + 9), -1);
            }
        } else {
            textRenderer.drawShadow(matrixStack, accessor.getTitle(), (float) (originX + accessor.getX() + 32), (float) (originY + accessor.getY() + 9), -1);
            if (string != null) {
                textRenderer.drawShadow(matrixStack, string, (float) (originX + accessor.getX() + accessor.getWidth() - i - 5), (float) (originY + accessor.getY() + 9), -1);
            }
        }

        float var10003;
        int o;
        int var10004;
        FontRenderer var21;
        IReorderingProcessor var22;
        if (bl2) {
            for (o = 0; o < description.size(); ++o) {
                var21 = textRenderer;
                var22 = description.get(o);
                var10003 = (float) (startX + 5);
                var10004 = l + 26 - n + 7;
                Objects.requireNonNull(textRenderer);
                var21.draw(matrixStack, var22, var10003, (float) (var10004 + o * 9), -5592406);
            }
        } else {
            for (o = 0; o < description.size(); ++o) {
                var21 = textRenderer;
                var22 = description.get(o);
                var10003 = (float) (startX + 5);
                var10004 = originY + accessor.getY() + 9 + 17;
                Objects.requireNonNull(textRenderer);
                var21.draw(matrixStack, var22, var10003, (float) (var10004 + o * 9), -5592406);
            }
        }

        if (frameWrapper == null) {
            matrixStack.pushPose();
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.last().pose());
            RenderHelper.turnBackOn();
            Minecraft.getInstance().getItemRenderer().renderGuiItem(accessor.getDisplay().getIcon(), originX + accessor.getX() + 8, originY + accessor.getY() + 5);
            RenderHelper.turnOff();
            RenderSystem.popMatrix();
            matrixStack.popPose();
        } else {
            matrixStack.pushPose();
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.last().pose());
            RenderHelper.turnBackOn();
            Minecraft.getInstance().getItemRenderer().renderGuiItem(accessor.getDisplay().getIcon(), originX + accessor.getX() + 8 + frameWrapper.getItemOffsetX(), originY + accessor.getY() + 5 + frameWrapper.getItemOffsetY());
            RenderHelper.turnOff();
            RenderSystem.popMatrix();
            matrixStack.popPose();
        }
    }

    public int getDebugScrollAmount() {
        return debugScrollAmount;
    }

    public void setDebugScrollAmount(int debugScrollAmount) {
        this.debugScrollAmount = debugScrollAmount;
    }
}
