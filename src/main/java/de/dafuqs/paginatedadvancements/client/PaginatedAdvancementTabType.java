package de.dafuqs.paginatedadvancements.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class PaginatedAdvancementTabType {

    public static final int WIDTH = 28;
    public static final int HEIGHT = 32;

    /* protected static final ResourceLocation TOP_LEFT_TEXTURE_SELECTED = new ResourceLocation("advancements/tab_above_left_selected");
     protected static final ResourceLocation TOP_MIDDLE_TEXTURE_SELECTED = new ResourceLocation("advancements/tab_above_middle_selected");
     protected static final ResourceLocation TOP_LEFT_TEXTURE = new ResourceLocation("advancements/tab_above_left");
     protected static final ResourceLocation TOP_MIDDLE_TEXTURE = new ResourceLocation("advancements/tab_above_middle");
 */
    public static int getWidthWithSpacing() {
        return WIDTH + PaginatedAdvancementsClient.CONFIG.SpacingBetweenHorizontalTabs; // includes the empty space between tabs
    }

    public static void drawBackground(MatrixStack matrices, AbstractGui tab, int x, int y, boolean selected, int index) {
        int i = index > 0 ? WIDTH : 0;
        int j = selected ? HEIGHT : 0;
        tab.blit(matrices, x + getTabX(index), y + getTabY(), i, j, WIDTH, HEIGHT);
    }

    public static void drawIcon(int x, int y, int index, @Nonnull ItemRenderer itemRenderer, ItemStack icon) {
        int i = x + getTabX(index) + 6;
        int j = y + getTabY() + 9;
        itemRenderer.renderGuiItem(icon, i, j);
    }


    public static int getTabX(int index) {
        return getWidthWithSpacing() * index;
    }

    public static int getTabY() {
        return -HEIGHT + 4;
    }

    public static boolean isClickOnTab(int screenX, int screenY, int index, double mouseX, double mouseY) {
        int i = screenX + getTabX(index);
        int j = screenY + getTabY();
        return mouseX > (double) i && mouseX < (double) (i + WIDTH) && mouseY > (double) j && mouseY < (double) (j + HEIGHT);
    }

}
