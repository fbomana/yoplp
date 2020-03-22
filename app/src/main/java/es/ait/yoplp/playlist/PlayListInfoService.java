package es.ait.yoplp.playlist;

import android.app.IntentService;
import android.content.Intent;
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
    public PlayListInfoService()
    {
        super("PlayListInfoService");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            PlayListManager<Track> plm = PlayListManager.getInstance();
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Track track;
            for (int i = 0; i < plm.size(); i++)
            {
                track = plm.get(i);
                loadTrackMetadata( track, retriever );
            }

            BusManager.getBus().post( new PlayListUpdatedMessage() );
        }
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
    }

    public static void loadTrackMetada( Track track )
    {
        loadTrackMetadata( track, new MediaMetadataRetriever() );
    }

    private static void loadTrackMetadata( Track track, MediaMetadataRetriever retriever )
    {

        long duration;
        if (track.getDuration() == null || track.getAuthor() == null )
        {
            try
            {
                retriever.setDataSource(track.getFile().getAbsolutePath());
                track.setAuthor(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR));
                if (track.getAuthor() == null || "".equals(track.getAuthor().trim()))
                {
                    track.setAuthor(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                }
                track.setTitle(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                track.setAlbum(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                if (track.getTitle() == null || "".equals(track.getTitle().trim()))
                {
                    track.setTitle(track.getFile().getName());
                }
                try
                {
                    duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                } catch (Exception e)
                {
                    duration = 0;
                }
                track.setDurationMillis(duration);
                track.setDuration(Utils.milisToText(duration));
            }
            catch ( Throwable t )
            {
                Log.e( "[YOPLP]", " Error during media metadata retriving on file: " + track.getFile(), t );
            }
        }
        if (track.getDurationMillis() == 0)
        {
            try
            {
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(track.getFile().getAbsolutePath());
                mp.prepare();
                duration = mp.getDuration();

                mp.release();
            }
            catch (IOException e)
            {
                duration = 0;
            }
            if (duration > -1)
            {
                track.setDurationMillis(duration);
                track.setDuration(Utils.milisToText(duration));
            }

        }
    }
}
