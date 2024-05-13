package com.level_is_health.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.level_is_health.LevelHealthMod;

@Mixin(PlayerEntity.class)
public abstract class LevelUpMixin extends LivingEntity {

    private static final UUID MODIFIER_ID = UUID.fromString("6fd1cdc4-6c1f-4879-96d5-791bb49d4d7f");

    @Accessor
    public abstract int getExperienceLevel();

    protected LevelUpMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // When player is loaded (in case of config changes or newly added mod..)
    // @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    // private void constructorInj(CallbackInfo info) {
    // applyHealthModifier();
    // }

    @Inject(at = @At("HEAD"), method = "tick")
    private void constructorInj(CallbackInfo info) {
        if (this.firstUpdate) {
            LevelHealthMod.LOGGER.info("First update? " + this.firstUpdate);
            applyHealthModifier();
        }
    }

    // When level is changed
    @Inject(at = @At("TAIL"), method = "addExperienceLevels")
    private void addExperienceLevelsInj(CallbackInfo info) {
        applyHealthModifier();
    }

    // @Inject(at = @At("HEAD"), method = "createPlayerAttributes", cancellable =
    // true)
    // private static void
    // attr(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
    // info.setReturnValue(LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
    // 1.0)
    // .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.10000000149011612)
    // .add(EntityAttributes.GENERIC_ATTACK_SPEED).add(EntityAttributes.GENERIC_LUCK)
    // .add(EntityAttributes.GENERIC_MAX_HEALTH, 2));
    // }

    private void applyHealthModifier() {
        LevelHealthMod.LOGGER.info("Current level is " + this.getExperienceLevel());

        EntityAttributeInstance instance = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (instance == null) {
            return;
        }

        var config = LevelHealthMod.config;

        int baseHealth = this.defaultMaxHealth;
        int targetHealth = config.minHealth + (this.getExperienceLevel() / config.levelInterval) * config.levelHealth;
        int healthDiff = targetHealth - baseHealth;

        instance.tryRemoveModifier(MODIFIER_ID);
        try {
        instance.addPersistentModifier(
                new EntityAttributeModifier(MODIFIER_ID, "levelIsHealth.healthModifier",
                        healthDiff,
                        Operation.ADDITION));
        } catch (Exception ex) {
            LevelHealthMod.LOGGER.info("Could not apply health modifier (probably already applied)");
        }
    }

}
