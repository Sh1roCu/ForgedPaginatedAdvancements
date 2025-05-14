package de.dafuqs.paginatedadvancements.frames;

import de.dafuqs.paginatedadvancements.client.PaginatedAdvancementFrame;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract class FrameWrapper {
    public abstract ResourceLocation getId();

    public abstract int getItemOffsetX();

    public abstract int getItemOffsetY();

    /*public abstract Style getTitleStyle();*/
    public abstract ChatFormatting getFormatting();

    public abstract ResourceLocation getTexture(AdvancementWidgetType status, FrameType vanillaFrame);

    public static class VanillaFrameWrapper extends FrameWrapper {
        public final FrameType frame;

        private VanillaFrameWrapper(FrameType frame) {
            this.frame = frame;
        }

        @Override
        public ResourceLocation getId() {
            return ResourceLocation.tryParse(frame.getName());
        }

        @Override
        public int getItemOffsetX() {
            return 0;
        }

        @Override
        public int getItemOffsetY() {
            return 0;
        }

/*        @Override
        public Style getTitleStyle() {
            return Style.EMPTY.applyFormat(frame.getChatColor());
        }*/

        public ChatFormatting getFormatting() {
            return frame.getChatColor();
        }

        public ResourceLocation getTexture(AdvancementWidgetType status, FrameType vanillaFrame) {
            return status.frameSprite(frame);
        }

    }

    public static class PaginatedFrameWrapper extends FrameWrapper {
        public final PaginatedAdvancementFrame frame;

        @Override
        public ResourceLocation getId() {
            return frame.getId();
        }

        private PaginatedFrameWrapper(PaginatedAdvancementFrame frame) {
            this.frame = frame;
        }

        @Override
        public int getItemOffsetX() {
            return frame.getItemOffsetX();
        }

        @Override
        public int getItemOffsetY() {
            return frame.getItemOffsetY();
        }

        /*  @Override
          public Style getTitleStyle() {
              return frame.getTitleStyle();
          }*/

        @Override
        public ChatFormatting getFormatting() {
            return frame.getFormatting();
        }

        public ResourceLocation getTexture(AdvancementWidgetType status, FrameType vanillaFrame) {
            if (status == AdvancementWidgetType.OBTAINED) {

                return frame.getTextureObtained();
            }
            return frame.getTextureUnobtained();
        }

    }

    public static @Nullable FrameWrapper of(ResourceLocation frame) {
        String path = frame.getPath();
        if (frame.getNamespace().equals("minecraft")) {
            for (FrameType vanillaFrame : FrameType.values()) {
                if (vanillaFrame.getName().equals(path)) {
                    return new VanillaFrameWrapper(vanillaFrame);
                }
            }
        }

        @Nullable PaginatedAdvancementFrame paginatedFrame = AdvancementFrameTypeDataLoader.getFrameForAdvancement(frame);
        return paginatedFrame == null ? null : new PaginatedFrameWrapper(paginatedFrame);
    }

}
