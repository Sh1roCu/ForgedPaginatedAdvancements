package de.dafuqs.paginatedadvancements.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class PinnedAdvancementTabType {

    public static final int TOP_SPACING = 24; // accounting for the "pin" ribbon
    public static final int WIDTH = 32;
    public static final int HEIGHT = 28;

   /* protected static final ResourceLocation RIGHT_TOP_TEXTURE_SELECTED = ResourceLocation.withDefaultNamespace("advancements/tab_right_top_selected");
    protected static final ResourceLocation RIGHT_MIDDLE_TEXTURE_SELECTED = ResourceLocation.withDefaultNamespace("advancements/tab_right_middle_selected");
    protected static final ResourceLocation RIGHT_TOP_TEXTURE = ResourceLocation.withDefaultNamespace("advancements/tab_right_top");
    protected static final ResourceLocation RIGHT_MIDDLE_TEXTURE = ResourceLocation.withDefaultNamespace("advancements/tab_right_middle");*/

    public static int getHeightWithSpacing() {
        return HEIGHT + PaginatedAdvancementsClient.CONFIG.SpacingBetweenPinnedTabs; // includes the empty space between tabs
    }

    public static int getWidthWithSpacing() {
        return WIDTH + PaginatedAdvancementsClient.CONFIG.SpacingBetweenHorizontalTabs; // includes the empty space between tabs
    }

    public static void drawBackground(MatrixStack matrices, AbstractGui tab, int x, int y, boolean selected, int index) {
        int i = index > 0 ? WIDTH + 96 : 96;
        int j = selected ? 64 + HEIGHT : 64;
        tab.blit(matrices, x + getTabX(), y + getTabY(index), i, j, WIDTH, HEIGHT);
    }

    public static void drawIcon(int x, int y, int index, @Nonnull ItemRenderer itemRenderer, ItemStack icon) {
        int i = x + getTabX() + 6;
        int j = y + getTabY(index) + 5;
        itemRenderer.renderGuiItem(icon, i, j);
    }


    public static int getTabX() {
        return WIDTH - PaginatedAdvancementScreen.BORDER_PADDING - 4;
    }

    public static int getTabY(int index) {
        return TOP_SPACING + getHeightWithSpacing() * index;
    }

    public static boolean isClickOnTab(int screenX, int screenY, int index, double mouseX, double mouseY) {
        int i = screenX + getTabX();
        int j = screenY + getTabY(index);
        return mouseX > (double) i && mouseX < (double) (i + WIDTH) && mouseY > (double) j && mouseY < (double) (j + HEIGHT);
    }

}
