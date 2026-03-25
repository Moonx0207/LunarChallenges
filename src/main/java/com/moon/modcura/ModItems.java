package com.moon.modcura;  // seu pacote novo!

import com.moon.modcura.entity.ModEntities;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.entity.LivingEntity;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ModCura.MOD_ID);  // Agora usa ModCura.MOD_ID

    public static final RegistryObject<Item> CURA_MOON = ITEMS.register("cura_moon",
            () -> new Item(new Item.Properties()
                    .tab(CreativeModeTab.TAB_FOOD)
                    .food(new FoodProperties.Builder()
                            .nutrition(8)                        // Mais fome restaurada
                            .saturationMod(2.0f)                 // Saturação altíssima
                            .alwaysEat()                         // Come mesmo full
                            .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 3), 1.0f)  // Amplifier 3 = nível 4
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200, 3), 1.0f)  // 1200 ticks = 60s, amplifier 3 = nível 4
                            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 3600, 5), 1.0f)  // 3600 ticks = 3 min, amplifier 5 = nível 6
                            .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 3600, 4), 1.0f)  // Amplifier 2 = nível 3, +12 corações max

                            .build())
            ));

    public static final RegistryObject<Item> CRISTAL_LUA = ITEMS.register("cristal_lua",
            () -> new Item(new Item.Properties()
                    .tab(CreativeModeTab.TAB_MISC)  // Aba Miscellaneous
                    .stacksTo(8)                   // Pode stackar até 8
            ) {

                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                    ItemStack itemstack = player.getItemInHand(hand);
                    if (!level.isClientSide) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0));  // 30 segundos (600 ticks)
                        player.playSound(net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);  // Consome 1 item (se não for criativo)
                        }
                    }
                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                }


                @Override
                public UseAnim getUseAnimation(ItemStack stack) {
                    return UseAnim.BOW;
                }
            }
    );

    public static final RegistryObject<Item> MOON_PALADIN = ITEMS.register("moon_paladin",
            () -> new net.minecraftforge.common.ForgeSpawnEggItem(ModEntities.PALADIN_MOB, 0xFFFFFF, 0xFFD700,  // Cores: fundo branco, pontos dourados
                    new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<Item> SABRE_LUNAR = ITEMS.register("sabre_lunar",

            () -> new SwordItem(Tiers.IRON, 10, -2.4F, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)) {
                @Override
                public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                    // Aplica lentidão e efeito de "lua" (queda lenta)
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1)); // Lentidão nível 2 por 10 segundos
                    target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0)); // Queda lenta por 10 segundos
                    return super.hurtEnemy(stack, target, attacker);
                }
            });

}