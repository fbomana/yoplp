package es.ait.yoplp.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class that will manage out play list. It extends from array list and adds the necesary methodas to
 * navigate one entry at a time and to alter the order of the list.
 */
public class PlayList<E extends Comparable<E>> extends ArrayList<E>
{
    private int pointer;
    private List<PlayListPositionChangeListener> positionListeners;

    @Override
    public void clear()
    {
        super.clear();
        pointer = 0;
    }

    /**
     * Intercambia dos elementos de la lista.
     * @param i
     * @param j
     */
    private void swap(int i, int j)
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
        updatePointer();
    }


    /**
     * Move the pointer to the first Entry. Returns false if list is empty
     * @return
     */
    public synchronized boolean first()
    {
        if ( !isEmpty())
        {
            pointer = 0;
            notifyPiositionChange();
        }
        return size() > 0;
    }

    /**
     * Move the pointer to the last entry and return false if list is empty
     * @return
     */
    public synchronized boolean last()
    {
        if ( !isEmpty() )
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
    public synchronized boolean next()
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
    public synchronized boolean previous()
    {
        if ( !isEmpty() && pointer > 0 )
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
        if ( !isEmpty() )
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

    /**
     * This method it's for set the initial value of pointer only. For change the pointer after initialization
     * use navigateTod
     *
     * @param pointer
     * @return
     */
    public void setPointer( int pointer )
    {
        this.pointer = pointer;
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
            positionListeners = new ArrayList<>();
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

    public void sort()
    {
        if ( size() > 1 )
        {
            Collections.sort(this);
        }
        updatePointer();
    }

    /**
     * Removes the selected attribute from all Tracks
     */
    public void deSelectAll()
    {
        for ( int i = 0; i < size(); i ++ )
        {
            (( Track )get(i)).setSelected( false );
        }
    }

    /**
     * Move all seleceted tracks a position up in the list and updates the pointer so it points
     * to the right track.
     */
    public void moveSelectedUp()
    {
        for ( int i = 1; i < size(); i ++ )
        {
            if ( ((Track)get(i)).isSelected() && !((Track)get(i -1)).isSelected())
            {
                if ( pointer == i )
                {
                    pointer --;
                }
                else if ( pointer == i - 1)
                {
                    pointer ++;
                }
                swap( i - 1, i );
            }
        }
    }

    /**
     * Move all selected tracks a position down in the list and updates the pointer so it still points
     * to the right track.
     */
    public void moveSelectedDown()
    {
        for ( int i = size() -2; i >= 0; i -- )
        {
            if ( ((Track)get(i)).isSelected() && !((Track)get(i + 1)).isSelected())
            {
                if ( pointer == i )
                {
                    pointer ++;
                }
                else if (pointer == i + 1 )
                {
                    pointer --;
                }
                swap( i + 1, i );
            }
        }
    }

    /**
     * Remove all selected tracks in the list.
     */
    public void removeSelected()
    {
        int i = 0;
        while ( i < size() )
        {
            if ( ((Track)get(i)).isSelected() )
            {
                if ( i < pointer )
                {
                    pointer --;
                }
                remove( i );
            }
            else
            {
                i++;
            }
        }
        if ( pointer == size() && !isEmpty())
        {
            pointer --;
        }
    }

    /**
     * finds the next unselected position
     * @param position
     * @return
     */
    public int findNextUnselected( int position )
    {
        for ( int i = position + 1; i < size(); i ++ )
        {
            if (!(( Track )get( i )).isSelected())
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Search for the track marked as playing and updates the pointer.
     */
    private void updatePointer()
    {
        for ( int i =  0; i < size(); i++ )
        {
            if ( ((Track)get(i)).isPlaying())
            {
                pointer = i;
                break;
            }
        }
    }
}
