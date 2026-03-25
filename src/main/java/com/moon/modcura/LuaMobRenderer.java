package com.moon.modcura;

import com.moon.modcura.entity.LuaMob;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;

public class LuaMobRenderer extends HumanoidMobRenderer<LuaMob, HumanoidModel<LuaMob>> {

    public LuaMobRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }
}
