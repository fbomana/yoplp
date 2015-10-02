package es.ait.yoplp.message;

import es.ait.yoplp.Utils;

/**
 * Created by aitkiar on 2/10/15.
 */
public class NewTimeMessage
{

    private int newTime;

    public NewTimeMessage( int newTime )
    {
        this.newTime = newTime;
    }

    public int getNewTime()
    {
        return newTime;
    }

    public String getNewTimeAsString()
    {
        return Utils.milisToText( newTime );
    }
}
