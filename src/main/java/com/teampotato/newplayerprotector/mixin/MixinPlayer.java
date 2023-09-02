package com.teampotato.newplayerprotector.mixin;

import com.teampotato.newplayerprotector.NewPlayerProtector;
import com.teampotato.newplayerprotector.api.IPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = Player.class, priority = 10)
public abstract class MixinPlayer extends LivingEntity implements IPlayer {

    @Unique @Nullable private Integer protect_tickCount = 0;
    @Unique private boolean protect_shouldCountTick = true;
    @Unique private int protect_respawnTickCount = 0;

    protected MixinPlayer(EntityType<? extends LivingEntity> arg, Level arg2) {
        super(arg, arg2);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;isClientSide:Z", ordinal = 2, shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (this.protect_shouldCountTick() && !this.getTags().contains(NewPlayerProtector.MOD_ID + ".end") && protect_tickCount != null && this.level instanceof ServerLevel) {
            protect_tickCount++;
            if (protect_tickCount.equals(NewPlayerProtector.protectTicks.get())) {
                this.protect_setShouldCountTick(false);
                this.addTag(NewPlayerProtector.MOD_ID + ".end");
                protect_tickCount = null;
            }
        }
        if (this.protect_getRespawnTickCount() != 0 && this.level instanceof ServerLevel) {
            this.protect_setRespawnTickCount(this.protect_getRespawnTickCount() - 1);
            if (this.protect_getRespawnTickCount() == 0) {
                this.level.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(false, this.level.getServer());
            }
        }
    }

    @Override
    public boolean protect_shouldCountTick() {
        return protect_shouldCountTick;
    }

    @Override
    public void protect_setShouldCountTick(boolean shouldCountTick) {
        this.protect_shouldCountTick = shouldCountTick;
    }

    @Override
    public int protect_getTickCount() {
        return Objects.requireNonNullElse(protect_tickCount, NewPlayerProtector.protectTicks.get() + 1);
    }

    @Override
    public int protect_getRespawnTickCount() {
        return this.protect_respawnTickCount;
    }

    @Override
    public void protect_setRespawnTickCount(int respawnTickCount) {
        this.protect_respawnTickCount = respawnTickCount;
    }
}
