package com.teampotato.newplayerprotector.event;

import com.teampotato.newplayerprotector.NewPlayerProtector;
import com.teampotato.newplayerprotector.api.IPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Events {
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player &&
                ((IPlayer)player).protect_getTickCount() < NewPlayerProtector.protectTicks.get() &&
                !player.server.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            player.server.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, player.server);
            player.addTag(NewPlayerProtector.MOD_ID);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player &&
                player.server.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) &&
                player.removeTag(NewPlayerProtector.MOD_ID)) {
            ((IPlayer)player).protect_setRespawnTickCount(3);
        }
    }
}
