package com.moon.modcura.entity;

import com.moon.modcura.ModCura;
import com.moon.modcura.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LuaMob extends Monster {  // Herda de Monster para ser hostil

    public LuaMob(EntityType<? extends LuaMob> type, Level level) {
        super(type, level);
    }

    // Atributos básicos (saúde, velocidade, dano etc.)
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)      // Saúde média
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        // Tenta aplicar o dano e verifica se teve sucesso
        boolean hasHit = super.doHurtTarget(target);

        if (hasHit && target instanceof LivingEntity livingTarget) {
            // 1. Aplica Lentidão (Nível 2) por 5 segundos (100 ticks)
            livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1), this);

            // 2. Aplica Queda Lenta por 10 segundos (200 ticks)
            livingTarget.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0), this);

            // 3. Força o Knockback manualmente (o valor 3.0D é bem alto)
            double d0 = -Math.sin(this.getYRot() * (float)Math.PI / 180.0F);
            double d1 = Math.cos(this.getYRot() * (float)Math.PI / 180.0F);
            livingTarget.knockback(3.0D, d0, d1);

            // Opcional: Empurra um pouco para cima para o efeito de Queda Lenta ser notado na hora
            livingTarget.setDeltaMovement(livingTarget.getDeltaMovement().add(0, 0.4D, 0));

            // Avisa ao cliente que a entidade mudou de velocidade (evita "lag" visual do player)
            livingTarget.hasImpulse = true;
        }

        return hasHit;
    }


    // Goals (comportamento)
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Ataca jogadores
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // Drops customizados ao morrer
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        // Drop 0-2 cristais (aumenta com looting)
        int count = this.random.nextInt(3) + (looting > 0 ? this.random.nextInt(looting + 1) : 0);
        for (int i = 0; i < count; i++) {
            this.spawnAtLocation(new ItemStack(ModItems.CRISTAL_LUA.get()));
        }
    }

    // Som de morte (opcional: reuse de zombie)
    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.ZOMBIE_DEATH;
    }

    // Som de dano
    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source) {
        return net.minecraft.sounds.SoundEvents.ZOMBIE_HURT;
    }
}