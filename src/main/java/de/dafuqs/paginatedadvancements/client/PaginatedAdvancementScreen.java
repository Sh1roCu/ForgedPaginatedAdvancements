package de.dafuqs.paginatedadvancements.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.dafuqs.paginatedadvancements.PaginatedAdvancementsClient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import net.minecraft.client.gui.advancements.AdvancementTabGui;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;

public class PaginatedAdvancementScreen extends AdvancementsScreen implements ClientAdvancementManager.IListener {

    public static final ResourceLocation PAGINATION_TEXTURE = new ResourceLocation("paginatedadvancements", "textures/gui/buttons.png");
    public static final ResourceLocation WINDOW_TEXTURE = new ResourceLocation("textures/gui/advancements/window.png");
    public static final ResourceLocation TABS_TEXTURE = new ResourceLocation("textures/gui/advancements/tabs.png");

    private static final ITextComponent SAD_LABEL_TEXT = new TranslationTextComponent("advancements.sad_label");
    private static final ITextComponent EMPTY_TEXT = new TranslationTextComponent("advancements.empty");
    private static final ITextComponent ADVANCEMENTS_TEXT = new TranslationTextComponent("gui.advancements");

    private final ClientAdvancementManager advancementHandler;
    private final Map<Advancement, PaginatedAdvancementTab> tabs = Maps.newLinkedHashMap();
    private final Map<Advancement, PaginatedAdvancementTab> pinnedTabs = Maps.newLinkedHashMap();
    @Nullable
    private PaginatedAdvancementTab selectedTab;
    private boolean movingTab;

    // pagination
    private int currentPage = 0;

    public static final int ADDITIONAL_PADDING_TOP = 20; // to account for the advancement tabs at the top
    public static final int BORDER_PADDING = 32;
    public static final int ELEMENT_WIDTH = 15;
    public static final int TOP_ELEMENT_HEIGHT = 22;
    public static final int BOTTOM_ELEMENT_HEIGHT = 15;

    public static final int FAVOURITES_BUTTON_WIDTH = 18;
    public static final int FAVOURITES_BUTTON_HEIGHT = 18;

    public static final int FAVOURITES_BUTTON_OFFSET_X = 32;
    public static final int FAVOURITES_BUTTON_OFFSET_Y = 8;

    public PaginatedAdvancementScreen(ClientAdvancementManager advancementHandler) {
        super(advancementHandler);
        this.advancementHandler = advancementHandler;
    }

    @Override
    protected void init() {
        super.init();
        this.tabs.clear();
        this.selectedTab = null;
        this.advancementHandler.setListener(this);

        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            boolean tabSelected = false;
            if (PaginatedAdvancementsClient.CONFIG.SaveLastSelectedTab && !PaginatedAdvancementsClient.CONFIG.LastSelectedTab.isEmpty()) {
                // search for the tab and if that is existent open that instead

                ResourceLocation savedTabResourceLocation = ResourceLocation.tryParse(PaginatedAdvancementsClient.CONFIG.LastSelectedTab);
                for (AdvancementTabGui advancementTab : this.tabs.values()) {
                    if (advancementTab.getAdvancement().getId().equals(savedTabResourceLocation)) {
                        this.advancementHandler.setSelectedTab(advancementTab.getAdvancement(), true);
                        tabSelected = true;
                        break;
                    }
                }
            }
            if (!tabSelected) {
                // vanilla default behavior: just open some random tab
                this.advancementHandler.setSelectedTab((this.tabs.values().iterator().next()).getRoot(), true);
            }
        } else {
            this.advancementHandler.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getRoot(), true);
        }

        // initialize pinned tabs
        if (!this.tabs.isEmpty() && PaginatedAdvancementsClient.hasPins()) {
            for (String pinnedTabString : PaginatedAdvancementsClient.getPinnedTabs()) {
                ResourceLocation pinnedTabResourceLocation = ResourceLocation.tryParse(pinnedTabString);
                for (PaginatedAdvancementTab advancementTab : this.tabs.values()) {
                    if (advancementTab.getRoot().getId().equals(pinnedTabResourceLocation)) {
                        this.pinnedTabs.put(advancementTab.getRoot(), advancementTab);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void removed() {
        super.removed();
        this.advancementHandler.setListener(null);
        ClientPlayNetHandler clientplaynethandler = this.getMinecraft().getConnection();
        if (clientplaynethandler != null) {
            clientplaynethandler.send(CSeenAdvancementsPacket.closedScreen());
        }
    }

    // instead of drawing the full texture here, we cut it into pieces and draw
    // the top, sides and more piece by piece, making the size variable with the mc window size
    public void drawWindow(MatrixStack matrixStack, int mouseX, int mouseY, int minWidth, int minHeight, int maxWidth, int maxHeight) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        this.getMinecraft().getTextureManager().bind(WINDOW_TEXTURE);
        drawFrame(matrixStack, minWidth, minHeight, maxWidth, maxHeight);
        this.font.draw(matrixStack, ADVANCEMENTS_TEXT, minWidth + 8, minHeight + 6, 4210752);
    }

    public void drawPinButtonAndHeader(MatrixStack matrixStack, int mouseX, int mouseY, int startX, int startY, int endX, int endY, boolean hasPins) {
        if (this.selectedTab != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            this.getMinecraft().getTextureManager().bind(PAGINATION_TEXTURE);

            if (isClickOnFavouritesButton(mouseX, mouseY, startY, endX)) {
                if (PaginatedAdvancementsClient.isPinned(this.selectedTab.getRoot().getId())) {
                    this.blit(matrixStack, endX - FAVOURITES_BUTTON_OFFSET_X, startY + FAVOURITES_BUTTON_OFFSET_Y, FAVOURITES_BUTTON_WIDTH, 46 + FAVOURITES_BUTTON_HEIGHT, FAVOURITES_BUTTON_WIDTH, FAVOURITES_BUTTON_HEIGHT);
                } else {
                    this.blit(matrixStack, endX - FAVOURITES_BUTTON_OFFSET_X, startY + FAVOURITES_BUTTON_OFFSET_Y, 0, 46 + FAVOURITES_BUTTON_HEIGHT, FAVOURITES_BUTTON_WIDTH, FAVOURITES_BUTTON_HEIGHT);
                }
            } else {
                if (PaginatedAdvancementsClient.isPinned(this.selectedTab.getRoot().getId())) {
                    this.blit(matrixStack, endX - FAVOURITES_BUTTON_OFFSET_X, startY + FAVOURITES_BUTTON_OFFSET_Y, FAVOURITES_BUTTON_WIDTH, 46, FAVOURITES_BUTTON_WIDTH, FAVOURITES_BUTTON_HEIGHT);
                } else {
                    this.blit(matrixStack, endX - FAVOURITES_BUTTON_OFFSET_X, startY + FAVOURITES_BUTTON_OFFSET_Y, 0, 46, FAVOURITES_BUTTON_WIDTH, FAVOURITES_BUTTON_HEIGHT);
                }
            }

            if (hasPins) {
                // draw pinned tab header
                this.blit(matrixStack, endX + PinnedAdvancementTabType.getTabX() + 1, startY + 6, 46, 0, 32, 15);
            }
        }
    }

    public boolean isClickOnFavouritesButton(double mouseX, double mouseY, int minHeight, int maxWidth) {
        return mouseX > maxWidth - FAVOURITES_BUTTON_OFFSET_X && mouseX < maxWidth - FAVOURITES_BUTTON_OFFSET_X + FAVOURITES_BUTTON_WIDTH && mouseY > minHeight + FAVOURITES_BUTTON_OFFSET_Y && mouseY < minHeight + FAVOURITES_BUTTON_OFFSET_Y + FAVOURITES_BUTTON_HEIGHT;
    }

    public int getMaxPaginatedTabsToRender(int startX, int endXTitle, int endXWindow, boolean paginated) {
        if (paginated) {
            int usableWidth = endXTitle - startX;
            return ((usableWidth - 58) / PaginatedAdvancementTabType.getWidthWithSpacing()); // room for forward and back button
        } else {
            int usableWidth = endXWindow - startX;
            return ((usableWidth - 28) / PaginatedAdvancementTabType.getWidthWithSpacing());
        }
    }

    public int getMaxPinnedTabsToRender(int startY, int endY) {
        int usableHeight = endY - startY;
        return (usableHeight - 56 + PaginatedAdvancementTabType.getWidthWithSpacing()) / PinnedAdvancementTabType.getHeightWithSpacing(); // room for pin button + spacing
    }

    private boolean isPaginated(int startX, int endXWindow) {
        if (tabs.size() < 3) {
            return false; // fast fail. Does not make sense to paginate
        } else {
            return endXWindow - startX < tabs.size() * PaginatedAdvancementTabType.getWidthWithSpacing();
        }
    }

    private void renderPaginatedTabs(MatrixStack matrixStack, int startX, int startY, int endXTitle, int endXWindow, boolean paginated) {
        Iterator<PaginatedAdvancementTab> tabIterator = this.tabs.values().iterator();
        int maxAdvancementTabsToRender = getMaxPaginatedTabsToRender(startX, endXTitle, endXWindow, paginated);
        this.getMinecraft().getTextureManager().bind(TABS_TEXTURE);
        int index = 0;
        PaginatedAdvancementTab advancementTab;
        while (tabIterator.hasNext()) {
            advancementTab = tabIterator.next();
            if (paginated) {
                if (advancementTab.getPaginatedDisplayedPage(maxAdvancementTabsToRender) == this.currentPage) {
                    int displayedPosition = advancementTab.getPaginatedDisplayedPosition(maxAdvancementTabsToRender, this.currentPage);
                    advancementTab.drawBackground(matrixStack, startX, startY, advancementTab == this.selectedTab, displayedPosition);
                }
            } else {
                advancementTab.drawBackground(matrixStack, startX, startY, advancementTab == this.selectedTab, index);
                index++;
            }
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        index = 0;
        tabIterator = this.tabs.values().iterator();
        while (tabIterator.hasNext()) {
            advancementTab = tabIterator.next();
            if (paginated) {
                if (advancementTab.getPaginatedDisplayedPage(maxAdvancementTabsToRender) == this.currentPage) {
                    int displayedPosition = advancementTab.getPaginatedDisplayedPosition(maxAdvancementTabsToRender, this.currentPage);
                    matrixStack.pushPose();
                    RenderSystem.pushMatrix();
                    RenderSystem.multMatrix(matrixStack.last().pose());
                    RenderHelper.turnBackOn();
                    advancementTab.drawIcon(startX, startY,  this.itemRenderer, displayedPosition);
                    RenderHelper.turnOff();
                    RenderSystem.popMatrix();
                    matrixStack.popPose();
                }
            } else {
                matrixStack.pushPose();
                RenderSystem.pushMatrix();
                RenderSystem.multMatrix(matrixStack.last().pose());
                RenderHelper.turnBackOn();
                advancementTab.drawIcon(startX, startY, this.itemRenderer, index);
                RenderHelper.turnOff();
                RenderSystem.popMatrix();
                matrixStack.popPose();
                index++;
            }
        }
        RenderSystem.disableBlend();
    }

    private void renderPinnedTabs(MatrixStack matrixStack, int startX, int startY, int endX, int endY) {
        int maxPinnedTabs = getMaxPinnedTabsToRender(startY, endY);

        Iterator<PaginatedAdvancementTab> tabIterator = this.pinnedTabs.values().iterator();
        this.getMinecraft().getTextureManager().bind(TABS_TEXTURE);

        PaginatedAdvancementTab advancementTab;
        while (tabIterator.hasNext()) {
            advancementTab = tabIterator.next();
            advancementTab.drawPinnedBackground(matrixStack, endX, startY, advancementTab == this.selectedTab, maxPinnedTabs);
        }

        RenderSystem.defaultBlendFunc();
        tabIterator = this.pinnedTabs.values().iterator();
        while (tabIterator.hasNext()) {
            advancementTab = tabIterator.next();
            advancementTab.drawPinnedIcon(endX, startY, this.itemRenderer, maxPinnedTabs);
        }
        RenderSystem.disableBlend();
    }

    // instead of drawing the full texture here, we cut it into pieces and draw
    // the top, sides and more piece by piece, making the size variable with the mc window size
    public void drawPaginationButtons(MatrixStack matrixStack, int mouseX, int mouseY, int startX, int endX) {
        matrixStack.pushPose();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bind(PAGINATION_TEXTURE);

        if (isClickOnBackTab(mouseX, mouseY, startX, endX)) {
            // hover
            this.blit(matrixStack, startX + 4, TOP_ELEMENT_HEIGHT + ADDITIONAL_PADDING_TOP - 15, 0, 23, 23, 23);
        } else {
            // no hover
            this.blit(matrixStack, startX + 4, TOP_ELEMENT_HEIGHT + ADDITIONAL_PADDING_TOP - 15, 0, 0, 23, 23);
        }

        if (isClickOnForwardTab(mouseX, mouseY, startX, endX)) {
            // hover
            this.blit(matrixStack, endX - startX + 4, TOP_ELEMENT_HEIGHT + ADDITIONAL_PADDING_TOP - 15, 23, 23, 23, 23);
        } else {
            // no hover
            this.blit(matrixStack, endX - startX + 4, TOP_ELEMENT_HEIGHT + ADDITIONAL_PADDING_TOP - 15, 23, 0, 23, 23);
        }

        matrixStack.popPose();
    }

    public static boolean isClickOnBackTab(double mouseX, double mouseY, int startX, int enX) {
        int buttonStartX = startX + 4;
        int buttonStartY = PaginatedAdvancementScreen.TOP_ELEMENT_HEIGHT + PaginatedAdvancementScreen.ADDITIONAL_PADDING_TOP - 15;
        return mouseX > buttonStartX && mouseX < buttonStartX + 23 && mouseY > buttonStartY && mouseY < buttonStartY + 23;
    }

    public static boolean isClickOnForwardTab(double mouseX, double mouseY, int startX, int endX) {
        int buttonStartX = endX - startX + 4;
        int buttonStartY = PaginatedAdvancementScreen.TOP_ELEMENT_HEIGHT + PaginatedAdvancementScreen.ADDITIONAL_PADDING_TOP - 15;
        return mouseX > buttonStartX && mouseX < buttonStartX + 23 && mouseY > buttonStartY && mouseY < buttonStartY + 23;
    }

    private void drawFrame(MatrixStack matrixStack, int startX, int startY, int endX, int endY) {
        // corners
        this.blit(matrixStack, startX, startY, 0, 0, ELEMENT_WIDTH, TOP_ELEMENT_HEIGHT); // top left
        this.blit(matrixStack, endX - ELEMENT_WIDTH, startY, 237, 0, ELEMENT_WIDTH, TOP_ELEMENT_HEIGHT); // top right
        this.blit(matrixStack, startX, endY - BOTTOM_ELEMENT_HEIGHT, 0, 125, ELEMENT_WIDTH, BOTTOM_ELEMENT_HEIGHT); // bottom left
        this.blit(matrixStack, endX - ELEMENT_WIDTH, endY - BOTTOM_ELEMENT_HEIGHT, 237, 125, ELEMENT_WIDTH, BOTTOM_ELEMENT_HEIGHT); // bottom right

        // left + right sides
        int maxTopHeightInOneDrawCall = 100;
        int middleHeight = endY - startY - TOP_ELEMENT_HEIGHT - BOTTOM_ELEMENT_HEIGHT;
        int currentY = startY + TOP_ELEMENT_HEIGHT;
        while (middleHeight > 0) {
            int currentDrawHeight = Math.min(middleHeight, maxTopHeightInOneDrawCall);

            this.blit(matrixStack, startX, currentY, 0, TOP_ELEMENT_HEIGHT, ELEMENT_WIDTH, currentDrawHeight);
            this.blit(matrixStack, endX - ELEMENT_WIDTH, currentY, 237, TOP_ELEMENT_HEIGHT, ELEMENT_WIDTH, currentDrawHeight);

            middleHeight -= currentDrawHeight;
            currentY += currentDrawHeight;
        }

        // top + bottom
        int maxTopWidthInOneDrawCall = 220;
        int middleWidth = endX - startX - ELEMENT_WIDTH - ELEMENT_WIDTH;
        int currentX = startX + ELEMENT_WIDTH;
        while (middleWidth > 0) {
            int currentDrawWidth = Math.min(middleWidth, maxTopWidthInOneDrawCall);

            this.blit(matrixStack, currentX, startY, ELEMENT_WIDTH, 0, currentDrawWidth, TOP_ELEMENT_HEIGHT);
            this.blit(matrixStack, currentX, endY - BOTTOM_ELEMENT_HEIGHT, ELEMENT_WIDTH, 125, currentDrawWidth, BOTTOM_ELEMENT_HEIGHT);

            middleWidth -= currentDrawWidth;
            currentX += currentDrawWidth;
        }
    }

    @Override
    public void onAddAdvancementRoot(Advancement root) {
        int pinnedIndex = PaginatedAdvancementsClient.getPinIndex(root.getId());
        PaginatedAdvancementTab advancementTab = PaginatedAdvancementTab.create(this.getMinecraft(), this, this.tabs.size(), pinnedIndex, root);
        if (advancementTab != null) {
            this.tabs.put(root, advancementTab);
            if (PaginatedAdvancementsClient.isPinned(root.getId())) {
                this.pinnedTabs.put(root, advancementTab);
            }
        }
    }

    @Override
    public void onRemoveAdvancementRoot(Advancement root) {
    }

    @Override
    public void onAddAdvancementTask(Advancement dependent) {
        PaginatedAdvancementTab advancementTab = this.getTab(dependent);
        if (advancementTab != null) {
            advancementTab.addAdvancement(dependent);
        }
    }

    @Override
    public void onRemoveAdvancementTask(Advancement dependent) {
    }

    @Nullable
    private PaginatedAdvancementTab getTab(Advancement advancement) {
        while (advancement.getParent() != null) {
            advancement = advancement.getParent();
        }

        return this.tabs.get(advancement);
    }

    @Override
    public void onAdvancementsCleared() {
        this.tabs.clear();
        this.pinnedTabs.clear();
        this.selectedTab = null;
    }

    @Override
    public void onUpdateAdvancementProgress(Advancement advancement, AdvancementProgress progress) {
        AdvancementEntryGui advancementWidget = this.getAdvancementWidget(advancement);
        if (advancementWidget != null) {
            advancementWidget.setProgress(progress);
        }
    }

    @Nullable
    public AdvancementEntryGui getAdvancementWidget(Advancement advancement) {
        PaginatedAdvancementTab advancementTab = this.getTab(advancement);
        return advancementTab == null ? null : advancementTab.getWidget(advancement);
    }

    @Override
    public void onSelectedTabChanged(@Nullable Advancement advancement) {
        this.selectedTab = this.tabs.get(advancement);
        if (this.selectedTab != null) {
            PaginatedAdvancementsClient.saveSelectedTab(this.selectedTab.getRoot().getId());
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int startX = BORDER_PADDING;
            int endXWindow = !this.pinnedTabs.isEmpty() ? this.width - BORDER_PADDING - PinnedAdvancementTabType.WIDTH : this.width - BORDER_PADDING;
            int endXTitle = this.width - BORDER_PADDING;
            int startY = BORDER_PADDING + ADDITIONAL_PADDING_TOP;
            int endY = this.height - BORDER_PADDING;

            boolean isPaginated = isPaginated(startX, endXWindow);

            if (this.selectedTab != null && isClickOnFavouritesButton(mouseX, mouseY, startY, endXWindow)) {
                ResourceLocation pageResourceLocation = this.selectedTab.getRoot().getId();
                if (PaginatedAdvancementsClient.isPinned(pageResourceLocation)) {
                    unpinTab(pageResourceLocation);
                } else {
                    pinTab(pageResourceLocation);
                }
            }

            if (isPaginated) {
                if (isClickOnBackTab(mouseX, mouseY, startX, endXTitle)) {
                    pageBackward(startX, endXTitle, endXWindow);
                } else if (isClickOnForwardTab(mouseX, mouseY, startX, endXTitle)) {
                    pageForward(startX, endXTitle, endXWindow);
                }
            }

            int maxDisplayedTabs = getMaxPaginatedTabsToRender(startX, endXTitle, endXWindow, isPaginated);
            for (PaginatedAdvancementTab paginatedAdvancementTab : this.tabs.values()) {
                if (paginatedAdvancementTab.isClickOnTab(BORDER_PADDING, BORDER_PADDING + ADDITIONAL_PADDING_TOP, mouseX, mouseY, isPaginated, maxDisplayedTabs, currentPage)) {
                    this.advancementHandler.setSelectedTab(paginatedAdvancementTab.getRoot(), true);
                    break;
                }
            }

            if (this.pinnedTabs.size() > 0) {
                int maxPinnedTabs = getMaxPinnedTabsToRender(startY, endY);
                for (PaginatedAdvancementTab paginatedAdvancementTab : this.pinnedTabs.values()) {
                    if (paginatedAdvancementTab.isClickOnPinnedTab(endXWindow, startY, mouseX, mouseY, maxPinnedTabs)) {
                        this.advancementHandler.setSelectedTab(paginatedAdvancementTab.getRoot(), true);
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void pinTab(ResourceLocation pageResourceLocation) {
        selectedTab.setPinIndex(this.pinnedTabs.size());
        this.pinnedTabs.put(selectedTab.getRoot(), selectedTab);
        PaginatedAdvancementsClient.pinTab(pageResourceLocation);
    }

    private void unpinTab(ResourceLocation pageResourceLocation) {
        int oldPinIndex = selectedTab.getPinIndex();
        selectedTab.setPinIndex(-1);
        this.pinnedTabs.remove(selectedTab.getRoot());
        PaginatedAdvancementsClient.unpinTab(pageResourceLocation);

        // move all pinned tabs with a pin index > this up by 1 to fill its place
        for (PaginatedAdvancementTab tab : this.pinnedTabs.values()) {
            int currentPinIndex = tab.getPinIndex();
            if (currentPinIndex > oldPinIndex) {
                tab.setPinIndex(currentPinIndex - 1);
            }
        }
    }

    public int getMaxPageIndex(int startX, int endXTitle, int endXWindow) {
        int maxDisplayedTabsPerPage = getMaxPaginatedTabsToRender(startX, endXTitle, endXWindow, true);
        return (this.tabs.size() - 1) / maxDisplayedTabsPerPage;
    }

    public void clampCurrentPage(int startX, int endXTitle, int endXWindow) {
        this.currentPage = Math.min(this.currentPage, getMaxPageIndex(startX, endXTitle, endXWindow));
    }

    public void pageForward(int startX, int endXTitle, int endXWindow) {
        this.currentPage++;
        this.currentPage = this.currentPage % (getMaxPageIndex(startX, endXTitle, endXWindow) + 1);
    }

    public void pageBackward(int startX, int endXTitle, int endXWindow) {
        int maxPageIndex = getMaxPageIndex(startX, endXTitle, endXWindow);
        if (this.currentPage == 0) {
            this.currentPage = maxPageIndex;
        } else {
            this.currentPage--;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.getMinecraft().options.keyAdvancements.matches(keyCode, scanCode)) {
            this.getMinecraft().setScreen(null);
            this.getMinecraft().mouseHandler.grabMouse();
            return true;
        } else if (this.selectedTab != null && keyCode == GLFW.GLFW_KEY_C && modifiers == 2) { // ctrl + c
            this.selectedTab.copyHoveredAdvancementID();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0) {
            this.movingTab = false;
            return false;
        } else {
            if (!this.movingTab) {
                this.movingTab = true;
            } else if (this.selectedTab != null) {
                int endX = !this.pinnedTabs.isEmpty() ? this.width - BORDER_PADDING - PinnedAdvancementTabType.WIDTH - 4 : this.width - BORDER_PADDING;
                int endY = this.height - BORDER_PADDING;

                this.selectedTab.move(deltaX, deltaY, endX - 60 + 5, endY - 84);
            }

            return true;
        }
    }

    private void drawWidgetTooltip(MatrixStack matrixStack, int mouseX, int mouseY, int startX, int startY, int endXTitle, int endXWindow, int endY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.selectedTab != null) {
            matrixStack.pushPose();
            RenderSystem.enableDepthTest();
            matrixStack.translate((startX + 9), (startY + 18), 400.0D);
            this.selectedTab.drawWidgetTooltip(matrixStack, mouseX - startX - 9, mouseY - startY - 18, startX, startY, endXWindow, endY);

            matrixStack.translate(0, 0, 400.0D);
            if (PaginatedAdvancementsClient.CONFIG.shouldShowAdvancementDebug(this.getMinecraft())) {
                this.selectedTab.drawDebugInfo(matrixStack, startX, endXWindow, endY);
            }

            RenderSystem.disableDepthTest();
            matrixStack.popPose();
        }

        if (this.tabs.size() > 1) {
            boolean isPaginated = isPaginated(startX, endXWindow);
            int maxDisplayedTabs = getMaxPaginatedTabsToRender(startX, endXTitle, endXWindow, isPaginated);

            for (PaginatedAdvancementTab paginatedAdvancementTab : this.tabs.values()) {
                if (paginatedAdvancementTab.isClickOnTab(startX, startY, mouseX, mouseY, isPaginated, maxDisplayedTabs, currentPage)) {
                    this.renderTooltip(matrixStack, paginatedAdvancementTab.getTitle(), mouseX, mouseY);
                }
            }
        }

        if (!this.pinnedTabs.isEmpty()) {
            int maxPinnedTabs = getMaxPinnedTabsToRender(startY, endY);
            for (PaginatedAdvancementTab paginatedAdvancementTab : this.pinnedTabs.values()) {
                if (paginatedAdvancementTab.isClickOnPinnedTab(endXWindow, startY, mouseX, mouseY, maxPinnedTabs)) {
                    this.renderTooltip(matrixStack, paginatedAdvancementTab.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        boolean hasPins = !this.pinnedTabs.isEmpty();
        int startX = BORDER_PADDING;
        int startY = BORDER_PADDING + ADDITIONAL_PADDING_TOP;
        int endXWindow = hasPins ? this.width - BORDER_PADDING - PinnedAdvancementTabType.WIDTH : this.width - BORDER_PADDING;
        int endXTitle = this.width - BORDER_PADDING;
        int endY = this.height - BORDER_PADDING;

        clampCurrentPage(startX, endXTitle, endXWindow); // if the screen has been resized

        this.renderBackground(matrixStack);
        this.drawAdvancementTree(matrixStack, startX, startY, endXWindow, endY);
        this.drawWindow(matrixStack, mouseX, mouseY, startX, startY, endXWindow, endY);

        if (this.tabs.size() > 1) {
            if (isPaginated(startX, endXWindow)) { // overflows
                // draw forward and back button tabs, fill the rest with the remaining tabs
                drawPaginationButtons(matrixStack, mouseX, mouseY, startX, endXTitle);
                renderPaginatedTabs(matrixStack, startX, startY, endXTitle, endXWindow, true);
            } else {
                renderPaginatedTabs(matrixStack, startX, startY, endXTitle, endXWindow, false);
            }
        }
        if (hasPins) {
            renderPinnedTabs(matrixStack, startX, startY, endXWindow, endY);
        }
        this.drawWidgetTooltip(matrixStack, mouseX, mouseY, startX, startY, endXTitle, endXWindow, endY);
        this.drawPinButtonAndHeader(matrixStack, mouseX, mouseY, startX, startY, endXWindow, endY, hasPins);
    }

    private void drawAdvancementTree(MatrixStack matrixStack, int startX, int startY, int endX, int endY) {
        PaginatedAdvancementTab advancementTab = this.selectedTab;
        if (advancementTab == null) {
            fill(matrixStack, startX + 9, startY + 18, endX, endY, -16777216);

            int textCenterX = startX + ((endX - startX) / 2);
            int textY = startY + ((endY - startY) / 2);
            drawCenteredString(matrixStack, this.font, EMPTY_TEXT, textCenterX, textY, -1);
            drawCenteredString(matrixStack, this.font, SAD_LABEL_TEXT, textCenterX, textY + 16, -1);
        } else {
            matrixStack.pushPose();
            matrixStack.translate((startX + 9), (startY + 18), 0.0D);
            advancementTab.render(matrixStack, startX, startY, endX, endY);
            matrixStack.popPose();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
        }
    }

}