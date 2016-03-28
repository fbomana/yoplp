package es.ait.yoplp.playlist;

/**
 * Interfaz que deben implementar las clases que quieran enterarse cuando hay cambios en la lista de
 * reproducción.
 */
public interface PlayListPositionChangeListener
{
    void playListPositionChanged( int newPosition );
}
