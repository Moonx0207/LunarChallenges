package com.moon.modcura;

import com.moon.modcura.entity.PaladinMob;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PaladinMobRenderer extends HumanoidMobRenderer<PaladinMob, HumanoidModel<PaladinMob>> {

    public PaladinMobRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(PaladinMob entity) {
        return new ResourceLocation(ModCura.MOD_ID, "textures/entity/paladin_mob.png");
    }
}
