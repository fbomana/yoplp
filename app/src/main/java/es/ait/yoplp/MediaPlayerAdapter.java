package es.ait.yoplp;

import android.media.MediaPlayer;

import java.io.IOException;

import es.ait.yoplp.playlist.PlayListManager;
import es.ait.yoplp.playlist.Track;

/**
 * Class that manages the playback of the playlist.
 */
public class MediaPlayerAdapter implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener
{
    private static MediaPlayerAdapter instance;

    private MediaPlayer actualPlayer;
    private MediaPlayer nextPlayer;
    private boolean actualPlayerPaused;
    private int startPosition = 0;

    private MediaPlayerAdapter()
    {
        actualPlayerPaused = false;
    }

    protected static void initialize( MediaPlayerAdapter mpa )
    {
        instance = mpa;
    }

    public static MediaPlayerAdapter getInstance()
    {
        if ( instance == null )
        {
            instance = new MediaPlayerAdapter();
        }
        return instance;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player)
    {
        if ( player == actualPlayer )
        {
            if ( startPosition > 0 )
            {
                player.setOnSeekCompleteListener( this );
                player.seekTo( startPosition );
                startPosition = 0;
            }
            else
            {
                player.start();
            }
        }
        else
        {
            if ( actualPlayer != null )
            {
                try
                {
                    actualPlayer.setNextMediaPlayer(nextPlayer);
                }
                catch ( IllegalStateException e )
                {
                    // Capture the exception that can happen if someone hit the buttons repeteadly
                }
            }
        }
    }

    /**
     * Called when we start in the middle of a track.
     * @param mp
     */
    @Override
    public void onSeekComplete(MediaPlayer mp)
    {
        mp.start();
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

        PlayListManager<Track> plm = PlayListManager.getInstance();
        if ( actualPlayer != null )
        {
            plm.next();
            try
            {
                prepareNextPlayer();
            }
            catch ( Exception e )
            {

            }
        }
    }

    public void play() throws IOException
    {
        play( 0 );
    }

    public void play( int startPosition ) throws IOException
    {
        if ( actualPlayer == null )
        {
            PlayListManager<Track> plm = PlayListManager.getInstance();
            if (!plm.isEmpty())
            {
                Track track = plm.get();
                if (track != null)
                {
                    actualPlayer = new MediaPlayer();
                    actualPlayer.setDataSource(track.getFile().getAbsolutePath());
                    actualPlayer.setOnCompletionListener(this);
                    actualPlayer.setOnPreparedListener(this);
                    actualPlayer.prepareAsync();
                    this.startPosition = startPosition;
                    prepareNextPlayer();
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

    public void stop()
    {
        stopActualPlayer();
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

    public void next() throws IOException
    {
        PlayListManager<Track> plm = PlayListManager.getInstance();

        stopActualPlayer();

        if ( plm.next() )
        {
            if ( nextPlayer != null )
            {
                actualPlayer = nextPlayer;
                actualPlayer.start();
                prepareNextPlayer();
            }
            else
            {
                play();
            }
        }
    }

    public void previous() throws IOException
    {
        PlayListManager<Track> plm = PlayListManager.getInstance();
        stopActualPlayer();
        if ( plm.previous())
        {
            play();
        }
        else if ( nextPlayer != null )
        {
            nextPlayer.release();
            nextPlayer = null;
        }
    }

    /**
     * Method that prepare the next player once the actual player it's prepared.
     * @throws IOException
     */
    private void prepareNextPlayer() throws IOException
    {
        PlayListManager<Track> plm = PlayListManager.getInstance();
        Track nextTrack = plm.getNext();
        if ( nextTrack != null )
        {
            nextPlayer = new MediaPlayer();
            nextPlayer.setOnPreparedListener(this);
            nextPlayer.setOnCompletionListener(this);
            nextPlayer.setDataSource(nextTrack.getFile().getAbsolutePath());
            nextPlayer.prepareAsync();
        }
        else
        {
            nextPlayer = null;
            actualPlayer.setNextMediaPlayer( null );
        }
    }

    /**
     * Stop the actual player and releases it. It restores whatever state it's necessary in the adapter.
     */
    private void stopActualPlayer()
    {
        if ( actualPlayer != null )
        {
            if ( actualPlayer.isPlaying() || actualPlayerPaused )
            {
                actualPlayer.stop();
                actualPlayerPaused = false;
            }
            actualPlayer.release();
            actualPlayer = null;
        }
    }

    public MediaPlayer getActualPlayer()
    {
        return actualPlayer;
    }

    public MediaPlayer getNextPlayer()
    {
        return nextPlayer;
    }

    public void refreshNextPlayer() throws IOException
    {
        if ( actualPlayer != null )
        {
            prepareNextPlayer();
        }
    }
}
