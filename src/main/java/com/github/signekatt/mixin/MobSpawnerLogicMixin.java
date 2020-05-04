package com.github.signekatt.mixin;

import java.util.Optional;

import com.github.signekatt.LivingEntityExt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/MobSpawnerLogic;spawnEntity(Lnet/minecraft/entity/Entity;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    void onUpdate(CallbackInfo info, World world, BlockPos blockPos, boolean bl, int i, CompoundTag compoundTag,
            Optional<EntityType<?>> optional, ListTag listTag, int j, double g, double h, double k, Entity entity) {
        ((LivingEntityExt) entity).mob_farm_nerfer_setIsSpawnerMob(true);
    }
}