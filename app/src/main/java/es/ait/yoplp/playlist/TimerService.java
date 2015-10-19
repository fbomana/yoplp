package es.ait.yoplp.playlist;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import es.ait.yoplp.MediaPlayerAdapter;
import es.ait.yoplp.Utils;
import es.ait.yoplp.message.BusManager;
import es.ait.yoplp.message.NewTimeMessage;

/**
 * Created by aitkiar on 7/09/15.
 */
public class TimerService extends IntentService
{
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_START = "START";
    public static final String INTENT_TIME_CHANGE = "es.ait.yoplp.TimerService.INTENT_TIME_CHANGE";
    private AtomicBoolean stop;

    public TimerService()
    {
        super("TimerService");
        stop = new AtomicBoolean( false );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("[YOPLP]", "onStartCommand stop=[" + stop.get() + "] intent=[" + intent.getAction() + "]");
        if ( ACTION_STOP.equals(intent.getAction()) )
        {
            stop.set( true );
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            MediaPlayer mp = MediaPlayerAdapter.getInstance().getActualPlayer();
            while( !stop.get())
            {
                try
                {
                    if (mp != null && mp.isPlaying())
                    {
                        BusManager.getBus().post(new NewTimeMessage(mp.getDuration() - mp.getCurrentPosition()));
                    }
                }
                catch ( IllegalStateException e )
                {
                    // Capture the Exception that can be cause if someone hits next o previous very fast.
                }

                try
                {
                    Thread.currentThread().sleep(1000);
                }
                catch ( Exception e )
                {
                    Log.i("[YOPLP]", "Excepcion en onHandleItemSleep", e);
                    break;
                }
                mp = MediaPlayerAdapter.getInstance().getActualPlayer();
            }
            stop = new AtomicBoolean( false );
            stopSelf();
        }
        catch ( Throwable t )
        {
            Utils.dumpException(getBaseContext(), t);
            throw t;
        }
    }
}
