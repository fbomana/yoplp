package es.ait.yoplp.playlist;

import android.content.Context;
import android.util.Log;

import com.squareup.otto.Subscribe;

import es.ait.yoplp.exoplayer.YOPLPAudioPlayer;
import es.ait.yoplp.message.BusManager;
import es.ait.yoplp.message.NewTimeMessage;
import es.ait.yoplp.message.NextMessage;
import es.ait.yoplp.message.PauseMessage;
import es.ait.yoplp.message.PlayMessage;
import es.ait.yoplp.message.PreviousMessage;
import es.ait.yoplp.message.SeekMessage;
import es.ait.yoplp.message.StopMessage;
import es.ait.yoplp.message.TrackEndedMessage;

/**
 * Created by aitkiar on 20/11/15.
 */
public class YOPLPPlayingThread implements Runnable
{
    private static YOPLPPlayingThread instance;

    private final YOPLPAudioPlayer player;
    private boolean stop = false;

    public static YOPLPPlayingThread getInstance( Context context )
    {
        if ( instance == null )
        {
            instance = new YOPLPPlayingThread( YOPLPAudioPlayer.getInstance( context ));
        }
        instance.stop = false;
        return instance;
    }

    private YOPLPPlayingThread ( YOPLPAudioPlayer player )
    {
        Log.e("[YOPLP]", "En el constructor de YOPLPPlayingThread");
        this.player = player;
        BusManager.getBus().register(this);
    }

    public void stop()
    {
        this.stop = true;
    }

    @Override
    public void run()
    {
        while( !stop )
        {
            if ( player.isPlaying())
            {
                BusManager.getBus().post(new NewTimeMessage( player.getCurrentPosition() ));
            }
            try
            {
                Thread.sleep(900);
            }
            catch ( Exception e )
            {
            }
        }
    }

    //-----------------------------------------------------------------------------
    // Bus message recivers.
    //-----------------------------------------------------------------------------
    
    /**
     *  
     * @param message
     */
    @SuppressWarnings("unchecked")
    @Subscribe
    public void playMessage( PlayMessage message )
    {
        if ( !player.isPlaying() )
        {
            PlayListManager<Track> plm = PlayListManager.getInstance();
            if (!plm.isEmpty())
            {
                Track track = plm.get();
                if (track != null)
                {
                    player.start( track, message.getPosition() );
                }
            }
        }
        else
        {
            player.togglePausePlay();
        }
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void stopMessge( StopMessage message )
    {
        if ( player.isPlaying())
        {
            player.stop();
        }
    }

    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void pauseMessage( PauseMessage message )
    {
        if ( player.isPlaying() )
        {
            player.togglePausePlay();
        }
    }

    @SuppressWarnings({"unchecked", "UnusedParameters"})
    @Subscribe
    public void nextMessage(NextMessage message)
    {
        PlayListManager<Track> plm = PlayListManager.getInstance();

        if ( plm.next() )
        {
            player.stop();
            Track track = plm.get();
            if ( track != null )
            {
                player.start( track, 0 );
            }
        }
    }

    @SuppressWarnings({"unchecked", "UnusedParameters"})
    @Subscribe
    public void previousMessage( PreviousMessage message )
    {
        PlayListManager<Track> plm = PlayListManager.getInstance();

        if ( plm.previous())
        {
            player.stop();
            Track track = plm.get();
            if ( track != null )
            {
                player.start( track, 0 );
            }
        }
    }

    @Subscribe
    public void seekMessage( SeekMessage message )
    {
        player.seekTo( message.getPosition() );
    }


    /**
     * Pasamos de una canción a la siguiente.
     *
     * @param message
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe
    public void trackEndedMessage( TrackEndedMessage message )
    {
        nextMessage(new NextMessage());
    }

}
