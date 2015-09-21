package es.ait.yoplp.playlist;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import java.io.IOException;

import es.ait.yoplp.Utils;

/**
 * This service it's used to obtain metadata from the tracks on the playlist. When finished it sends
 * a broadcast with the tag PlayListInfoService.PLAYLISTINFOUPDATED.
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
                track.setAlbum( retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                if ( track.getTitle() == null || "".equals( plm.get( i ).getTitle().trim()))
                {
                    track.setTitle( track.getFile().getName());
                }
                long duration = 0;
                try
                {
                    duration = Long.parseLong( retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_DURATION ));
                    track.setDurationMillis( duration );
                    track.setDuration(Utils.milisToText(duration));

                }
                catch ( Exception e )
                {

                }
            }
            if ( track.getDurationMillis() == 0 )
            {
                try
                {
                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(track.getFile().getAbsolutePath());
                    mp.prepare();
                    long durationMilis = mp.getDuration();
                    if (durationMilis > -1)
                    {
                        track.setDurationMillis(durationMilis);
                        track.setDuration(Utils.milisToText(durationMilis));
                    }

                    mp.release();
                } catch (IOException e)
                {
                }
            }
        }

        Intent message = new Intent( PlayListInfoService.PLAYLISTINFOUPDATED );
        sendBroadcast(message);
    }
}
