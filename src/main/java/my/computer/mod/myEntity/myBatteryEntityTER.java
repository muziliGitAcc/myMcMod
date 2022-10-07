package my.computer.mod.myEntity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class myBatteryEntityTER extends EntityRenderer<myBatteryEntity> {
    private final customModel model = new customModel();;
    public myBatteryEntityTER(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(myBatteryEntity entity) {
        return customModel.ROBOT_ENTITY_TEXTURE;
    }

    @Override
    public void render(final myBatteryEntity entity, final float entityYaw, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer buffer, final int packedLight) {
        matrixStack.push();
        final IVertexBuilder builder = buffer.getBuffer(model.getRenderType(getEntityTexture(entity)));
        model.render(matrixStack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        matrixStack.pop();
    }
}
