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
 * Carrega a nave .schematic no mundo no primeiro tick
 */
@Mod.EventBusSubscriber(modid = ModCura.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShipStructureLoader {
    
    private static boolean shipLoaded = false;
    
    // 🚀 COORDENADAS DA NAVE - MUDE AQUI PARA MUDAR A POSIÇÃO
    public static final int SHIP_X = 600;
    public static final int SHIP_Y = 150;  // 🚀 Nave bem alto no céu
    public static final int SHIP_Z = 780;

    // Raio ao redor da nave onde paladinos podem spawnar (em cima da nave)
    public static final int SHIP_SPAWN_RADIUS = 30;
    public static final int SHIP_SPAWN_HEIGHT = SHIP_Y + 5;  // 5 blocos acima da nave

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
        try {
            // Tenta carregar o arquivo .nbt
            InputStream stream = ShipStructureLoader.class.getResourceAsStream(
                "/data/modcura/structures/nave_paladin.nbt"
            );
            
            if (stream == null) {
                System.out.println("[ModCura] ⚠️ Arquivo 'nave_paladin.nbt' não encontrado - usando nave padrão");
                createDefaultShip(level);
                return;
            }
            
            // Lê o arquivo NBT
            CompoundTag tag = NbtIo.readCompressed(stream);
            stream.close();
            
            // Cria template e coloca no mundo
            StructureTemplate template = new StructureTemplate();
            template.load(tag);
            
            BlockPos pos = new BlockPos(SHIP_X, SHIP_Y, SHIP_Z);
            StructurePlaceSettings settings = new StructurePlaceSettings();
            settings.setMirror(Mirror.NONE);
            settings.setRotation(Rotation.NONE);
            
            template.placeInWorld(level, pos, pos, settings, new java.util.Random(), 1);
            System.out.println("[ModCura] ✅ Nave .nbt carregada com sucesso!");

        } catch (Exception e) {
            System.out.println("[ModCura] ⚠️ Erro ao carregar nave .nbt - usando nave padrão");
            System.out.println("[ModCura] ℹ️ Motivo: " + e.getMessage());
            createDefaultShip(level);
        }
    }
    
    /**
     * Cria uma nave padrão simples se o arquivo schematic for inválido
     */
    private static void createDefaultShip(ServerLevelAccessor level) {
        BlockPos pos = new BlockPos(SHIP_X, SHIP_Y, SHIP_Z);
        
        // Cria uma estrutura simples de teste (plataforma 10x10)
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                BlockPos blockPos = pos.offset(x, 0, z);
                level.setBlock(blockPos, net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(), 3);
            }
        }
        
        System.out.println("[ModCura] ✅ Nave padrão criada em: " + SHIP_X + ", " + SHIP_Y + ", " + SHIP_Z);
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

