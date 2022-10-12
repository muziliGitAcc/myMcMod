package my.computer.mod.myEntity;// Made with Blockbench 4.4.2
// Exported for Minecraft version 1.15 - 1.16 with MCP mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import my.computer.mod.Generic.propertiesGeneric;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import java.util.function.Function;

public class customModel extends EntityModel<myBatteryEntity> {
	public static final ResourceLocation ROBOT_ENTITY_TEXTURE = new ResourceLocation(propertiesGeneric.modId, "textures/entity/my_battery_entity.png");
	private final ModelRenderer group;
	private final ModelRenderer bone;
	public customModel() {
		group = new ModelRenderer(64,64,0,0);
		group.setTextureSize(64,64);
		group.setRotationPoint(0.0F, 24.0F, 0.0F);
		group.setTextureOffset(0, 0).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, 0.0F, false);

		bone = new ModelRenderer(64,64,0,0);
		bone.setRotationPoint(3.66F, -3.0F, 0.0F);
		group.addChild(bone);
		bone.setTextureOffset(0, 14).addBox(-5.66F, -4.0F, -4.0F, 8.0F, 3.0F, 8.0F, 0.0F, false);
		bone.setTextureOffset(24, 18).addBox(-13.66F, -3.0F, -3.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(24, 14).addBox(-13.66F, -3.0F, 1.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);
		bone.setTextureOffset(0, 0).addBox(-0.9933F, -1.0333F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(myBatteryEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		matrixStackIn.push();

//		matrixStackIn.translate(0.0F,0.5F,0.0F);
//		matrixStackIn.rotate(Vector3f.XP.rotationDegrees( 180.0F));
		Minecraft mc = Minecraft.getInstance();
		matrixStackIn.rotate( mc.getRenderManager().getCameraOrientation() );
//		matrixStackIn.translate(0.5F,-0.5F,0.0F);
		group.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn);
		matrixStackIn.pop();
	}
}