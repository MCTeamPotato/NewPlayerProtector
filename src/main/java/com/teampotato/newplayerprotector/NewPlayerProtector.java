package com.teampotato.newplayerprotector;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(NewPlayerProtector.MOD_ID)
public class NewPlayerProtector {
    public static final String MOD_ID = "newplayerprotector";

    public static final ForgeConfigSpec config;
    public static final ForgeConfigSpec.IntValue protectTicks;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("NewPlayerProtector");
        protectTicks = builder.defineInRange("protectTicks", 5000, 0, Integer.MAX_VALUE);
        builder.pop();
        config = builder.build();
    }

    public NewPlayerProtector() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, config);
    }
}
