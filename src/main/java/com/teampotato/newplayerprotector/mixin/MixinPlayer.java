package com.teampotato.newplayerprotector.mixin;

import com.teampotato.newplayerprotector.NewPlayerProtector;
import com.teampotato.newplayerprotector.api.IPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, priority = 10)
public abstract class MixinPlayer extends LivingEntity implements IPlayer {

    @Unique private int newPlayerProtector$tickCount;
    @Unique private boolean newPlayerProtector$shouldCountTick = true;

    protected MixinPlayer(EntityType<? extends LivingEntity> arg, Level arg2) {
        super(arg, arg2);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", ordinal = 2, shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (this.newPlayerProtector$getShouldCountTick() && !this.getTags().contains(NewPlayerProtector.MOD_ID)) {
            if (newPlayerProtector$tickCount == NewPlayerProtector.protectTicks.get()) {
                this.newPlayerProtector$setShouldCountTick(false);
                this.addTag(NewPlayerProtector.MOD_ID);
                newPlayerProtector$tickCount = 0;
                return;
            }
            newPlayerProtector$tickCount++;
        }
    }

    @Override
    public boolean newPlayerProtector$getShouldCountTick() {
        return newPlayerProtector$shouldCountTick;
    }

    @Override
    public void newPlayerProtector$setShouldCountTick(boolean shouldCountTick) {
        this.newPlayerProtector$shouldCountTick = shouldCountTick;
    }

    @Override
    public int newPlayerProtector$getTickCount() {
        return newPlayerProtector$tickCount;
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void onDiePre(DamageSource arg, CallbackInfo ci) {
        if (this.newPlayerProtector$getTickCount() < NewPlayerProtector.protectTicks.get() && !this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            MinecraftServer server = this.getServer();
            if (server == null) return;
            server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "gamerule keepInventory true");
        }
    }

    @Inject(method = "die", at = @At("RETURN"))
    private void onDiePost(DamageSource arg, CallbackInfo ci) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            MinecraftServer server = this.getServer();
            if (server == null) return;
            server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "gamerule keepInventory false");
        }
    }
}
