package es.ait.yoplp;

import java.io.File;

/**
 * Singleton version of the PlayList class that we will use to storage the files of the active playlist.
 *
 */
public class PlayListManager<E> extends PlayList<E>
{
    private static PlayListManager<File> instance;

    private PlayListManager()
    {
    }

    /**
     * returns de only instance of the playlist
     * @return
     */
    public static PlayListManager<File> getInstance()
    {
        if ( instance == null )
        {
            instance = new PlayListManager<File>();
        }
        return instance;
    }

}
