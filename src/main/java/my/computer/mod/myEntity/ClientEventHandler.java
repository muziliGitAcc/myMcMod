package my.computer.mod.myEntity;

import my.computer.mod.myBlock.myBlockRegistry;
import my.computer.mod.myTile.myComputerTileTER;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            System.out.println("Dsadhbsakhfklgljkdlkjgfdlkj");
            RenderingRegistry.registerEntityRenderingHandler(MyEntities.ROBOT.get(), myBatteryEntityTER::new);
        });
    }
}
