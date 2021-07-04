package com.github.signekatt.mixin;

import com.github.signekatt.MobFarmNerfer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(HostileEntity.class)
public class HostileEntityMixin extends PathAwareEntity implements Monster {

    protected HostileEntityMixin(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    private int mob_farm_nerfer_PrevDeathCalc = -1;
    private float mob_farm_nerfer_FallDamageTaken = 0.0F;

    private boolean shouldDrop() {
        // If value was previously calculated we don't have to do it again
        if (mob_farm_nerfer_PrevDeathCalc == -1) {
            HostileEntity thisP = (HostileEntity) (Object) this;
            // Checks for falling damage
            if (MobFarmNerfer.FALL_DAMAGE_THRESHOLD > 0.0F) {
                float maxFallDamage = thisP.getMaxHealth() * MobFarmNerfer.FALL_DAMAGE_THRESHOLD;
                if (mob_farm_nerfer_FallDamageTaken >= maxFallDamage) {
                    mob_farm_nerfer_PrevDeathCalc = 0;
                    return false;
                }
            }

            // Checks for mob crowding
            if (MobFarmNerfer.CROWDING_THRESHOLD > 0 && MobFarmNerfer.CROWDING_RADIUS > 0) {
                int radius = MobFarmNerfer.CROWDING_RADIUS;
                BlockPos blockPos = thisP.getBlockPos();
                int entityCount = thisP.world
                        .getEntitiesByType(TypeFilter.instanceOf(HostileEntity.class), new Box(blockPos.down(radius).west(radius).north(radius),
                                blockPos.up(radius).east(radius).south(radius)), Entity::isAlive)
                        .size();

                if (entityCount >= MobFarmNerfer.CROWDING_THRESHOLD) {
                    mob_farm_nerfer_PrevDeathCalc = 0;
                    return false;
                }
            }
            
            // Checks if mob can reach player
            if (MobFarmNerfer.MAX_PATH_CHECKING_DISTANCE > 0) {
                Entity attacker = thisP.getAttacker();
                if (attacker instanceof PlayerEntity) {
                    int distance = (int) attacker.distanceTo(thisP);
                    if (distance < MobFarmNerfer.MAX_PATH_CHECKING_DISTANCE) {
                        Path path = thisP.getNavigation().findPathTo(attacker.getBlockPos(), distance);
                        if (path != null && !path.reachesTarget()) {
                            mob_farm_nerfer_PrevDeathCalc = 0;
                            return false;
                        }
                    }
                }
            }

            mob_farm_nerfer_PrevDeathCalc = 1;
        } else if (mob_farm_nerfer_PrevDeathCalc == 0) {
            return false;
        }

        return true;
    }

    @Inject(at = @At("HEAD"), method = "shouldDropXp", cancellable = true)
    protected void onShouldDropXp(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(shouldDrop());
        info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "shouldDropLoot", cancellable = true)
    protected void onShouldDropLoot(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(shouldDrop());
        info.cancel();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);

        if(result && source == DamageSource.FALL) {
            mob_farm_nerfer_FallDamageTaken += amount;
        }

        return result;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("mob_farm_nerfer_FallDamageTaken", mob_farm_nerfer_FallDamageTaken);
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        mob_farm_nerfer_FallDamageTaken = nbt.getFloat("mob_farm_nerfer_FallDamageTaken");
        super.readCustomDataFromNbt(nbt);
    }
}