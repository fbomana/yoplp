package es.ait.yoplp;

import android.content.Intent;

/**
 * Created by aitkiar on 30/09/15.
 */
public class MediaPlayerServiceController
{
    private YOPLPActivity activity;
    private static MediaPlayerServiceController instance;

    private MediaPlayerServiceController ( YOPLPActivity activity )
    {
        this.activity = activity;
    }

    public static MediaPlayerServiceController getInstance( YOPLPActivity activity )
    {
        if ( instance == null )
        {
            instance = new MediaPlayerServiceController( activity );
        }

        return instance;
    }

    public void next()
    {
        Intent intent = new Intent( MediaPlayerService.ACTION_NEXT, null, activity, MediaPlayerService.class );
        activity.startService(intent);
    }

    public void pause()
    {
        Intent intent = new Intent( MediaPlayerService.ACTION_PAUSE, null, activity, MediaPlayerService.class );
        activity.startService(intent);
    }

    public void play()
    {
        Intent intent = new Intent( MediaPlayerService.ACTION_PLAY, null, activity, MediaPlayerService.class );
        activity.startService(intent);
    }

    public void previous()
    {
        Intent intent = new Intent( MediaPlayerService.ACTION_PREVIOUS, null, activity, MediaPlayerService.class );
        activity.startService(intent);
    }

    public void  stop()
    {
        Intent intent = new Intent( MediaPlayerService.ACTION_STOP, null, activity, MediaPlayerService.class );
        activity.startService(intent);
    }
}
