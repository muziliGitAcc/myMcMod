/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.peripheral.speaker;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static dan200.computercraft.api.lua.LuaValues.checkFinite;

/**
 * Speakers allow playing notes and other sounds.
 *
 * @cc.module speaker
 */
public abstract class SpeakerPeripheral implements IPeripheral
{
    private long m_clock = 0;
    private long m_lastPlayTime = 0;
    private final AtomicInteger m_notesThisTick = new AtomicInteger();

    public void update()
    {
        m_clock++;
        m_notesThisTick.set( 0 );
    }

    public abstract World getWorld();

    public abstract Vector3d getPosition();

    public boolean madeSound( long ticks )
    {
        return m_clock - m_lastPlayTime <= ticks;
    }

    @Nonnull
    @Override
    public String getType()
    {
        return "speaker";
    }

    /**
     * Plays a sound through the speaker.
     *
     * This plays sounds similar to the {@code /playsound} command in Minecraft.
     * It takes the namespaced path of a sound (e.g. {@code minecraft:block.note_block.harp})
     * with an optional volume and speed multiplier, and plays it through the speaker.
     *
     * @param context The Lua context
     * @param name    The name of the sound to play.
     * @param volumeA The volume to play the sound at, from 0.0 to 3.0. Defaults to 1.0.
     * @param pitchA  The speed to play the sound at, from 0.5 to 2.0. Defaults to 1.0.
     * @return Whether the sound could be played.
     * @throws LuaException If the sound name couldn't be decoded.
     */
    @LuaFunction
    public final boolean playSound( ILuaContext context, String name, Optional<Double> volumeA, Optional<Double> pitchA ) throws LuaException
    {
        float volume = (float) checkFinite( 1, volumeA.orElse( 1.0 ) );
        float pitch = (float) checkFinite( 2, pitchA.orElse( 1.0 ) );

        ResourceLocation identifier;
        try
        {
            identifier = new ResourceLocation( name );
        }
        catch( ResourceLocationException e )
        {
            throw new LuaException( "Malformed sound name '" + name + "' " );
        }

        return playSound( context, identifier, volume, pitch, false );
    }

    /**
     * Plays a note block note through the speaker.
     *
     * This takes the name of a note to play, as well as optionally the volume
     * and pitch to play the note at.
     *
     * The pitch argument uses semitones as the unit. This directly maps to the
     * number of clicks on a note block. For reference, 0, 12, and 24 map to F#,
     * and 6 and 18 map to C.
     *
     * @param context The Lua context
     * @param name    The name of the note to play.
     * @param volumeA The volume to play the note at, from 0.0 to 3.0. Defaults to 1.0.
     * @param pitchA  The pitch to play the note at in semitones, from 0 to 24. Defaults to 12.
     * @return Whether the note could be played.
     * @throws LuaException If the instrument doesn't exist.
     */
    @LuaFunction
    public final synchronized boolean playNote( ILuaContext context, String name, Optional<Double> volumeA, Optional<Double> pitchA ) throws LuaException
    {
        float volume = (float) checkFinite( 1, volumeA.orElse( 1.0 ) );
        float pitch = (float) checkFinite( 2, pitchA.orElse( 1.0 ) );

        NoteBlockInstrument instrument = null;
        for( NoteBlockInstrument testInstrument : NoteBlockInstrument.values() )
        {
            if( testInstrument.getString().equalsIgnoreCase( name ) )
            {
                instrument = testInstrument;
                break;
            }
        }

        // Check if the note exists
        if( instrument == null ) throw new LuaException( "Invalid instrument, \"" + name + "\"!" );

        // If the resource location for note block notes changes, this method call will need to be updated
        boolean success = playSound( context, instrument.getSound().getRegistryName(), volume, (float) Math.pow( 2.0, (pitch - 12.0) / 12.0 ), true );
        if( success ) m_notesThisTick.incrementAndGet();
        return success;
    }

    private synchronized boolean playSound( ILuaContext context, ResourceLocation name, float volume, float pitch, boolean isNote ) throws LuaException
    {
        if( m_clock - m_lastPlayTime < TileSpeaker.MIN_TICKS_BETWEEN_SOUNDS &&
            (!isNote || m_clock - m_lastPlayTime != 0 || m_notesThisTick.get() >= ComputerCraft.maxNotesPerTick) )
        {
            // Rate limiting occurs when we've already played a sound within the last tick, or we've
            // played more notes than allowable within the current tick.
            return false;
        }

        World world = getWorld();
        Vector3d pos = getPosition();

        context.issueMainThreadTask( () -> {
            MinecraftServer server = world.getServer();
            if( server == null ) return null;

            float adjVolume = Math.min( volume, 3.0f );
            server.getPlayerList().sendToAllNearExcept(
                null, pos.x, pos.y, pos.z, adjVolume > 1.0f ? 16 * adjVolume : 16.0, world.func_234923_W_(),
                new SPlaySoundPacket( name, SoundCategory.RECORDS, pos, adjVolume, pitch )
            );
            return null;
        } );

        m_lastPlayTime = m_clock;
        return true;
    }
}
