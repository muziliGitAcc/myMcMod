package my.computer.mod.myTile;

import com.mojang.blaze3d.matrix.MatrixStack;
import my.computer.mod.Generic.propertiesGeneric;
import my.computer.mod.myBlock.myBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.model.TransformationHelper;

public class myComputerTileTER extends TileEntityRenderer<myComputerTile> {
    public static final ResourceLocation ROBOT_ENTITY_TEXTURE = new ResourceLocation(propertiesGeneric.modId, "textures/entity/my_battery_entity.png");
    private final ModelRenderer group;
    private final ModelRenderer bone;
    public myComputerTileTER(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);

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
    public void render(myComputerTile tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
//        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
//        BlockState state = Blocks.CHEST.getDefaultState();
        BlockState state = myBlockRegistry.ModBlocks.MONITOR_NORMAL_BLOCK.get().getDefaultState();
        Minecraft mc = Minecraft.getInstance();
        matrixStackIn.push();
        group.render(matrixStackIn,bufferIn.getBuffer(RenderType.func_239274_p_()),combinedLightIn,combinedOverlayIn);
        matrixStackIn.translate( 0.5f, 0.5f, 0.5f );
        matrixStackIn.rotate( mc.getRenderManager().getCameraOrientation() );
        matrixStackIn.translate( -0.5f, -0.5f, -0.5f );
//        blockRenderer.renderBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.pop();

    }
}
