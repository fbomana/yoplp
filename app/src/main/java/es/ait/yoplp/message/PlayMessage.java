package es.ait.yoplp.message;

/**
 * Mensaje que se envía cuando se quiere iniciar la reproducción de una pista en un punto concreto.
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
