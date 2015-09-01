package es.ait.yoplp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by aitkiar on 1/09/15.
 */
public class MediaPlayerService extends Service
{
    public static final String ACTION_PLAY = "es.ait.yoplp.PLAY";
    public static final String ACTION_PAUSE = "es.ait.yoplp.PAUSE";
    public static final String ACTION_STOP = "es.ait.yoplp.STOP";
    public static final String ACTION_NEXT = "es.ait.yoplp.NEXT";
    public static final String ACTION_PREVIOUSS = "es.ait.yoplp.PREVIOUSS";
    public static final String ACTION_FIRST = "es.ait.yoplp.FIRST";
    public static final String ACTION_LAST = "es.ait.yoplp.LAST";


    public int onStartCommand(Intent intent, int flags, int startId)
    {

        try
        {
            if (intent.getAction().equals(ACTION_PLAY))
            {
                MediaPlayerAdapter.getInstance().play();
            }
            else if (intent.getAction().equals(ACTION_PAUSE))
            {
                MediaPlayerAdapter.getInstance().pause();
            }
            else if (intent.getAction().equals(ACTION_STOP))
            {
                MediaPlayerAdapter.getInstance().stop();
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand( intent, flags, startId );
    }


    /**
     * El servicio no se puede anular.
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


}