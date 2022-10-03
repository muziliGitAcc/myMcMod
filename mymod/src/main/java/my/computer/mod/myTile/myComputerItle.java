package my.computer.mod.myTile;

import my.computer.mod.Generic.TileGeneric;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class myComputerItle extends TileGeneric {
    public myComputerItle(TileEntityType<? extends TileGeneric> type) {
        super(type);
    }

    @Nonnull
    @Override
    public ActionResultType onActivate(PlayerEntity player, Hand hand, BlockRayTraceResult hit )
    {
        TranslationTextComponent translationTextComponent = new TranslationTextComponent("message mymodmsg", 0);
        player.sendStatusMessage(translationTextComponent, false);
        return ActionResultType.SUCCESS;
    }


//    @Nonnull
//    @Override
//    public ActionResultType onActivate(PlayerEntity player, Hand hand, BlockRayTraceResult hit )
//    {
//        // Apply dye
//        ItemStack currentItem = player.getHeldItem( hand );
//        if( !currentItem.isEmpty() )
//        {
//            if( currentItem.getItem() instanceof DyeItem)
//            {
//                // Dye to change turtle colour
//                if( !getWorld().isRemote )
//                {
//                    DyeColor dye = ((DyeItem) currentItem.getItem()).getDyeColor();
//                    if( m_brain.getDyeColour() != dye )
//                    {
//                        m_brain.setDyeColour( dye );
//                        if( !player.isCreative() )
//                        {
//                            currentItem.shrink( 1 );
//                        }
//                    }
//                }
//                return ActionResultType.SUCCESS;
//            }
//            else if( currentItem.getItem() == Items.WATER_BUCKET && m_brain.getColour() != -1 )
//            {
//                // Water to remove turtle colour
//                if( !getWorld().isRemote )
//                {
//                    if( m_brain.getColour() != -1 )
//                    {
//                        m_brain.setColour( -1 );
//                        if( !player.isCreative() )
//                        {
//                            player.setHeldItem( hand, new ItemStack( Items.BUCKET ) );
//                            player.inventory.markDirty();
//                        }
//                    }
//                }
//                return ActionResultType.SUCCESS;
//            }
//        }
//
//        // Open GUI or whatever
//        return super.onActivate( player, hand, hit );
//    }
}
