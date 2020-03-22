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
        Log.d( LOGCAT, String.format( "Phone event received: %s", intent.getAction()));
        if ( intent != null && "android.intent.action.PHONE_STATE".equals( intent.getAction()))
        {
            Bundle extras = intent.getExtras();
            if ( extras != null )
            {
                Log.d( LOGCAT, String.format("Phone event %s", extras.getString("state") ));
                if ( "RINGING".equals( extras.getString("state")) || "OFFHOOK".equals( extras.getString("state")))
                {
                    if ( YOPLPAudioPlayer.getInstance() != null && YOPLPAudioPlayer.getInstance().isPlaying())
                    {
                        BusManager.getBus().post( new PauseMessage());
                    }
                }
                else if ( YOPLPAudioPlayer.getInstance() != null && "IDLE".equals( extras.getString("state")))
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
