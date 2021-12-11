package me.moondark.log4jsafetycheck.mixin;

import com.mojang.realmsclient.gui.ChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.TextComponentString;

@Mixin(GuiConnecting.class)
public abstract class MixinGuiConnecting {
    private boolean log4jUnsafe = false;
    private String log4jVersion = "";

    @Shadow
    private NetworkManager networkManager;

    @Shadow
    private boolean cancel;

    @Shadow
    private GuiScreen previousGuiScreen;

    @Inject(method = "connect", at = @At("HEAD"), cancellable = true)
    private void connect(final String ip, final int port, CallbackInfo callbackInfo) {
        Package log4jClass = org.apache.logging.log4j.Logger.class.getPackage();

        this.log4jVersion = log4jClass.getImplementationVersion();
        String[] log4jVersionSplit = this.log4jVersion.split("\\.");

        if (Integer.valueOf(log4jVersionSplit[0]) == 2 && Integer.valueOf(log4jVersionSplit[1]) < 15) {
            this.cancel = true;
            this.log4jUnsafe = true;

            if (this.networkManager != null)
            {
                this.networkManager.closeChannel(new TextComponentString("Aborted"));
            }

            System.out.println("Outdated log4j version: v" + this.log4jVersion);
            callbackInfo.cancel();
        }
    }

    @Inject(method = "updateScreen", at = @At("RETURN"))
    public void updateScreen(CallbackInfo info) {
        if (this.networkManager == null && this.cancel && this.log4jUnsafe) { // It's essentially impossible to switch displays in connect() because it's called in constructor, which is called before another displayGuiScreen()
            Minecraft.getMinecraft().displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", new TextComponentString(ChatFormatting.RED + "[log4jSafetyCheck] " + ChatFormatting.RESET + "Outdated log4j version: v" + this.log4jVersion)));
        }
    }
}