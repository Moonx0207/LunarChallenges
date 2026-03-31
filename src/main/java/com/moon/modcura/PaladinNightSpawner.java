package com.moon.modcura;

import com.moon.modcura.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Random;

/**
 * Spawna Paladinos da Lua APENAS à noite PERTO DA NAVE
 */
@Mod.EventBusSubscriber(modid = ModCura.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PaladinNightSpawner {

    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;
    private static int paladinCount = 0;  // 🌙 Contador de paladinos ativos

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        // Checa a cada 40 ticks (2 segundos)
        tickCounter++;
        if (tickCounter < 40) {
            return;
        }
        tickCounter = 0;

        try {
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                Level level = server.overworld();
                if (level != null && !level.isClientSide) {
                    spawnPaladinsAtNight(level);
                }
            }
        } catch (Exception e) {
            System.err.println("[ModCura] Erro ao spawnar Paladinos: " + e.getMessage());
        }
    }

    private static void spawnPaladinsAtNight(Level level) {
        // Verifica se é NOITE (13000 a 23000 ticks)
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime < 23000;

        if (!isNight) {
            paladinCount = 0;  // Reset durante o dia
            return;
        }

        // 🌙 Máximo 3 paladinos por vez
        if (paladinCount < 3) {
            if (RANDOM.nextFloat() < 0.5F) {
                spawnPaladinNearShip(level);
                paladinCount++;
            }
        }
    }

    private static void spawnPaladinNearShip(Level level) {
        // Gera coordenadas aleatórias perto da nave (raio configurável)
        int distance = 15 + RANDOM.nextInt(25); // 15-40 blocos da nave
        double angle = Math.random() * Math.PI * 2;

        int spawnX = ShipStructureLoader.SHIP_X + (int) (Math.cos(angle) * distance);
        int spawnZ = ShipStructureLoader.SHIP_Z + (int) (Math.sin(angle) * distance);

        // Encontra altura válida
        int spawnY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnX, spawnZ);

        BlockPos spawnPos = new BlockPos(spawnX, spawnY + 1, spawnZ);

        // Valida se está dentro do mundo
        if (!level.isInWorldBounds(spawnPos)) {
            return;
        }

        try {
            Mob paladin = new com.moon.modcura.entity.PaladinMob(
                ModEntities.PALADIN_MOB.get(),
                level
            );

            paladin.setPos(spawnX, spawnY + 1, spawnZ);
            level.addFreshEntity(paladin);

            System.out.println("[ModCura] 🌙 Paladino spawnado perto da nave em: " + spawnX + ", " + (spawnY + 1) + ", " + spawnZ);
        } catch (Exception e) {
            System.err.println("[ModCura] Erro ao criar Paladino: " + e.getMessage());
        }
    }

    /**
     * Chamado quando um paladino morre - decrementa o contador
     */
    public static void onPaladinDeath() {
        if (paladinCount > 0) {
            paladinCount--;
        }
    }
}

