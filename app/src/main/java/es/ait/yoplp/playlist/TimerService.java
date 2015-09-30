package es.ait.yoplp.playlist;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import es.ait.yoplp.MediaPlayerAdapter;

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
        MediaPlayer mp = MediaPlayerAdapter.getInstance().getActualPlayer();
        Log.i("[YOPLP]", "[TimerService] Inicio del bucle");
        while( !stop.get())
        {
            if ( mp != null && mp.isPlaying())
            {
                Intent message = new Intent( TimerService.INTENT_TIME_CHANGE );
                message.putExtra("newtime", mp.getDuration() - mp.getCurrentPosition());
                sendBroadcast(message);
                Log.i("[YOPLP]", "New time broadcast send");
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
        Log.i("[YOPLP]", "[TimerService] Fin del bucle");
        stopSelf();
    }
}
