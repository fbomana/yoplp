package es.ait.yoplp.message;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by aitkiar on 2/10/15.
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
        } else
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
