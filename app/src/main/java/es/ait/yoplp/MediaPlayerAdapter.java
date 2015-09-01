package es.ait.yoplp;

import android.media.MediaPlayer;

import java.io.File;

/**
 * Class that manages the playback of the playlist.
 */
public class MediaPlayerAdapter implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener
{
    private static MediaPlayerAdapter instance;

    private MediaPlayer actualPlayer;
    private MediaPlayer nextPlayer;
    private boolean actualPlayerPaused;

    public MediaPlayerAdapter()
    {
        actualPlayerPaused = false;
    }

    public static MediaPlayerAdapter getInstance()
    {
        if ( instance == null )
        {
            instance = new MediaPlayerAdapter();
        }
        return instance;
    }

    public void play() throws Exception
    {
        if ( actualPlayer == null )
        {
            PlayListManager<File> plm = PlayListManager.getInstance();
            if (!plm.isEmpty())
            {
                File file = plm.next();
                if (file != null)
                {
                    actualPlayer = new MediaPlayer();
                    actualPlayer.setDataSource(file.getAbsolutePath());
                    actualPlayer.setOnCompletionListener(this);
                    actualPlayer.setOnPreparedListener( this );
                    actualPlayer.prepareAsync();
                    File nextFile = plm.next();
                    if ( nextFile != null )
                    {
                        nextPlayer = new MediaPlayer();
                        nextPlayer.setOnPreparedListener(this);
                        nextPlayer.setOnCompletionListener(this);
                        nextPlayer.setDataSource(nextFile.getAbsolutePath());
                        nextPlayer.prepareAsync();
                    }
                }
            }
        }
        else if ( actualPlayerPaused || !actualPlayer.isPlaying())
        {
            actualPlayer.start();
        }
    }

    public void pause()
    {
        if ( actualPlayerPaused )
        {
            actualPlayer.start();
            actualPlayerPaused = false;
        }
        else
        {
            actualPlayer.pause();
            actualPlayerPaused = true;
        }
    }


    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player)
    {
        if ( player == actualPlayer )
        {
            player.start();
        }
        else
        {
            actualPlayer.setNextMediaPlayer( nextPlayer );
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        mp.release();
        actualPlayer = nextPlayer;
        if ( actualPlayer != null && !actualPlayer.isPlaying())
        {
            actualPlayer.prepareAsync();
        }

        PlayListManager<File> plm = PlayListManager.getInstance();
        File nextFile = plm.next();
        if ( nextFile != null )
        {
            try
            {
                nextPlayer = new MediaPlayer();
                nextPlayer.setOnPreparedListener(this);
                nextPlayer.setOnCompletionListener(this);
                nextPlayer.setDataSource(nextFile.getAbsolutePath());
                nextPlayer.prepareAsync();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
    }

    public void stop()
    {
        if ( actualPlayer != null && actualPlayer.isPlaying())
        {
            actualPlayer.stop();
            actualPlayer.release();
            actualPlayer = null;
        }
        if ( nextPlayer != null )
        {
            if ( nextPlayer.isPlaying())
            {
                nextPlayer.stop();
            }
            nextPlayer.release();
            nextPlayer = null;
        }
    }
}
