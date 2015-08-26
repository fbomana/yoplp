package es.ait.yoplp;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class that will manage out play list. It extends from array list and adds the necesary methodas to
 * navigate one entry at a time and to alter the order of the list.
 */
public class PlayList<E> extends ArrayList<E>
{
    private int pointer;

    /**
     * Intercambia dos elementos de la lista.
     * @param i
     * @param j
     */
    public void swap( int i, int j )
    {
        E item1 = get( i );
        E item2 = get( j );
        remove( i );
        add( i, item2 );
        remove( j );
        add( j, item1 );
    }

    /**
     * Aleatoriza la posición de todos los elementos de la lista.
     */
    public void randomize()
    {
        Random rnd = new Random( System.currentTimeMillis());
        for( int i = 0; i < size(); i++ )
        {
            swap( i, rnd.nextInt( size()));
        }
    }


    /**
     * return the first object on the PlayList or null if it's empty. Move the pointer to the first
     * entry
     * @return
     */
    public E first()
    {
        pointer = 0;
        if ( size() > 0 )
        {
            return get( pointer );
        }
        return null;
    }

    /**
     * Return the last object of the playList or null if empty. Move the pointer to the last entry
     * @return
     */
    public E last()
    {
        if ( size() > 0 )
        {
            pointer = size() - 1;
            return get( pointer );
        }
        pointer = 0;
        return null;
    }

    /**
     * move the pointer to the next entry and returns it. If it's already the last entry returns null.
     * @return
     */
    public E next()
    {
        if ( pointer < size() - 1 )
        {
            pointer ++;
            return get( pointer );
        }
        return null;
    }

    /**
     * Move the pointer to the previous entry and returns it. It it's already the first entry returns null
     * @return
     */
    public E previous()
    {
        if ( pointer > 0 )
        {
            pointer --;
            return get( pointer );
        }
        return null;
    }
}
