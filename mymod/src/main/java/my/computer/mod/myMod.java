package my.computer.mod;

import my.computer.mod.Generic.propertiesGeneric;
import my.computer.mod.myBlock.myBlockRegistry;
import my.computer.mod.myItem.myItemRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(propertiesGeneric.modId)
public class myMod {
    public myMod() {
        myItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        myBlockRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
