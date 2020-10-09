/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.turtle.blocks;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.common.TileGeneric;
import dan200.computercraft.shared.computer.blocks.ComputerPeripheral;
import dan200.computercraft.shared.computer.blocks.ComputerProxy;
import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.apis.TurtleAPI;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import dan200.computercraft.shared.turtle.inventory.ContainerTurtle;
import dan200.computercraft.shared.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class TileTurtle extends TileComputerBase implements ITurtleTile, DefaultInventory
{
    public static final int INVENTORY_SIZE = 16;
    public static final int INVENTORY_WIDTH = 4;
    public static final int INVENTORY_HEIGHT = 4;

    enum MoveState
    {
        NOT_MOVED,
        IN_PROGRESS,
        MOVED
    }

    private final NonNullList<ItemStack> m_inventory = NonNullList.withSize( INVENTORY_SIZE, ItemStack.EMPTY );
    private final NonNullList<ItemStack> m_previousInventory = NonNullList.withSize( INVENTORY_SIZE, ItemStack.EMPTY );
    private final IItemHandlerModifiable m_itemHandler = new InvWrapper( this );
    private LazyOptional<IItemHandlerModifiable> itemHandlerCap;
    private boolean m_inventoryChanged = false;
    private TurtleBrain m_brain = new TurtleBrain( this );
    private MoveState m_moveState = MoveState.NOT_MOVED;
    private LazyOptional<IPeripheral> peripheral;

    public TileTurtle( TileEntityType<? extends TileGeneric> type, ComputerFamily family )
    {
        super( type, family );
    }

    private boolean hasMoved()
    {
        return m_moveState == MoveState.MOVED;
    }

    @Override
    protected ServerComputer createComputer( int instanceID, int id )
    {
        ServerComputer computer = new ServerComputer(
            getWorld(), id, label, instanceID, getFamily(),
            ComputerCraft.turtleTermWidth, ComputerCraft.turtleTermHeight
        );
        computer.setPosition( getPos() );
        computer.addAPI( new TurtleAPI( computer.getAPIEnvironment(), getAccess() ) );
        m_brain.setupComputer( computer );
        return computer;
    }

    public ComputerProxy createProxy()
    {
        return m_brain.getProxy();
    }

    @Override
    public void destroy()
    {
        if( !hasMoved() )
        {
            // Stop computer
            super.destroy();

            // Drop contents
            if( !getWorld().isRemote )
            {
                int size = getSizeInventory();
                for( int i = 0; i < size; i++ )
                {
                    ItemStack stack = getStackInSlot( i );
                    if( !stack.isEmpty() )
                    {
                        WorldUtil.dropItemStack( stack, getWorld(), getPos() );
                    }
                }
            }
        }
        else
        {
            // Just turn off any redstone we had on
            for( Direction dir : DirectionUtil.FACINGS )
            {
                RedstoneUtil.propagateRedstoneOutput( getWorld(), getPos(), dir );
            }
        }
    }

    @Override
    protected void unload()
    {
        if( !hasMoved() )
        {
            super.unload();
        }
    }

    @Override
    protected void invalidateCaps()
    {
        super.invalidateCaps();
        itemHandlerCap = CapabilityUtil.invalidate( itemHandlerCap );
        peripheral = CapabilityUtil.invalidate( peripheral );
    }

    @Nonnull
    @Override
    public ActionResultType onActivate( PlayerEntity player, Hand hand, BlockRayTraceResult hit )
    {
        // Apply dye
        ItemStack currentItem = player.getHeldItem( hand );
        if( !currentItem.isEmpty() )
        {
            if( currentItem.getItem() instanceof DyeItem )
            {
                // Dye to change turtle colour
                if( !getWorld().isRemote )
                {
                    DyeColor dye = ((DyeItem) currentItem.getItem()).getDyeColor();
                    if( m_brain.getDyeColour() != dye )
                    {
                        m_brain.setDyeColour( dye );
                        if( !player.isCreative() )
                        {
                            currentItem.shrink( 1 );
                        }
                    }
                }
                return ActionResultType.SUCCESS;
            }
            else if( currentItem.getItem() == Items.WATER_BUCKET && m_brain.getColour() != -1 )
            {
                // Water to remove turtle colour
                if( !getWorld().isRemote )
                {
                    if( m_brain.getColour() != -1 )
                    {
                        m_brain.setColour( -1 );
                        if( !player.isCreative() )
                        {
                            player.setHeldItem( hand, new ItemStack( Items.BUCKET ) );
                            player.inventory.markDirty();
                        }
                    }
                }
                return ActionResultType.SUCCESS;
            }
        }

        // Open GUI or whatever
        return super.onActivate( player, hand, hit );
    }

    @Override
    protected boolean canNameWithTag( PlayerEntity player )
    {
        return true;
    }

    @Override
    protected double getInteractRange( PlayerEntity player )
    {
        return 12.0;
    }

    @Override
    public void tick()
    {
        super.tick();
        m_brain.update();
        if( !getWorld().isRemote && m_inventoryChanged )
        {
            ServerComputer computer = getServerComputer();
            if( computer != null ) computer.queueEvent( "turtle_inventory" );

            m_inventoryChanged = false;
            for( int n = 0; n < getSizeInventory(); n++ )
            {
                m_previousInventory.set( n, getStackInSlot( n ).copy() );
            }
        }
    }

    @Override
    protected void updateBlockState( ComputerState newState )
    {
    }

    @Override
    public void onNeighbourChange( @Nonnull BlockPos neighbour )
    {
        if( m_moveState == MoveState.NOT_MOVED ) super.onNeighbourChange( neighbour );
    }

    @Override
    public void onNeighbourTileEntityChange( @Nonnull BlockPos neighbour )
    {
        if( m_moveState == MoveState.NOT_MOVED ) super.onNeighbourTileEntityChange( neighbour );
    }

    public void notifyMoveStart()
    {
        if( m_moveState == MoveState.NOT_MOVED ) m_moveState = MoveState.IN_PROGRESS;
    }

    public void notifyMoveEnd()
    {
        // MoveState.MOVED is final
        if( m_moveState == MoveState.IN_PROGRESS ) m_moveState = MoveState.NOT_MOVED;
    }

    @Override
    public void read( @Nonnull BlockState state, @Nonnull CompoundNBT nbt )
    {
        super.read( state, nbt );

        // Read inventory
        ListNBT nbttaglist = nbt.getList( "Items", Constants.NBT.TAG_COMPOUND );
        m_inventory.clear();
        m_previousInventory.clear();
        for( int i = 0; i < nbttaglist.size(); i++ )
        {
            CompoundNBT tag = nbttaglist.getCompound( i );
            int slot = tag.getByte( "Slot" ) & 0xff;
            if( slot < getSizeInventory() )
            {
                m_inventory.set( slot, ItemStack.read( tag ) );
                m_previousInventory.set( slot, m_inventory.get( slot ).copy() );
            }
        }

        // Read state
        m_brain.readFromNBT( nbt );
    }

    @Nonnull
    @Override
    public CompoundNBT write( @Nonnull CompoundNBT nbt )
    {
        // Write inventory
        ListNBT nbttaglist = new ListNBT();
        for( int i = 0; i < INVENTORY_SIZE; i++ )
        {
            if( !m_inventory.get( i ).isEmpty() )
            {
                CompoundNBT tag = new CompoundNBT();
                tag.putByte( "Slot", (byte) i );
                m_inventory.get( i ).write( tag );
                nbttaglist.add( tag );
            }
        }
        nbt.put( "Items", nbttaglist );

        // Write brain
        nbt = m_brain.writeToNBT( nbt );

        return super.write( nbt );
    }

    @Override
    protected boolean isPeripheralBlockedOnSide( ComputerSide localSide )
    {
        return hasPeripheralUpgradeOnSide( localSide );
    }

    // IDirectionalTile

    @Override
    public Direction getDirection()
    {
        return getBlockState().get( BlockTurtle.FACING );
    }

    public void setDirection( Direction dir )
    {
        if( dir.getAxis() == Direction.Axis.Y ) dir = Direction.NORTH;
        world.setBlockState( pos, getBlockState().with( BlockTurtle.FACING, dir ) );
        updateOutput();
        updateInput();
        onTileEntityChange();
    }

    // ITurtleTile

    @Override
    public ITurtleUpgrade getUpgrade( TurtleSide side )
    {
        return m_brain.getUpgrade( side );
    }

    @Override
    public int getColour()
    {
        return m_brain.getColour();
    }

    @Override
    public ResourceLocation getOverlay()
    {
        return m_brain.getOverlay();
    }

    @Override
    public ITurtleAccess getAccess()
    {
        return m_brain;
    }

    @Override
    public Vector3d getRenderOffset( float f )
    {
        return m_brain.getRenderOffset( f );
    }

    @Override
    public float getRenderYaw( float f )
    {
        return m_brain.getVisualYaw( f );
    }

    @Override
    public float getToolRenderAngle( TurtleSide side, float f )
    {
        return m_brain.getToolRenderAngle( side, f );
    }

    void setOwningPlayer( GameProfile player )
    {
        m_brain.setOwningPlayer( player );
        markDirty();
    }

    // IInventory

    @Override
    public int getSizeInventory()
    {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty()
    {
        for( ItemStack stack : m_inventory )
        {
            if( !stack.isEmpty() ) return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot( int slot )
    {
        return slot >= 0 && slot < INVENTORY_SIZE ? m_inventory.get( slot ) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot( int slot )
    {
        ItemStack result = getStackInSlot( slot );
        setInventorySlotContents( slot, ItemStack.EMPTY );
        return result;
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize( int slot, int count )
    {
        if( count == 0 ) return ItemStack.EMPTY;

        ItemStack stack = getStackInSlot( slot );
        if( stack.isEmpty() ) return ItemStack.EMPTY;

        if( stack.getCount() <= count )
        {
            setInventorySlotContents( slot, ItemStack.EMPTY );
            return stack;
        }

        ItemStack part = stack.split( count );
        onInventoryDefinitelyChanged();
        return part;
    }

    @Override
    public void setInventorySlotContents( int i, @Nonnull ItemStack stack )
    {
        if( i >= 0 && i < INVENTORY_SIZE && !InventoryUtil.areItemsEqual( stack, m_inventory.get( i ) ) )
        {
            m_inventory.set( i, stack );
            onInventoryDefinitelyChanged();
        }
    }

    @Override
    public void clear()
    {
        boolean changed = false;
        for( int i = 0; i < INVENTORY_SIZE; i++ )
        {
            if( !m_inventory.get( i ).isEmpty() )
            {
                m_inventory.set( i, ItemStack.EMPTY );
                changed = true;
            }
        }

        if( changed ) onInventoryDefinitelyChanged();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if( !m_inventoryChanged )
        {
            for( int n = 0; n < getSizeInventory(); n++ )
            {
                if( !ItemStack.areItemStacksEqual( getStackInSlot( n ), m_previousInventory.get( n ) ) )
                {
                    m_inventoryChanged = true;
                    break;
                }
            }
        }
    }

    @Override
    public boolean isUsableByPlayer( @Nonnull PlayerEntity player )
    {
        return isUsable( player, false );
    }

    private void onInventoryDefinitelyChanged()
    {
        super.markDirty();
        m_inventoryChanged = true;
    }

    public void onTileEntityChange()
    {
        super.markDirty();
    }

    // Networking stuff

    @Override
    protected void writeDescription( @Nonnull CompoundNBT nbt )
    {
        super.writeDescription( nbt );
        m_brain.writeDescription( nbt );
    }

    @Override
    protected void readDescription( @Nonnull CompoundNBT nbt )
    {
        super.readDescription( nbt );
        m_brain.readDescription( nbt );
    }

    // Privates

    private boolean hasPeripheralUpgradeOnSide( ComputerSide side )
    {
        ITurtleUpgrade upgrade;
        switch( side )
        {
            case RIGHT:
                upgrade = getUpgrade( TurtleSide.RIGHT );
                break;
            case LEFT:
                upgrade = getUpgrade( TurtleSide.LEFT );
                break;
            default:
                return false;
        }
        return upgrade != null && upgrade.getType().isPeripheral();
    }

    public void transferStateFrom( TileTurtle copy )
    {
        super.transferStateFrom( copy );
        Collections.copy( m_inventory, copy.m_inventory );
        Collections.copy( m_previousInventory, copy.m_previousInventory );
        m_inventoryChanged = copy.m_inventoryChanged;
        m_brain = copy.m_brain;
        m_brain.setOwner( this );

        // Mark the other turtle as having moved, and so its peripheral is dead.
        copy.m_moveState = MoveState.MOVED;
        copy.peripheral = CapabilityUtil.invalidate( copy.peripheral );
    }

    public IItemHandlerModifiable getItemHandler()
    {
        return m_itemHandler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability( @Nonnull Capability<T> cap, @Nullable Direction side )
    {
        if( cap == ITEM_HANDLER_CAPABILITY )
        {
            if( itemHandlerCap == null ) itemHandlerCap = LazyOptional.of( () -> new InvWrapper( this ) );
            return itemHandlerCap.cast();
        }

        if( cap == CAPABILITY_PERIPHERAL )
        {
            if( hasMoved() ) return LazyOptional.empty();
            if( peripheral == null )
            {
                peripheral = LazyOptional.of( () -> new ComputerPeripheral( "turtle", createProxy() ) );
            }
            return peripheral.cast();
        }

        return super.getCapability( cap, side );
    }

    @Nullable
    @Override
    public Container createMenu( int id, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player )
    {
        return new ContainerTurtle( id, inventory, m_brain );
    }
}
