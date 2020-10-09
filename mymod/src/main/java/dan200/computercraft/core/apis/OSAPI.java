/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.core.apis;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.StringUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import static dan200.computercraft.api.lua.LuaValues.checkFinite;

/**
 * The {@link OSAPI} API allows interacting with the current computer.
 *
 * @cc.module os
 */
public class OSAPI implements ILuaAPI
{
    private final IAPIEnvironment apiEnvironment;

    private final Int2ObjectMap<Alarm> m_alarms = new Int2ObjectOpenHashMap<>();
    private int m_clock;
    private double m_time;
    private int m_day;

    private int m_nextAlarmToken = 0;

    private static class Alarm implements Comparable<Alarm>
    {
        final double m_time;
        final int m_day;

        Alarm( double time, int day )
        {
            m_time = time;
            m_day = day;
        }

        @Override
        public int compareTo( @Nonnull Alarm o )
        {
            double t = m_day * 24.0 + m_time;
            double ot = m_day * 24.0 + m_time;
            return Double.compare( t, ot );
        }
    }

    public OSAPI( IAPIEnvironment environment )
    {
        apiEnvironment = environment;
    }

    @Override
    public String[] getNames()
    {
        return new String[] { "os" };
    }

    @Override
    public void startup()
    {
        m_time = apiEnvironment.getComputerEnvironment().getTimeOfDay();
        m_day = apiEnvironment.getComputerEnvironment().getDay();
        m_clock = 0;

        synchronized( m_alarms )
        {
            m_alarms.clear();
        }
    }

    @Override
    public void update()
    {
        m_clock++;

        // Wait for all of our alarms
        synchronized( m_alarms )
        {
            double previousTime = m_time;
            int previousDay = m_day;
            double time = apiEnvironment.getComputerEnvironment().getTimeOfDay();
            int day = apiEnvironment.getComputerEnvironment().getDay();

            if( time > previousTime || day > previousDay )
            {
                double now = m_day * 24.0 + m_time;
                Iterator<Int2ObjectMap.Entry<Alarm>> it = m_alarms.int2ObjectEntrySet().iterator();
                while( it.hasNext() )
                {
                    Int2ObjectMap.Entry<Alarm> entry = it.next();
                    Alarm alarm = entry.getValue();
                    double t = alarm.m_day * 24.0 + alarm.m_time;
                    if( now >= t )
                    {
                        apiEnvironment.queueEvent( "alarm", entry.getIntKey() );
                        it.remove();
                    }
                }
            }

            m_time = time;
            m_day = day;
        }
    }

    @Override
    public void shutdown()
    {
        synchronized( m_alarms )
        {
            m_alarms.clear();
        }
    }

    private static float getTimeForCalendar( Calendar c )
    {
        float time = c.get( Calendar.HOUR_OF_DAY );
        time += c.get( Calendar.MINUTE ) / 60.0f;
        time += c.get( Calendar.SECOND ) / (60.0f * 60.0f);
        return time;
    }

    private static int getDayForCalendar( Calendar c )
    {
        GregorianCalendar g = c instanceof GregorianCalendar ? (GregorianCalendar) c : new GregorianCalendar();
        int year = c.get( Calendar.YEAR );
        int day = 0;
        for( int y = 1970; y < year; y++ )
        {
            day += g.isLeapYear( y ) ? 366 : 365;
        }
        day += c.get( Calendar.DAY_OF_YEAR );
        return day;
    }

    private static long getEpochForCalendar( Calendar c )
    {
        return c.getTime().getTime();
    }

    /**
     * Adds an event to the event queue. This event can later be pulled with
     * os.pullEvent.
     *
     * @param name The name of the event to queue.
     * @param args The parameters of the event.
     * @cc.tparam string name The name of the event to queue.
     * @cc.param ... The parameters of the event.
     * @cc.see os.pullEvent To pull the event queued
     */
    @LuaFunction
    public final void queueEvent( String name, IArguments args )
    {
        apiEnvironment.queueEvent( name, args.drop( 1 ).getAll() );
    }

    /**
     * Starts a timer that will run for the specified number of seconds. Once
     * the timer fires, a timer event will be added to the queue with the ID
     * returned from this function as the first parameter.
     *
     * @param timer The number of seconds until the timer fires.
     * @return The ID of the new timer.
     * @throws LuaException If the time is below zero.
     */
    @LuaFunction
    public final int startTimer( double timer ) throws LuaException
    {
        return apiEnvironment.startTimer( Math.round( checkFinite( 0, timer ) / 0.05 ) );
    }

    /**
     * Cancels a timer previously started with startTimer. This will stop the
     * timer from firing.
     *
     * @param token The ID of the timer to cancel.
     * @see #startTimer To start a timer.
     */
    @LuaFunction
    public final void cancelTimer( int token )
    {
        apiEnvironment.cancelTimer( token );
    }

    /**
     * Sets an alarm that will fire at the specified world time. When it fires,
     * an alarm event will be added to the event queue.
     *
     * @param time The time at which to fire the alarm, in the range [0.0, 24.0).
     * @return The ID of the alarm that was set.
     * @throws LuaException If the time is out of range.
     */
    @LuaFunction
    public final int setAlarm( double time ) throws LuaException
    {
        checkFinite( 0, time );
        if( time < 0.0 || time >= 24.0 ) throw new LuaException( "Number out of range" );
        synchronized( m_alarms )
        {
            int day = time > m_time ? m_day : m_day + 1;
            m_alarms.put( m_nextAlarmToken, new Alarm( time, day ) );
            return m_nextAlarmToken++;
        }
    }

    /**
     * Cancels an alarm previously started with setAlarm. This will stop the
     * alarm from firing.
     *
     * @param token The ID of the alarm to cancel.
     * @see #setAlarm To set an alarm.
     */
    @LuaFunction
    public final void cancelAlarm( int token )
    {
        synchronized( m_alarms )
        {
            m_alarms.remove( token );
        }
    }

    /**
     * Shuts down the computer immediately.
     */
    @LuaFunction( "shutdown" )
    public final void doShutdown()
    {
        apiEnvironment.shutdown();
    }

    /**
     * Reboots the computer immediately.
     */
    @LuaFunction( "reboot" )
    public final void doReboot()
    {
        apiEnvironment.reboot();
    }

    /**
     * Returns the ID of the computer.
     *
     * @return The ID of the computer.
     */
    @LuaFunction( { "getComputerID", "computerID" } )
    public final int getComputerID()
    {
        return apiEnvironment.getComputerID();
    }

    /**
     * Returns the label of the computer, or {@code nil} if none is set.
     *
     * @return The label of the computer.
     * @cc.treturn string The label of the computer.
     */
    @LuaFunction( { "getComputerLabel", "computerLabel" } )
    public final Object[] getComputerLabel()
    {
        String label = apiEnvironment.getLabel();
        return label == null ? null : new Object[] { label };
    }

    /**
     * Set the label of this computer.
     *
     * @param label The new label. May be {@code nil} in order to clear it.
     */
    @LuaFunction
    public final void setComputerLabel( Optional<String> label )
    {
        apiEnvironment.setLabel( StringUtil.normaliseLabel( label.orElse( null ) ) );
    }

    /**
     * Returns the number of seconds that the computer has been running.
     *
     * @return The computer's uptime.
     */
    @LuaFunction
    public final double clock()
    {
        return m_clock * 0.05;
    }

    /**
     * Returns the current time depending on the string passed in. This will
     * always be in the range [0.0, 24.0).
     *
     * * If called with {@code ingame}, the current world time will be returned.
     * This is the default if nothing is passed.
     * * If called with {@code utc}, returns the hour of the day in UTC time.
     * * If called with {@code local}, returns the hour of the day in the
     * timezone the server is located in.
     *
     * This function can also be called with a table returned from {@link #date},
     * which will convert the date fields into a UNIX timestamp (number of
     * seconds since 1 January 1970).
     *
     * @param args The locale of the time, or a table filled by {@code os.date("*t")} to decode. Defaults to {@code ingame} locale if not specified.
     * @return The hour of the selected locale, or a UNIX timestamp from the table, depending on the argument passed in.
     * @throws LuaException If an invalid locale is passed.
     * @cc.tparam [opt] string|table locale The locale of the time, or a table filled by {@code os.date("*t")} to decode. Defaults to {@code ingame} locale if not specified.
     * @see #date To get a date table that can be converted with this function.
     */
    @LuaFunction
    public final Object time( IArguments args ) throws LuaException
    {
        Object value = args.get( 0 );
        if( value instanceof Map ) return LuaDateTime.fromTable( (Map<?, ?>) value );

        String param = args.optString( 0, "ingame" );
        switch( param.toLowerCase( Locale.ROOT ) )
        {
            case "utc": // Get Hour of day (UTC)
                return getTimeForCalendar( Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) ) );
            case "local": // Get Hour of day (local time)
                return getTimeForCalendar( Calendar.getInstance() );
            case "ingame": // Get in-game hour
                return m_time;
            default:
                throw new LuaException( "Unsupported operation" );
        }
    }

    /**
     * Returns the day depending on the locale specified.
     *
     * * If called with {@code ingame}, returns the number of days since the
     * world was created. This is the default.
     * * If called with {@code utc}, returns the number of days since 1 January
     * 1970 in the UTC timezone.
     * * If called with {@code local}, returns the number of days since 1
     * January 1970 in the server's local timezone.
     *
     * @param args The locale to get the day for. Defaults to {@code ingame} if not set.
     * @return The day depending on the selected locale.
     * @throws LuaException If an invalid locale is passed.
     */
    @LuaFunction
    public final int day( Optional<String> args ) throws LuaException
    {
        switch( args.orElse( "ingame" ).toLowerCase( Locale.ROOT ) )
        {
            case "utc":     // Get numbers of days since 1970-01-01 (utc)
                return getDayForCalendar( Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) ) );
            case "local": // Get numbers of days since 1970-01-01 (local time)
                return getDayForCalendar( Calendar.getInstance() );
            case "ingame":// Get game day
                return m_day;
            default:
                throw new LuaException( "Unsupported operation" );
        }
    }

    /**
     * Returns the number of seconds since an epoch depending on the locale.
     *
     * * If called with {@code ingame}, returns the number of seconds since the
     * world was created. This is the default.
     * * If called with {@code utc}, returns the number of seconds since 1
     * January 1970 in the UTC timezone.
     * * If called with {@code local}, returns the number of seconds since 1
     * January 1970 in the server's local timezone.
     *
     * @param args The locale to get the seconds for. Defaults to {@code ingame} if not set.
     * @return The seconds since the epoch depending on the selected locale.
     * @throws LuaException If an invalid locale is passed.
     */
    @LuaFunction
    public final long epoch( Optional<String> args ) throws LuaException
    {
        switch( args.orElse( "ingame" ).toLowerCase( Locale.ROOT ) )
        {
            case "utc":
            {
                // Get utc epoch
                Calendar c = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) );
                return getEpochForCalendar( c );
            }
            case "local":
            {
                // Get local epoch
                Calendar c = Calendar.getInstance();
                return getEpochForCalendar( c );
            }
            case "ingame":
                // Get in-game epoch
                synchronized( m_alarms )
                {
                    return m_day * 86400000 + (int) (m_time * 3600000.0f);
                }
            default:
                throw new LuaException( "Unsupported operation" );
        }
    }

    /**
     * Returns a date string (or table) using a specified format string and
     * optional time to format.
     *
     * The format string takes the same formats as C's {@code strftime} function
     * (http://www.cplusplus.com/reference/ctime/strftime/). In extension, it
     * can be prefixed with an exclamation mark ({@code !}) to use UTC time
     * instead of the server's local timezone.
     *
     * If the format is exactly {@code *t} (optionally prefixed with {@code !}), a
     * table will be returned instead. This table has fields for the year, month,
     * day, hour, minute, second, day of the week, day of the year, and whether
     * Daylight Savings Time is in effect. This table can be converted to a UNIX
     * timestamp (days since 1 January 1970) with {@link #date}.
     *
     * @param formatA The format of the string to return. This defaults to {@code %c}, which expands to a string similar to "Sat Dec 24 16:58:00 2011".
     * @param timeA   The time to convert to a string. This defaults to the current time.
     * @return The resulting format string.
     * @throws LuaException If an invalid format is passed.
     */
    @LuaFunction
    public final Object date( Optional<String> formatA, Optional<Long> timeA ) throws LuaException
    {
        String format = formatA.orElse( "%c" );
        long time = timeA.orElseGet( () -> Instant.now().getEpochSecond() );

        Instant instant = Instant.ofEpochSecond( time );
        ZonedDateTime date;
        ZoneOffset offset;
        if( format.startsWith( "!" ) )
        {
            offset = ZoneOffset.UTC;
            date = ZonedDateTime.ofInstant( instant, offset );
            format = format.substring( 1 );
        }
        else
        {
            ZoneId id = ZoneId.systemDefault();
            offset = id.getRules().getOffset( instant );
            date = ZonedDateTime.ofInstant( instant, id );
        }

        if( format.equals( "*t" ) ) return LuaDateTime.toTable( date, offset, instant );

        DateTimeFormatterBuilder formatter = new DateTimeFormatterBuilder();
        LuaDateTime.format( formatter, format, offset );
        return formatter.toFormatter( Locale.ROOT ).format( date );
    }

}
