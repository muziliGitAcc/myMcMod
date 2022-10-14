package my.computer.mod.myTile;

import com.mojang.blaze3d.matrix.MatrixStack;
import my.computer.mod.Generic.propertiesGeneric;
import my.computer.mod.myBlock.myBlockRegistry;
import my.computer.mod.myEntity.customModel;
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
    public static final ResourceLocation ROBOT_ENTITY_TEXTURE_02 = new ResourceLocation(propertiesGeneric.modId, "textures/block/my_computer_block_02.png");
    private final customModel model = new customModel();;
    private final ModelRenderer group;
    public myComputerTileTER(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);

        group = new ModelRenderer(16,16,0,0);
        group.setTextureSize(16,16);
        group.setRotationPoint(0.0F, 24.0F, 0.0F);
        group.setTextureOffset(0, 5).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        group.setTextureOffset(0, 0).addBox(-2.0F, -3.0F, 1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);
    }

    @Override
    public void render(myComputerTile tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
        BlockState state1 = Blocks.CHEST.getDefaultState();
        BlockState state = myBlockRegistry.ModBlocks.MONITOR_NORMAL_BLOCK.get().getDefaultState();
        Minecraft mc = Minecraft.getInstance();
        matrixStackIn.push();
        matrixStackIn.translate( 0.5f, 0.5f, 0.5f );
        matrixStackIn.rotate( mc.getRenderManager().getCameraOrientation() );
        matrixStackIn.translate( -0.5f, -0.5f, -0.5f );
        blockRenderer.renderBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.pop();

        matrixStackIn.push();
        //在防止方块的-1处渲染

        matrixStackIn.translate( 0.5f, 0.5f, 0.5f );
        matrixStackIn.rotate( mc.getRenderManager().getCameraOrientation() );
        matrixStackIn.translate( -0.5f, -0.5f, -0.5f );
        //bufferIn.getBuffer(RenderTypeLookup.func_239220_a_(state, false)) 这个getBuffer中的参数 应该有限制 其他的有可能渲染不出来
        //model.getRenderType(ROBOT_ENTITY_TEXTURE_02) 用这种方式获取的应该可以直接使用，其他方式应该都会有贴图异常的问题
        group.render(matrixStackIn, bufferIn.getBuffer(model.getRenderType(ROBOT_ENTITY_TEXTURE_02)), combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);
        matrixStackIn.translate( -1.0f, -0.0f, -0.0f );
        matrixStackIn.pop();


    }
}
