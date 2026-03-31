package com.moon.modcura.entity;

import com.moon.modcura.ModCura;
import com.moon.modcura.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class PaladinMob extends Monster {

    public PaladinMob(EntityType<? extends PaladinMob> type, Level level) {
        super(type, level);
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.SABRE_LUNAR.get()));
    }

    // Atributos
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 75.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    // Goals
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new RandomStrollGoal(this, 0.8D));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }


    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hasHit = super.doHurtTarget(target);

        if (hasHit && target instanceof LivingEntity livingTarget) {
            // Efeitos de Status
            livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1), this);
            livingTarget.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0), this);

            // Física: Knockback + Impulso para o alto
            double d0 = -Math.sin(this.getYRot() * (float)Math.PI / 180.0F);
            double d1 = Math.cos(this.getYRot() * (float)Math.PI / 180.0F);
            livingTarget.knockback(3.0D, d0, d1);
            livingTarget.setDeltaMovement(livingTarget.getDeltaMovement().add(0, 0.5D, 0));
            livingTarget.hasImpulse = true;

            // --- ADICIONANDO O VISUAL E SOM ---
            // Certifique-se de usar this.level() com parênteses se estiver na 1.20+
            if (!this.level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level;

                // Explosão de Partículas aumentada (Aumentei a contagem de 20 para 60 e o spread para 0.5)
                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, livingTarget.getX(), livingTarget.getY(0.5D), livingTarget.getZ(), 60, 0.5, 0.5, 0.5, 0.15);

                // Fumaça aumentada (Aumentei de 10 para 40 e a dispersão para 0.6)
                serverLevel.sendParticles(ParticleTypes.CLOUD, livingTarget.getX(), livingTarget.getY(0.5D), livingTarget.getZ(), 40, 0.6, 0.6, 0.6, 0.1);

                // Som de Explosão de TNT
                serverLevel.playSound(null, livingTarget.getX(), livingTarget.getY(), livingTarget.getZ(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.5F, 1.2F);
            }
        }

        return hasHit;
    }
    // Som de morte
    @Override
    protected net.minecraft.sounds.SoundEvent getDeathSound() {
        return net.minecraft.sounds.SoundEvents.PLAYER_DEATH;
    }

    // Som de dano
    @Override
    protected net.minecraft.sounds.SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return net.minecraft.sounds.SoundEvents.PLAYER_HURT;
    }

    // Drops customizados ao morrer
    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        // 🌙 Dropa 5 Cristais da Lua quando morre
        for (int i = 0; i < 5; i++) {
            this.spawnAtLocation(new ItemStack(ModItems.CRISTAL_LUA.get()));
        }
        
        // Decrementa contador de paladinos vivos
        com.moon.modcura.PaladinNightSpawner.onPaladinDeath();
    }

    // Spawn controlado pelo SpawnPaladinHandler que detecta marcadores de nave
    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType) {
        return super.checkSpawnRules(level, spawnType);
    }
}
