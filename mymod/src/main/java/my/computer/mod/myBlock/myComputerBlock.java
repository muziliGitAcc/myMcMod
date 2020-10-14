package my.computer.mod.myBlock;

import my.computer.mod.Generic.BlockGeneric;
import my.computer.mod.Generic.TileGeneric;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.RegistryObject;

import static net.minecraft.block.WallSignBlock.FACING;

public class myComputerBlock extends BlockGeneric {



    public myComputerBlock( Properties settings, RegistryObject<? extends TileEntityType<? extends TileGeneric>> type )
    {
        super( settings, type );
//        // TODO: Test underwater - do we need isSolid at all?
//        setDefaultState( getStateContainer().getBaseState()
//                .with( FACING, Direction.NORTH )
//        );
    }
}

