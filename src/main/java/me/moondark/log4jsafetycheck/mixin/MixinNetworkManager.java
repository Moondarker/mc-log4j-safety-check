package me.moondark.log4jsafetycheck.mixin;

import com.mojang.realmsclient.gui.ChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager
{
    @Shadow
    public abstract void closeChannel(ITextComponent message);

    @Shadow
    private ITextComponent terminationReason;

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo callbackInfo)
    {
        if (packet instanceof CPacketLoginStart) {
            Package log4jClass = org.apache.logging.log4j.Logger.class.getPackage();
            String log4jVersion = log4jClass.getImplementationVersion();
            String[] log4jVersionSplit = log4jVersion.split("\\.");

            if (Integer.valueOf(log4jVersionSplit[0]) == 2 && Integer.valueOf(log4jVersionSplit[1]) < 15) {
                this.closeChannel(new TextComponentString( ChatFormatting.RED + "[AutoDisconnect] " + ChatFormatting.RESET + "Outdated log4j version: v" + log4jVersion));

                System.out.println("Outdated log4j version: v" + log4jVersion);
                callbackInfo.cancel();
            }
        }
    }
}
