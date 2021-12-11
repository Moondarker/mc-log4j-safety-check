package me.moondark.log4jsafetycheck.mixin;

import com.mojang.realmsclient.gui.ChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiMultiplayer;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends MixinGuiScreen {
    private String log4jVersionString = "log4j v?.?.?";

    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructor(CallbackInfo info) {
        Package log4jClass = org.apache.logging.log4j.Logger.class.getPackage();

        this.log4jVersionString = log4jClass.getImplementationVersion();
        String[] log4jVersionSplit = log4jVersionString.split("\\.");

        if (Integer.valueOf(log4jVersionSplit[0]) == 2 && Integer.valueOf(log4jVersionSplit[1]) < 15) {
            this.log4jVersionString = "log4j v" + ChatFormatting.RED + this.log4jVersionString;
        } else {
            this.log4jVersionString = "log4j v" + ChatFormatting.GREEN + this.log4jVersionString;
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        this.drawString(this.fontRenderer, this.log4jVersionString, 12, 12, 16777215);
    }
}