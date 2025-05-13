package de.dafuqs.paginatedadvancements.mixin;

import net.minecraft.advancements.AdvancementProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementProgress.class)
public interface AdvancementProgressAccessor {
    @Accessor(value = "requirements")
    String[][] getRequirements();
}