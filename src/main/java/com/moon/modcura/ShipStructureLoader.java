package com.moon.modcura;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.InputStream;

/**
 * Carrega a nave .nbt no mundo no primeiro tick
 */
@Mod.EventBusSubscriber(modid = ModCura.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShipStructureLoader {
    
    private static boolean shipLoaded = false;
    
    // 🚀 COORDENADAS DA NAVE - MUDE AQUI PARA MUDAR A POSIÇÃO
    public static final int SHIP_X = 100;
    public static final int SHIP_Y = 50;  // 🔥 Nave caída no chão
    public static final int SHIP_Z = 200;
    
    // Raio ao redor da nave onde paladinos podem spawnar
    public static final int SHIP_SPAWN_RADIUS = 40;
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        if (shipLoaded) return;
        
        try {
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                Level level = server.overworld();
                if (level != null && level instanceof ServerLevelAccessor) {
                    loadShipStructure((ServerLevelAccessor) level);
                    shipLoaded = true;
                    System.out.println("[ModCura] 🚀 Nave carregada em: " + SHIP_X + ", " + SHIP_Y + ", " + SHIP_Z);
                }
            }
        } catch (Exception e) {
            System.err.println("[ModCura] ❌ Erro ao carregar nave: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carrega a estrutura .nbt no mundo
     */
    public static void loadShipStructure(ServerLevelAccessor level) throws Exception {
        // Tenta carregar o arquivo .nbt
        InputStream stream = ShipStructureLoader.class.getResourceAsStream(
            "/data/modcura/structures/nave_paladin.nbt"
        );
        
        if (stream == null) {
            throw new RuntimeException("❌ Arquivo 'nave.nbt' não encontrado em /data/modcura/structures/");
        }
        
        // Lê o arquivo NBT
        CompoundTag tag = NbtIo.readCompressed(stream);
        stream.close();
        
        // Cria template
        StructureTemplate template = new StructureTemplate();
        template.load(tag);
        
        // Coloca a estrutura no mundo
        BlockPos pos = new BlockPos(SHIP_X, SHIP_Y, SHIP_Z);
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setMirror(Mirror.NONE);
        settings.setRotation(Rotation.NONE);
        
        // Usa o método correto para MC 1.18.2
        template.placeInWorld(level, pos, pos, settings, new java.util.Random(), 1);
    }
    
    /**
     * Verifica se um ponto está perto da nave
     */
    public static boolean isNearShip(int x, int z) {
        double dx = x - SHIP_X;
        double dz = z - SHIP_Z;
        double distance = Math.sqrt(dx * dx + dz * dz);
        return distance <= SHIP_SPAWN_RADIUS;
    }
}

