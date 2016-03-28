package es.ait.yoplp.playlist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Servicio que inicia el hilo de reproducción de ficheros.
 */
public class PlayListService extends Service
{
    private YOPLPPlayingThread playingThread;

    @Override
    public void onCreate()
    {
        Log.e("[YOPLP", "En el método onCreate del servicio");
        playingThread = YOPLPPlayingThread.getInstance( getApplicationContext());
        Thread thread = new Thread( playingThread );
        thread.start();
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
