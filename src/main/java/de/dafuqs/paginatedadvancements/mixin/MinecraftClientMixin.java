package de.dafuqs.paginatedadvancements.mixin;

import de.dafuqs.paginatedadvancements.client.PaginatedAdvancementScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {


    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    /**
     * Redirect all calls to the vanilla advancement screen to out custom one
     * Other screens that extend AdvancementScreen will not be touched
     */
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen paginatedAdvancements$modifyAdvancementsScreen(Screen screen) {
        if (this.player != null && screen != null && AdvancementsScreen.class == screen.getClass()) {
            return new PaginatedAdvancementScreen(this.player.connection.getAdvancements());
        } else {
            return screen;
        }
    }
}
