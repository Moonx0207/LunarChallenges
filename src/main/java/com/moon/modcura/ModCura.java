package com.moon.modcura;

import com.moon.modcura.entity.ModEntities;
import com.moon.modcura.entity.LuaMob;
import com.moon.modcura.entity.PaladinMob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
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


        // Registra Entidades, Itens e Efeitos
        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);

        modEventBus.addListener(this::entityAttributeCreation);
        modEventBus.addListener(this::clientSetup);

        // Eventos do Forge (A classe ClientAtmosphereEvents se registra sozinha via @EventBusSubscriber)
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Mod Cura da 0Lua carregado!");
    }

    private void entityAttributeCreation(final EntityAttributeCreationEvent event) {
        // NÃO ESQUEÇA DO LUA_MOB AQUI!
        event.put(ModEntities.LUA_MOB.get(), LuaMob.createAttributes().build());
        event.put(ModEntities.PALADIN_MOB.get(), PaladinMob.createAttributes().build());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // Registra os renders dos dois mobs
        EntityRenderers.register(ModEntities.LUA_MOB.get(), LuaMobRenderer::new);
        EntityRenderers.register(ModEntities.PALADIN_MOB.get(), PaladinMobRenderer::new);
    }
}



