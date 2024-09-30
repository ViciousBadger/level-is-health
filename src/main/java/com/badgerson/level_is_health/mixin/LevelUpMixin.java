package com.badgerson.level_is_health.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.badgerson.level_is_health.LevelHealthMod;

@Mixin(PlayerEntity.class)
public abstract class LevelUpMixin extends LivingEntity {

    private static final Identifier ATTR_ID = Identifier.of(LevelHealthMod.MOD_ID, "extra_health");

    @Accessor
    public abstract int getExperienceLevel();

    protected LevelUpMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // When joining a singleplayer world or server
    @Inject(at = @At("HEAD"), method = "tick")
    private void constructorInj(CallbackInfo info) {
        if (this.firstUpdate) {
            applyHealthModifier();
        }
    }

    // When level is changed
    @Inject(at = @At("TAIL"), method = "addExperienceLevels")
    private void addExperienceLevelsInj(CallbackInfo info) {
        applyHealthModifier();
    }

    // When item is enchanted (decrementing level)
    @Inject(at = @At("TAIL"), method = "applyEnchantmentCosts")
    private void applyEnchantmentCostsInj(CallbackInfo info) {
        applyHealthModifier();
    }

    private void applyHealthModifier() {
        EntityAttributeInstance instance = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (instance == null) {
            return;
        }

        var config = LevelHealthMod.config;

        int baseHealth = this.defaultMaxHealth;
        int targetHealth = config.minHealth + (this.getExperienceLevel() / config.levelInterval) * config.levelHealth;
        int healthDiff = targetHealth - baseHealth;

        try {
            instance.overwritePersistentModifier(
                    new EntityAttributeModifier(ATTR_ID,
                            healthDiff,
                            Operation.ADD_VALUE));
        } catch (Exception ex) {
            LevelHealthMod.LOGGER.error("[Level is health] Could not apply extra health modifier for a player... Please report the following message to the mod author:\n" + ex.toString());
        }
    }

}
