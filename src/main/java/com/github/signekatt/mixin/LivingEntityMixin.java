package com.github.signekatt.mixin;

import com.github.signekatt.LivingEntityExt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityExt {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    private DamageSource lastDamageSource;

    @Shadow
    protected float lastDamageTaken;

    private boolean mob_farm_nerfer_isSpawnerMob = false;

    public void mob_farm_nerfer_setIsSpawnerMob(boolean val) {
        this.mob_farm_nerfer_isSpawnerMob = val;
    }

    @Inject(at = @At("HEAD"), method = "dropXp", cancellable = true)
    protected void onDropXp(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (this.mob_farm_nerfer_isSpawnerMob || (!(entity instanceof PlayerEntity) && lastDamageSource != null
                && lastDamageSource == DamageSource.FALL && lastDamageTaken >= (entity.getMaximumHealth() / 2.0F))) {
            info.cancel();
        }
    }

    @Inject(at = @At("RETURN"), method = "writeCustomDataToTag")
    public void onWriteCustomDataToTag(CompoundTag tag, CallbackInfo info) {
        tag.putBoolean("mob_farm_nerfer_IsSpawnerMob", this.mob_farm_nerfer_isSpawnerMob);
    }

    @Inject(at = @At("RETURN"), method = "readCustomDataFromTag")
    public void onReadCustomDataFromTag(CompoundTag tag, CallbackInfo info) {
        this.mob_farm_nerfer_isSpawnerMob = tag.getBoolean("mob_farm_nerfer_IsSpawnerMob");
    }
}