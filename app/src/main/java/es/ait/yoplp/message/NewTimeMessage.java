package es.ait.yoplp.message;

import es.ait.yoplp.Utils;

/**
 * Mensaje que se envía a traves del bus cuando un servicio quiere informar a la actividad principal
 * de que ha cambiado el punto de reproducción.
 */
public class NewTimeMessage
{

    private final long newTime;

    public NewTimeMessage( long newTime )
    {
        this.newTime = newTime;
    }

    public long getNewTime()
    {
        return newTime;
    }

    public long getTimeLeftAsLong( long duration )
    {
        return duration - newTime;
    }

    public String getTimeLeftAsString( long duration )
    {
        return Utils.milisToText( duration - newTime );
    }
}
