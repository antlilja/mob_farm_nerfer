package com.github.signekatt.mixin;

import com.github.signekatt.MobFarmNerfer;

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
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    private DamageSource lastDamageSource;

    @Shadow
    protected float lastDamageTaken;

    @Inject(at = @At("HEAD"), method = "drop", cancellable = true)
    protected void onDrop(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        // If the entity is a player we always want to drop xp and inventory
        if (!entity.world.isClient && !(entity instanceof PlayerEntity)) {
            // Checks for falling damage
            if (MobFarmNerfer.FALL_DAMAGE_THRESHOLD > 0.0F) {
                boolean lastDamageFall = lastDamageSource != null && lastDamageSource == DamageSource.FALL;
                if (lastDamageFall
                        && lastDamageTaken >= (entity.getMaximumHealth() * MobFarmNerfer.FALL_DAMAGE_THRESHOLD)) {
                    info.cancel();
                }
            }

            // Checks for mob crowding
            if (MobFarmNerfer.CROWDING_THRESHOLD > 0 && MobFarmNerfer.CROWDING_RADIUS > 0) {
                int radius = MobFarmNerfer.CROWDING_RADIUS;
                int entityCount = entity.world.getEntities(LivingEntity.class,
                        new Box(entity.getBlockPos().down(radius).west(radius).north(radius),
                                entity.getBlockPos().up(radius).east(radius).south(radius)),
                        null).size();

                if (entityCount >= MobFarmNerfer.CROWDING_THRESHOLD) {
                    info.cancel();
                }
            }
        }
    }
}