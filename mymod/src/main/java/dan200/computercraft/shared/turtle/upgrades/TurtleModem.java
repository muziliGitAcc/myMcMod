/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.turtle.upgrades;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import dan200.computercraft.shared.Registry;
import dan200.computercraft.shared.peripheral.modem.ModemState;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemPeripheral;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class TurtleModem extends AbstractTurtleUpgrade
{
    private static class Peripheral extends WirelessModemPeripheral
    {
        private final ITurtleAccess turtle;

        Peripheral( ITurtleAccess turtle, boolean advanced )
        {
            super( new ModemState(), advanced );
            this.turtle = turtle;
        }

        @Nonnull
        @Override
        public World getWorld()
        {
            return turtle.getWorld();
        }

        @Nonnull
        @Override
        public Vector3d getPosition()
        {
            BlockPos turtlePos = turtle.getPosition();
            return new Vector3d(
                turtlePos.getX(),
                turtlePos.getY(),
                turtlePos.getZ()
            );
        }

        @Override
        public boolean equals( IPeripheral other )
        {
            return this == other || (other instanceof Peripheral && ((Peripheral) other).turtle == turtle);
        }
    }

    private final boolean advanced;

    @OnlyIn( Dist.CLIENT )
    private ModelResourceLocation m_leftOffModel;

    @OnlyIn( Dist.CLIENT )
    private ModelResourceLocation m_rightOffModel;

    @OnlyIn( Dist.CLIENT )
    private ModelResourceLocation m_leftOnModel;

    @OnlyIn( Dist.CLIENT )
    private ModelResourceLocation m_rightOnModel;

    public TurtleModem( boolean advanced, ResourceLocation id )
    {
        super(
            id, TurtleUpgradeType.PERIPHERAL,
            advanced
                ? Registry.ModBlocks.WIRELESS_MODEM_ADVANCED
                : Registry.ModBlocks.WIRELESS_MODEM_NORMAL
        );
        this.advanced = advanced;
    }

    @Override
    public IPeripheral createPeripheral( @Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side )
    {
        return new Peripheral( turtle, advanced );
    }

    @Nonnull
    @Override
    public TurtleCommandResult useTool( @Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side, @Nonnull TurtleVerb verb, @Nonnull Direction dir )
    {
        return TurtleCommandResult.failure();
    }

    @OnlyIn( Dist.CLIENT )
    private void loadModelLocations()
    {
        if( m_leftOffModel == null )
        {
            if( advanced )
            {
                m_leftOffModel = new ModelResourceLocation( "computercraft:turtle_modem_advanced_off_left", "inventory" );
                m_rightOffModel = new ModelResourceLocation( "computercraft:turtle_modem_advanced_off_right", "inventory" );
                m_leftOnModel = new ModelResourceLocation( "computercraft:turtle_modem_advanced_on_left", "inventory" );
                m_rightOnModel = new ModelResourceLocation( "computercraft:turtle_modem_advanced_on_right", "inventory" );
            }
            else
            {
                m_leftOffModel = new ModelResourceLocation( "computercraft:turtle_modem_normal_off_left", "inventory" );
                m_rightOffModel = new ModelResourceLocation( "computercraft:turtle_modem_normal_off_right", "inventory" );
                m_leftOnModel = new ModelResourceLocation( "computercraft:turtle_modem_normal_on_left", "inventory" );
                m_rightOnModel = new ModelResourceLocation( "computercraft:turtle_modem_normal_on_right", "inventory" );
            }
        }
    }

    @Nonnull
    @Override
    @OnlyIn( Dist.CLIENT )
    public TransformedModel getModel( ITurtleAccess turtle, @Nonnull TurtleSide side )
    {
        loadModelLocations();

        boolean active = false;
        if( turtle != null )
        {
            CompoundNBT turtleNBT = turtle.getUpgradeNBTData( side );
            active = turtleNBT.contains( "active" ) && turtleNBT.getBoolean( "active" );
        }

        return side == TurtleSide.LEFT
            ? TransformedModel.of( active ? m_leftOnModel : m_leftOffModel )
            : TransformedModel.of( active ? m_rightOnModel : m_rightOffModel );
    }

    @Override
    public void update( @Nonnull ITurtleAccess turtle, @Nonnull TurtleSide side )
    {
        // Advance the modem
        if( !turtle.getWorld().isRemote )
        {
            IPeripheral peripheral = turtle.getPeripheral( side );
            if( peripheral instanceof Peripheral )
            {
                ModemState state = ((Peripheral) peripheral).getModemState();
                if( state.pollChanged() )
                {
                    turtle.getUpgradeNBTData( side ).putBoolean( "active", state.isOpen() );
                    turtle.updateUpgradeNBTData( side );
                }
            }
        }
    }
}
