package es.ait.yoplp.message;

/**
 * Created by aitkiar on 20/11/15.
 */
public class PlayMessage
{
    private final long position;

    public PlayMessage()
    {
        this.position = 0;
    }

    public PlayMessage( long position )
    {
        this.position = position;
    }

    public long getPosition()
    {
        return position;
    }
}
