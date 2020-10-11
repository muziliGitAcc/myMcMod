package my.computer.mod.myBlock;


import my.computer.mod.Generic.propertiesGeneric;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class myBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, propertiesGeneric.modId);
    public static RegistryObject<Block> myComputerBlock = BLOCKS.register("my_computer", myComputerBlock::new);
}

