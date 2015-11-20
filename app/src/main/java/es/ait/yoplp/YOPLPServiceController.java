package es.ait.yoplp;

import android.content.Intent;

import es.ait.yoplp.message.BusManager;
import es.ait.yoplp.message.PlayMessage;
import es.ait.yoplp.playlist.PlayListInfoService;

/**
 * Created by aitkiar on 29/09/15.
 */
public class YOPLPServiceController
{
    private static YOPLPServiceController instance;
    private YOPLPActivity activity;

    private YOPLPServiceController( YOPLPActivity activity )
    {
        this.activity = activity;
    }

    protected static YOPLPServiceController getInstance( YOPLPActivity activity )
    {
        if ( instance == null || !instance.activity.equals( activity ))
        {
            instance = new YOPLPServiceController( activity );
        }
        return instance;
    }

    protected void playListInfoServiceStart()
    {
        Intent updateService = new Intent("AAAAA", null, activity, PlayListInfoService.class);
        activity.startService(updateService);
    }

    protected void playListInfoServiceKill()
    {
        Intent updateService = new Intent("AAAAA", null, activity, PlayListInfoService.class);
        activity.stopService(updateService);
    }
}
