package es.ait.yoplp;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

import es.ait.yoplp.exoplayer.YOPLPAudioPlayer;
import es.ait.yoplp.playlist.PlayListManager;
import es.ait.yoplp.playlist.Track;

/**
 * Class that manages the playback of the playlist.
 */
public class MediaPlayerAdapter
{
    private static MediaPlayerAdapter instance;

    private MediaPlayerAdapter( Context context )
    {
        YOPLPAudioPlayer.getInstance( context );
    }


    public static MediaPlayerAdapter getInstance( Context context )
    {
        if ( instance == null && context != null )
        {
            instance = new MediaPlayerAdapter( context );
        }
        return instance;
    }

    public static MediaPlayerAdapter getInstance()
    {
        return instance;
    }

    public void play()
    {
        play( 0 );
    }

    public void play( int startPosition )
    {
        if ( !YOPLPAudioPlayer.getInstance().isPlaying() )
        {
            PlayListManager<Track> plm = PlayListManager.getInstance();
            if (!plm.isEmpty())
            {
                Track track = plm.get();
                if (track != null)
                {
                    YOPLPAudioPlayer.getInstance().start( track, startPosition );
                }
            }
        }
        else
        {
            YOPLPAudioPlayer.getInstance().togglePausePlay();
        }
    }

    public void pause()
    {
        if ( !YOPLPAudioPlayer.getInstance().isPlaying() )
        {
            YOPLPAudioPlayer.getInstance().togglePausePlay();
        }
    }

    public void stop()
    {
        YOPLPAudioPlayer.getInstance().stop();
    }

    public void next()
    {
        PlayListManager<Track> plm = PlayListManager.getInstance();

        if ( plm.next() )
        {
            stop();
            play();
        }
    }

    public void previous()
    {
        PlayListManager<Track> plm = PlayListManager.getInstance();

        if ( plm.previous())
        {
            stop();
            play();
        }
    }

    public long getCurrentPosition()
    {
        return YOPLPAudioPlayer.getInstance().getCurrentPosition();
    }
}
