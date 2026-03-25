package com.moon.modcura;  

import com.moon.modcura.entity.ModEntities;
import com.moon.modcura.entity.LuaMob;
import com.moon.modcura.entity.PaladinMob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(ModCura.MOD_ID)
public class ModCura {
    public static final String MOD_ID = "modcura";

    private static final Logger LOGGER = LogUtils.getLogger();

    public ModCura() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, BiomeLoadingEvent.class, event -> {
            if (event.getCategory() == net.minecraft.world.level.biome.Biome.BiomeCategory.PLAINS || event.getCategory() == net.minecraft.world.level.biome.Biome.BiomeCategory.FOREST) {
                event.getSpawns().addSpawn(MobCategory.MONSTER, new net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData(ModEntities.LUA_MOB.get(), 50, 1, 3));
                event.getSpawns().addSpawn(MobCategory.MONSTER, new net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData(ModEntities.PALADIN_MOB.get(), 20, 1, 1));
            }
        });
        // Registra os itens
        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::entityAttributeCreation);
        modEventBus.addListener(this::clientSetup);
        // Eventos do Forge (ex: server start)
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Mod Cura da 0Lua carregado!");
    }

    private void entityAttributeCreation(final EntityAttributeCreationEvent event) {
        event.put(ModEntities.LUA_MOB.get(), LuaMob.createAttributes().build());
        event.put(ModEntities.PALADIN_MOB.get(), PaladinMob.createAttributes().build());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.LUA_MOB.get(), LuaMobRenderer::new);
        EntityRenderers.register(ModEntities.PALADIN_MOB.get(), PaladinMobRenderer::new);
    }
}