package my.computer.mod.myBlock;


import my.computer.mod.Generic.propertiesGeneric;
import my.computer.mod.myTile.myComputerTile;
import my.computer.util.FixedPointTileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

public class myBlockRegistry {
    public static final class ModBlocks{
        private static Block.Properties properties()
        {
            return Block.Properties.create( Material.ROCK ).hardnessAndResistance( 2 ).notSolid();
        }
        static final DeferredRegister<Block> BLOCKS = DeferredRegister.create( ForgeRegistries.BLOCKS, propertiesGeneric.modId );
        public static final RegistryObject<myComputerBlock> MONITOR_NORMAL_BLOCK = BLOCKS.register( "my_computer_block",
                () -> new myComputerBlock( properties(), ModTiles.MONITOR_NORMAL_ENTITY ) );
    }

    public static class ModTiles{
        static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create( ForgeRegistries.TILE_ENTITIES, propertiesGeneric.modId);
        private static <T extends TileEntity> RegistryObject<TileEntityType<T>> ofBlock(RegistryObject<? extends Block> block, Function<TileEntityType<T>, T> factory )
        {
            return TILES.register( block.getId().getPath(), () -> FixedPointTileEntityType.create( block, factory ) );
        }
        public static final RegistryObject<TileEntityType<myComputerTile>> MONITOR_NORMAL_ENTITY =
                ofBlock( ModBlocks.MONITOR_NORMAL_BLOCK, myComputerTile::new);
    }



    public static void setup()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register( bus );
        ModTiles.TILES.register( bus );
    }
}

