package es.ait.yoplp.playlist;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;


import java.io.IOException;

/**
 * Created by aitkiar on 2/09/15.
 */
public class PlayListInfoService extends IntentService
{
    public static final String PLAYLISTINFOUPDATED = "es.ait.yoplp.PLAYLISTINFOUPDATED";

    public PlayListInfoService()
    {
        super("PlayListInfoService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        PlayListManager<Track> plm = PlayListManager.getInstance();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Track track;
        for ( int i = 0; i < plm.size();i ++ )
        {
            track = plm.get( i );
            if ( track.getDuration() == null )
            {
                retriever.setDataSource(track.getFile().getAbsolutePath());
                track.setAuthor(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR));
                track.setTitle(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                if ( track.getTitle() == null || "".equals( plm.get( i ).getTitle().trim()))
                {
                    track.setTitle( track.getFile().getName());
                }
            }
            try
            {
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(track.getFile().getAbsolutePath());
                mp.prepare();
                long durationMilis = mp.getDuration();
                if ( durationMilis > -1 )
                {
                    long seconds = durationMilis / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    minutes = minutes % 60;
                    seconds = seconds % 60;

                    if ( hours > 0 )
                    {
                        track.setDuration(String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds));
                    }
                    else
                    {
                        track.setDuration(String.format("%1$02d:%2$02d", minutes, seconds));
                    }
                }

                mp.release();
            }
            catch ( IOException e )
            {
            }
        }

        Intent message = new Intent( PlayListInfoService.PLAYLISTINFOUPDATED );
        sendBroadcast( message );
    }
}
