package my.computer.mod;

import my.computer.mod.Generic.propertiesGeneric;
import my.computer.mod.myBlock.myBlockRegistry;
import my.computer.mod.myEntity.MyEntities;
import my.computer.mod.myItem.myItemRegistry;
import my.computer.mod.myTile.myComputerTileTER;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(propertiesGeneric.modId)
public class myMod {
    public myMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        myItemRegistry.ITEMS.register(modEventBus);
        MyEntities.ENTITIES.register(modEventBus);
        myBlockRegistry.setup();
    }

}
