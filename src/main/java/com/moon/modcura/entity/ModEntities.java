package com.moon.modcura.entity;

import com.moon.modcura.ModCura;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, ModCura.MOD_ID);

    public static final RegistryObject<EntityType<LuaMob>> LUA_MOB = ENTITIES.register("lua_mob",
            () -> EntityType.Builder.of(LuaMob::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)  // Tamanho parecido com zombie
                    .build("lua_mob"));

    public static final RegistryObject<EntityType<PaladinMob>> PALADIN_MOB = ENTITIES.register("paladin_mob",
            () -> EntityType.Builder.of(PaladinMob::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)  // Tamanho igual ao Enderman
                    .build("paladin_mob"));
}