package de.dafuqs.paginatedadvancements.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import net.minecraft.client.gui.advancements.AdvancementTabGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(AdvancementEntryGui.class)
public interface AdvancementWidgetAccessor {

    @Accessor(value = "x")
    int getX();

    @Accessor(value = "y")
    int getY();

    @Accessor(value = "advancement")
    Advancement getAdvancement();

    @Accessor(value = "display")
    DisplayInfo getDisplay();

    @Accessor(value = "progress")
    AdvancementProgress getProgress();

    @Accessor(value = "children")
    List<AdvancementEntryGui> getChildren();

    @Accessor(value = "width")
    int getWidth();

    @Accessor(value = "description")
    List<IReorderingProcessor> getDescription();

    @Accessor(value = "tab")
    AdvancementTabGui getTab();

    @Accessor(value = "title")
    IReorderingProcessor getTitle();

    @Invoker(value = "findOptimalLines")
    List<ITextProperties> invokeWrapDescription(ITextComponent text, int width);

}