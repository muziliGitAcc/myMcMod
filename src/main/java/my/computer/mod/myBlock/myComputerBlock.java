package my.computer.mod.myBlock;

import my.computer.mod.Generic.BlockGeneric;
import my.computer.mod.Generic.TileGeneric;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;

public class myComputerBlock extends BlockGeneric {



    public myComputerBlock( Properties settings, RegistryObject<? extends TileEntityType<? extends TileGeneric>> type )
    {
        super( settings, type );
//        // TODO: Test underwater - do we need isSolid at all?
//        setDefaultState( getStateContainer().getBaseState()
//                .with( FACING, Direction.NORTH )
//        );
    }

    private static final VoxelShape DEFAULT_SHAPE = VoxelShapes.create(
            0.5, 0.5, 0.5,
            0.875, 0.875, 0.875
    );

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return DEFAULT_SHAPE;
    }
}

