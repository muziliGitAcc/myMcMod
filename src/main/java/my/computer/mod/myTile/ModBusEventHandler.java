package my.computer.mod.myTile;

import my.computer.mod.myBlock.myBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//重写block的model
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModBusEventHandler {
    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        for (BlockState blockstate : myBlockRegistry.ModBlocks.MONITOR_NORMAL_BLOCK.get().getStateContainer().getValidStates()) {
            ModelResourceLocation modelResourceLocation = BlockModelShapes.getModelLocation(blockstate);
            IBakedModel existingModel = event.getModelRegistry().get(modelResourceLocation);

            ComputerBlockModel obsidianHiddenBlockModel = new ComputerBlockModel(existingModel);
            event.getModelRegistry().put(modelResourceLocation, obsidianHiddenBlockModel);
        }
    }
}

