package me.moondark.log4jsafetycheck;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "log4jsafetycheck", name = "Log4jSafetyChecker", version = Log4jSafetyChecker.VERSION)
public final class Log4jSafetyChecker {
	public static final String VERSION = "1.01";

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Log4j Safety Checker ready");
    }
}
