package es.ait.yoplp.message;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Classe que extiende bus y se asegura de que todos los mensajes se publican sobre el Bus del hilo principal
 */
class MainThreadBus extends Bus
{
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            super.post(event);
        }
        else
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    MainThreadBus.super.post(event);
                }
            });
        }
    }
}
