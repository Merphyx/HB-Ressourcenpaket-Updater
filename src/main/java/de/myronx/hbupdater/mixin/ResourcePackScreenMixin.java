package de.myronx.hbupdater.mixin;

import de.myronx.hbupdater.client.HBPackUpdaterClient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import java.nio.file.Path;

import static de.myronx.hbupdater.HBPackUpdater.MODID;

@Mixin(PackScreen.class)
public abstract class ResourcePackScreenMixin extends Screen {
    private static final Identifier textures$FOCUSED = new Identifier(MODID, "textures/gui/buttonfocused.png");
    private static final Identifier textures$UNFOCUSED = new Identifier(MODID, "textures/gui/buttonunfocused.png");
    @Shadow
    @Final
    private Path file;

    protected ResourcePackScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void addTexturesButton(CallbackInfo ci)
    {
        if(this.file.equals(this.client.getResourcePackDir())) {
            this.addDrawableChild(new TexturedButtonWidget(
                    22, (this.height - 40), 22, 22,
                    new ButtonTextures(textures$UNFOCUSED, textures$FOCUSED),
                    (button) -> HBPackUpdaterClient.downloadPack(),
                    Text.translatable(MODID + ".open_tooltip")) {
                {
                    setTooltip(Tooltip.of(Text.of(("HorstBlocks-Ressourcenpaket sofort aktualisieren. \n\n§cEntferne vorher das Ressourcenpaket aus den \"ausgewählten Ressourcenpaketen\"."))));
                }

                @Override
                public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                    Identifier identifier = this.isSelected() ? textures$FOCUSED : textures$UNFOCUSED;
                    context.drawTexture(identifier, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
                }

            });
        }
    }
}
