/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.network.container;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.network.PacketBuffer;

public class ComputerContainerData implements ContainerData
{
    private final int id;
    private final ComputerFamily family;

    public ComputerContainerData( ServerComputer computer )
    {
        this.id = computer.getInstanceID();
        this.family = computer.getFamily();
    }

    public ComputerContainerData( PacketBuffer buf )
    {
        this.id = buf.readInt();
        this.family = buf.readEnumValue( ComputerFamily.class );
    }

    @Override
    public void toBytes( PacketBuffer buf )
    {
        buf.writeInt( id );
        buf.writeEnumValue( family );
    }

    public int getInstanceId()
    {
        return id;
    }

    public ComputerFamily getFamily()
    {
        return family;
    }
}
