package com.moon.modcura;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Efeito de Maldição Noturna - tira 1 coração de vida a cada 4 segundos (80 ticks)
 */
public class NightCurseEffect extends MobEffect {

    private static final int DAMAGE_INTERVAL = 80; // 80 ticks = 4 segundos
    private static final float DAMAGE_AMOUNT = 2.0F; // 1 coração = 2 pontos de dano

    public NightCurseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Amplifier 0 = nível 1 (dano normal)
        // Amplifier 1 = nível 2 (mais dano)
        // etc...

        // Calcula dano baseado no nível do efeito
        float damage = DAMAGE_AMOUNT * (amplifier + 1);

        // Aplica dano ao jogador usando DamageSource.MAGIC
        entity.hurt(DamageSource.MAGIC, damage);

        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Aplica o efeito a cada DAMAGE_INTERVAL ticks
        return duration % DAMAGE_INTERVAL == 0;
    }

    @Override
    public String getDescriptionId() {
        return "effect.modcura.night_curse";
    }
}
