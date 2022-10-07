package my.computer.mod.myTile;


import my.computer.mod.myBlock.myBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerBlockModel implements IBakedModel {
    IBakedModel defaultModel;
    public static ModelProperty<BlockState> COPIED_BLOCK = new ModelProperty<>();

    public ComputerBlockModel(IBakedModel existingModel) {
        defaultModel = existingModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
//        IBakedModel renderModel = defaultModel;
//        if (extraData.hasProperty(COPIED_BLOCK)) {
//            BlockState copiedBlock = extraData.getData(COPIED_BLOCK);
//            if (copiedBlock != null) {
//                Minecraft mc = Minecraft.getInstance();
//                BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
//                renderModel = blockRendererDispatcher.getModelForState(copiedBlock);
//            }
//        }
        return new ArrayList<>();
    }

    @Override
    public IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData tileData) {
        BlockState downBlockState = world.getBlockState(pos.down());
        ModelDataMap modelDataMap = new ModelDataMap.Builder().withInitial(COPIED_BLOCK, null).build();

        if (downBlockState.getBlock() == Blocks.AIR || downBlockState.getBlock() == myBlockRegistry.ModBlocks.MONITOR_NORMAL_BLOCK.get()) {
            return modelDataMap;
        }
        modelDataMap.setData(COPIED_BLOCK, downBlockState);
        return modelDataMap;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        throw new AssertionError("IBakedModel::getQuads should never be called, only IForgeBakedModel::getQuads");
    }

    @Override
    public boolean isAmbientOcclusion() {
        return defaultModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return defaultModel.isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return defaultModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return defaultModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return null;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return defaultModel.getOverrides();
    }
}
