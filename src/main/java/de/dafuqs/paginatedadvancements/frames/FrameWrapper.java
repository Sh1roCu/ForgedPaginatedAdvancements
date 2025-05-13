package de.dafuqs.paginatedadvancements.frames;

import de.dafuqs.paginatedadvancements.client.PaginatedAdvancementFrame;
import net.minecraft.advancements.FrameType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public abstract class FrameWrapper {
    public abstract ResourceLocation getId();

    public abstract int getTextureU();

    public abstract int getTextureV();

    public abstract int getItemOffsetX();

    public abstract int getItemOffsetY();

    /*public abstract Style getTitleStyle();*/
    public abstract TextFormatting getFormatting();

    public abstract ResourceLocation getTextureSheet();

    public static class VanillaFrameWrapper extends FrameWrapper {
        public final FrameType frame;

        private VanillaFrameWrapper(FrameType frame) {
            this.frame = frame;
        }

        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(frame.getName());
        }

        @Override
        public int getTextureU() {
            return frame.getTexture();
        }

        @Override
        public int getTextureV() {
            return 128;
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
            return Style.EMPTY.applyFormat(frame.getChatTextFormatting());
        }*/

        public TextFormatting getFormatting() {
            return frame.getChatColor();
        }

        public ResourceLocation getTextureSheet() {
            return new ResourceLocation("textures/gui/advancements/widgets.png");
        }

    }

    public static class PaginatedFrameWrapper extends FrameWrapper {
        public final PaginatedAdvancementFrame frame;

        private PaginatedFrameWrapper(PaginatedAdvancementFrame frame) {
            this.frame = frame;
        }

        @Override
        public ResourceLocation getId() {
            return frame.getId();
        }

        @Override
        public int getTextureU() {
            return frame.getTextureU();
        }

        @Override
        public int getTextureV() {
            return frame.getTextureV();
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
        public TextFormatting getFormatting() {
            return frame.getFormatting();
        }

        /*        public ResourceLocation getTexture(AdvancementState status, FrameType vanillaFrame) {
                    if (status == AdvancementState.OBTAINED) {

                        return frame.getTextureObtained();
                    }
                    return frame.getTextureUnobtained();
                }*/
        @Override
        public ResourceLocation getTextureSheet() {
            return frame.getTextureSheet();
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
