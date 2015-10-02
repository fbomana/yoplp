package es.ait.yoplp.playlist;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

import es.ait.yoplp.Utils;
import es.ait.yoplp.message.BusManager;
import es.ait.yoplp.message.PlayListUpdatedMessage;

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
//        try
//        {
            PlayListManager<Track> plm = PlayListManager.getInstance();
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Track track;
            for (int i = 0; i < plm.size(); i++)
            {
                track = plm.get(i);
                loadTrackMetadata( track, retriever );
            }

            BusManager.getBus().post( new PlayListUpdatedMessage() );
//        }
//        catch ( Exception e )
//        {
//            Log.e("[YOPLP]","Error al obtener información de la playlist", e );
//        }
    }

    public static void loadTrackMetada( Track track )
    {
        loadTrackMetadata( track, new MediaMetadataRetriever() );
    }

    private static void loadTrackMetadata( Track track, MediaMetadataRetriever retriever )
    {
        if (track.getDuration() == null || track.getAuthor() == null )
        {
            retriever.setDataSource(track.getFile().getAbsolutePath());
            track.setAuthor(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR));
            if ( track.getAuthor() == null || "".equals( track.getAuthor().trim()))
            {
                track.setAuthor( retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_ARTIST));
            }
            track.setTitle(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            track.setAlbum(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            if (track.getTitle() == null || "".equals(track.getTitle().trim()))
            {
                track.setTitle(track.getFile().getName());
            }
            long duration = 0;
            try
            {
                duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                track.setDurationMillis(duration);
                track.setDuration(Utils.milisToText(duration));

            } catch (Exception e)
            {

            }
        }
        if (track.getDurationMillis() == 0)
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
}
