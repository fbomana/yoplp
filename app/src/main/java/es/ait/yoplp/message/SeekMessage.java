package es.ait.yoplp.message;

/**
 * Created by aitkiar on 25/11/15.
 */
public class SeekMessage
{
    private final long position;

    public SeekMessage( long position )
    {
        this.position = position;
    }

    public long getPosition()
    {
        return this.position;
    }
}
