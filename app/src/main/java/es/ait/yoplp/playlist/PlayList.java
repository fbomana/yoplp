package es.ait.yoplp.playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that will manage out play list. It extends from array list and adds the necesary methodas to
 * navigate one entry at a time and to alter the order of the list.
 */
public class PlayList<E> extends ArrayList<E>
{
    private int pointer;
    private List<PlayListPositionChangeListener> positionListeners;

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
     * Move the pointer to the first Entry. Returns false if list is empty
     * @return
     */
    public boolean first()
    {
        pointer = 0;
        notifyPiositionChange();
        return size() > 0;
    }

    /**
     * Move the pointer to the last entry and return false if list is empty
     * @return
     */
    public boolean last()
    {
        if ( size() > 0 )
        {
            pointer = size() - 1;
            notifyPiositionChange();
            return true;
        }
        pointer = 0;
        return false;
    }

    /**
     * Move the pointer one entry ahead. If there is no entry to move to, returns false.
     * @return
     */
    public boolean next()
    {
        if ( !isEmpty() && (pointer + 1 ) < size())
        {
            pointer ++;
            notifyPiositionChange();
            return true;
        }
        return false;

    }

    /**
     * Move the pointer to the previous entry.If there is no entry to move to, returns false.
     * @return
     */
    public boolean previous()
    {
        if ( !isEmpty() && pointer >= 0 )
        {
            pointer --;
            notifyPiositionChange();
            return true;
        }
        return false;
    }

    /**
     * Gets the element pointed by the pointer. Null if no actual element is pointed.
     * @return
     */
    public E get()
    {
        if ( pointer >= 0 && pointer < size())
        {
            return get( pointer );
        }
        return null;
    }

    /**
     * Gets the element next to the pointed element. Returns null if there is no such element.
     * @return
     */
    public E getNext()
    {
        if ( pointer >= 0 && (pointer + 1) < size())
        {
            return get( pointer + 1);
        }
        return null;
    }

    /**
     * Return the number of the selected item. It only has sense if there is at least one item in the
     * list
     * @return
     */
    public int getPointer()
    {
        return pointer;
    }

    public boolean navigateTo( int pointer )
    {
        if ( pointer >= 0 && pointer < size())
        {
            this.pointer = pointer;
            notifyPiositionChange();
            return true;
        }
        return false;
    }

    public void addPlayListPositionChangeListener( PlayListPositionChangeListener listener )
    {
        if ( positionListeners == null )
        {
            positionListeners = new ArrayList<PlayListPositionChangeListener>();
        }
        positionListeners.add( listener );
    }

    public void removePlayListPositionChangeListener( PlayListPositionChangeListener listener )
    {
        if ( positionListeners != null && !isEmpty())
        {
            positionListeners.remove( listener );
        }
    }

    private void notifyPiositionChange()
    {
        if ( positionListeners != null )
        {
            for (int i = 0; i < positionListeners.size(); i++)
            {
                positionListeners.get( i ).playListPositionChanged( pointer );
            }
        }
    }
}
