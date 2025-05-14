package de.dafuqs.paginatedadvancements.client;

import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.world.item.ItemStack;

public class PinnedAdvancementTabType {

    public static final int TOP_SPACING = 24; // accounting for the "pin" ribbon
    public static final int WIDTH = 32;
    public static final int HEIGHT = 28;

/*    protected static final ResourceLocation RIGHT_TOP_TEXTURE_SELECTED = ResourceLocation.tryParse("advancements/tab_right_top_selected");
    protected static final ResourceLocation RIGHT_MIDDLE_TEXTURE_SELECTED = ResourceLocation.tryParse("advancements/tab_right_middle_selected");
    protected static final ResourceLocation RIGHT_TOP_TEXTURE = ResourceLocation.tryParse("advancements/tab_right_top");
    protected static final ResourceLocation RIGHT_MIDDLE_TEXTURE = ResourceLocation.tryParse("advancements/tab_right_middle");*/

    public static int getWidthWithSpacing() {
        return WIDTH + PaginatedAdvancementsClient.CONFIG.SpacingBetweenHorizontalTabs; // includes the empty space between tabs
    }

    public static int getHeightWithSpacing() {
        return HEIGHT + PaginatedAdvancementsClient.CONFIG.SpacingBetweenPinnedTabs; // includes the empty space between tabs
    }

    public static void drawBackground(GuiGraphics context, int x, int y, boolean selected, int index) {
        int i = index > 0 ? WIDTH + 96 : 96;
        int j = selected ? 64 + HEIGHT : 64;
        context.blit(AdvancementsScreen.TABS_LOCATION, x + getTabX(), y + getTabY(index), i, j, WIDTH, HEIGHT);
    }

    public static void drawIcon(GuiGraphics context, int x, int y, int index, ItemStack stack) {
        int i = x + getTabX() + 6;
        int j = y + getTabY(index) + 5;
        context.renderItem(stack, i, j);
    }

    public static int getTabX(int index) {
        return getWidthWithSpacing() * index;
    }

    public static int getTabY() {
        return -HEIGHT + 4;
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
