package es.ait.yoplp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import es.ait.yoplp.exoplayer.YOPLPAudioPlayer;
import es.ait.yoplp.message.BusManager;
import es.ait.yoplp.message.PauseMessage;
import es.ait.yoplp.message.PlayMessage;

/**
 * Listen for phone state events in order to stop the music if it's playing
 *
 */
public class PhoneStateReciver extends BroadcastReceiver
{
    private final String LOGCAT = "[YOPLP]";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if ( intent != null && "PHONE_STATE".equals( intent.getAction()))
        {
            Bundle extras = intent.getExtras();
            if ( extras != null )
            {
                if ( "RINGING".equals( extras.getString("state")) || "OFFHOOK".equals( extras.getString("state")))
                {
                    if (YOPLPAudioPlayer.getInstance().isPlaying())
                    {
                        BusManager.getBus().post( new PauseMessage());
                    }
                }
                else if ( "IDLE".equals( extras.getString("state")))
                {
                    if ( YOPLPAudioPlayer.getInstance().isPaused())
                    {
                        BusManager.getBus().post( new PlayMessage());
                    }
                }
            }

        }

    }
}
