package de.dafuqs.paginatedadvancements.mixin;

import de.dafuqs.paginatedadvancements.frames.AdvancementFrameDataLoader;
import de.dafuqs.paginatedadvancements.frames.FrameWrapper;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Advancement.class)
public abstract class AdvancementMixin {

    /**
     * Redirect all calls to the vanilla advancement screen to out custom one
     * Other screens that extend AdvancementScreen will not be touched
     */
    @ModifyVariable(method = "<init>", at = @At("STORE"))
    private TextFormatting paginatedAdvancements$customFormat(TextFormatting formatting, ResourceLocation id) {
        FrameWrapper frameWrapper = AdvancementFrameDataLoader.get(id);
        if (frameWrapper instanceof FrameWrapper.PaginatedFrameWrapper) {
            return frameWrapper.getFormatting();
        }
        return formatting;
    }

}