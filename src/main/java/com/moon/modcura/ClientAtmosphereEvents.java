
package com.moon.modcura;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = ModCura.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientAtmosphereEvents {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            Level level = mc.level;

            // Verifica se está no cliente, tem jogador e mundo
            if (player == null || level == null || !level.isClientSide) {
                return;
            }

            // Verifica o horário do dia usando dayTime (13000 a 23000 é noite)
            long dayTime = level.getDayTime() % 24000;
            boolean isNight = dayTime >= 13000 && dayTime < 23000;

            if (!isNight) {
                return;
            }

            // ===== CAMADA 1: FUMAÇA BRANCA DENSA AO FUNDO =====
            for (int i = 0; i < 25; i++) {
                double x = player.getX() + (RANDOM.nextDouble() - 0.5D) * 32.0D;
                double y = player.getY() + (RANDOM.nextDouble() - 0.5D) * 12.0D;
                double z = player.getZ() + (RANDOM.nextDouble() - 0.5D) * 32.0D;

                level.addParticle(ParticleTypes.WHITE_ASH, x, y, z, 
                    (RANDOM.nextDouble() - 0.5D) * 0.02D, 
                    RANDOM.nextDouble() * 0.01D, 
                    (RANDOM.nextDouble() - 0.5D) * 0.02D);
            }

            // ===== CAMADA 2: FUMAÇA CINZENTA EXTRA =====
            for (int i = 0; i < 12; i++) {
                double x = player.getX() + (RANDOM.nextDouble() - 0.5D) * 28.0D;
                double y = player.getY() + (RANDOM.nextDouble() - 0.5D) * 10.0D;
                double z = player.getZ() + (RANDOM.nextDouble() - 0.5D) * 28.0D;

                level.addParticle(ParticleTypes.SMOKE, x, y, z, 
                    (RANDOM.nextDouble() - 0.5D) * 0.01D, 
                    RANDOM.nextDouble() * 0.005D, 
                    (RANDOM.nextDouble() - 0.5D) * 0.01D);
            }

            // ===== CAMADA 3: BRILHOS MÍSTICOS =====
            for (int i = 0; i < 6; i++) {
                double x = player.getX() + (RANDOM.nextDouble() - 0.5D) * 24.0D;
                double y = player.getY() + (RANDOM.nextDouble() - 0.5D) * 8.0D;
                double z = player.getZ() + (RANDOM.nextDouble() - 0.5D) * 24.0D;

                if (RANDOM.nextFloat() < 0.3F) {
                    level.addParticle(ParticleTypes.WITCH, x, y, z, 0.0D, 0.0D, 0.0D);
                }

                if (RANDOM.nextFloat() < 0.15F) {
                    level.addParticle(ParticleTypes.SOUL, x, y, z, 0.0D, 0.01D, 0.0D);
                }
            }
        }
    }
}