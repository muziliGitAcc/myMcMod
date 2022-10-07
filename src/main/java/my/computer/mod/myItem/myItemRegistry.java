package my.computer.mod.myItem;

import my.computer.mod.Generic.propertiesGeneric;
import my.computer.mod.myBlock.myBlockRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static my.computer.mod.Generic.propertiesGeneric.itemGroup;

public class myItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, propertiesGeneric.modId);
    public static RegistryObject<Item> myComputer = ITEMS.register("my_computer", myComputerItem::new);
    public static RegistryObject<Item> myComputerBlock = ITEMS.register("my_computer_block", () -> new BlockItem(myBlockRegistry.ModBlocks.MONITOR_NORMAL_BLOCK.get(), new Item.Properties().group(itemGroup)));

}
