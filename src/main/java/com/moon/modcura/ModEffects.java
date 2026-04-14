package com.moon.modcura;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registra os efeitos personalizados do mod
 */
public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModCura.MOD_ID);

    // Efeito de Maldição Noturna - causa dano lentamente durante a noite
    public static final RegistryObject<MobEffect> NIGHT_CURSE = MOB_EFFECTS.register(
            "night_curse",
            () -> new NightCurseEffect(MobEffectCategory.HARMFUL, 0x1a1a2e) // Azul escuro
    );
}
