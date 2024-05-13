package com.level_is_health.mixin;

import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.level_is_health.LevelHealthMod;

@Mixin(PlayerEntity.class)
public class LevelUpMixin {
	@Inject(at = @At("HEAD"), method = "addExperienceLevels")
	private void run(CallbackInfo info) {
		LevelHealthMod.LOGGER.info("Level up!!");
		// This code is injected into the start of MinecraftClient.run()V
	}
}
