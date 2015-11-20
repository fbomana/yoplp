package es.ait.yoplp.playlist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import es.ait.yoplp.exoplayer.YOPLPAudioPlayer;

/**
 * Created by aitkiar on 20/11/15.
 */
public class PlayListService extends Service
{
    private YOPLPPlayingThread playingThread;

    @Override
    public void onCreate()
    {
        Log.e("[YOPLP", "En el método onCreate del servicio");
        playingThread = new YOPLPPlayingThread(YOPLPAudioPlayer.getInstance(getApplicationContext()));
        Thread thread = new Thread( playingThread );
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand( intent, flags, startId );
        Log.e("[YOPLP", "En el método onStart del servicio");
        return START_STICKY;
    }

        @Override
    public void onDestroy()
    {
        playingThread.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
