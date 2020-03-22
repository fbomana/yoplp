package es.ait.yoplp.message;

/**
 * Mensaje que se envía cuando se mueve el punto de reproducción dentro de la pista que se está reproducciendo actualmente.
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
