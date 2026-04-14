package com.moon.modcura;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Aplica a Maldição Noturna aos jogadores durante a noite
 */
@Mod.EventBusSubscriber(modid = ModCura.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NightCurseApplier {


    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 20; // Verifica a cada 1 segundo (20 ticks)
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;
        
        // Itera sobre todos os jogadores
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(NightCurseApplier::applyNightCurse);
        }
    }
    
    /**
     * Aplica ou mantém a Maldição Noturna durante a noite
     */
    private static void applyNightCurse(Player player) {
        if (player == null || player.level.isClientSide) return;
        
        long dayTime = player.level.getDayTime() % 24000; // 24000 ticks = 1 dia completo
        // 0-12000 = dia (0-12000 ticks)
        // 12000-24000 = noite (12000-24000 ticks)
        
        boolean isNight = dayTime >= 12000 && dayTime < 24000;
        
        if (isNight) {
            // Aplica a maldição noturna se o jogador não tiver
            if (!player.hasEffect(ModEffects.NIGHT_CURSE.get())) {
                // Duração infinita (será removida quando passar para o dia)
                // Amplifier 0 = nível 1 (dano normal)
                player.addEffect(new MobEffectInstance(
                        ModEffects.NIGHT_CURSE.get(),
                        Integer.MAX_VALUE,  // Duração infinita
                        0,                   // Amplifier 0 = nível 1
                        false,              // Não é ambiente
                        false               // Não mostra partículas
                ));
            }
        } else {
            // Remove a maldição durante o dia
            if (player.hasEffect(ModEffects.NIGHT_CURSE.get())) {
                player.removeEffect(ModEffects.NIGHT_CURSE.get());
            }
        }
    }
}
