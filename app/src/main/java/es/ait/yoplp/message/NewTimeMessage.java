package es.ait.yoplp.message;

import es.ait.yoplp.Utils;

/**
 * Created by aitkiar on 2/10/15.
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

    public String getNewTimeAsString( long duration )
    {
        return Utils.milisToText( duration - newTime );
    }
}
