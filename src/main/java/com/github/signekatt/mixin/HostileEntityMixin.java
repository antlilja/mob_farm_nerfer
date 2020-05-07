package com.github.signekatt.mixin;

import com.github.signekatt.MobFarmNerfer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.Monster;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(HostileEntity.class)
public class HostileEntityMixin extends MobEntityWithAi implements Monster {

    protected HostileEntityMixin(EntityType<? extends MobEntityWithAi> type, World world) {
        super(type, world);
    }

    private float mob_farm_nerfer_FallDamageTaken = 0.0F;

    @Inject(at = @At("HEAD"), method = "damage")
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (source == DamageSource.FALL) {
            mob_farm_nerfer_FallDamageTaken += amount;
        }
    }

    @Inject(at = @At("HEAD"), method = "canDropLootAndXp", cancellable = true)
    protected void onCanDropLootAndXp(CallbackInfoReturnable<Boolean> info) {
        HostileEntity thisP = (HostileEntity) (Object) this;
        // Checks for falling damage
        if (MobFarmNerfer.FALL_DAMAGE_THRESHOLD > 0.0F) {
            float maxFallDamage = thisP.getMaximumHealth() * MobFarmNerfer.FALL_DAMAGE_THRESHOLD;
            if (mob_farm_nerfer_FallDamageTaken >= maxFallDamage) {
                info.setReturnValue(false);
                info.cancel();
                    return;
            }
        }

        // Checks for mob crowding
        if (MobFarmNerfer.CROWDING_THRESHOLD > 0 && MobFarmNerfer.CROWDING_RADIUS > 0) {
            int radius = MobFarmNerfer.CROWDING_RADIUS;
            BlockPos blockPos = thisP.getBlockPos();
            int entityCount = thisP.world
                    .getEntities(HostileEntity.class, new Box(blockPos.down(radius).west(radius).north(radius),
                            blockPos.up(radius).east(radius).south(radius)), null)
                    .size();

            if (entityCount >= MobFarmNerfer.CROWDING_THRESHOLD) {
                info.setReturnValue(false);
                info.cancel();
                    return;
            }
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        tag.putFloat("mob_farm_nerfer_FallDamageTaken", mob_farm_nerfer_FallDamageTaken);
        super.writeCustomDataToTag(tag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        mob_farm_nerfer_FallDamageTaken = tag.getFloat("mob_farm_nerfer_FallDamageTaken");
        super.readCustomDataFromTag(tag);
    }

}