/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.core.asm;

import javax.annotation.Nonnull;

public final class NamedMethod<T>
{
    private final String name;
    private final T method;
    private final boolean nonYielding;

    NamedMethod( String name, T method, boolean nonYielding )
    {
        this.name = name;
        this.method = method;
        this.nonYielding = nonYielding;
    }

    @Nonnull
    public String getName()
    {
        return name;
    }

    @Nonnull
    public T getMethod()
    {
        return method;
    }

    public boolean nonYielding()
    {
        return nonYielding;
    }
}
