package es.ait.yoplp.playlist;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import java.io.IOException;

import es.ait.yoplp.Utils;

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
        Log.i("[YOPLP]", "---------- Inicio ---------");
        Log.i("[YOPLP]", System.currentTimeMillis() + "" );
        for ( int i = 0; i < plm.size();i ++ )
        {
            track = plm.get( i );
            if ( track.getDuration() == null )
            {
                if ( track.getFile().getName().toLowerCase().endsWith(".mp3"))
                {
                    try
                    {
                        Mp3File mp3file = new Mp3File(track.getFile().getAbsolutePath());
                        if ( mp3file.hasId3v1Tag())
                        {
                            ID3v1 id3v1 = mp3file.getId3v1Tag();
                            track.setTitle( id3v1.getTitle());
                            track.setAuthor( id3v1.getArtist());
                            track.setAlbum( id3v1.getAlbum());
                        }
                        if ( mp3file.hasId3v2Tag())
                        {
                            ID3v2 id3v2 = mp3file.getId3v2Tag();
                            if ( track.getTitle() == null || "".equals( track.getTitle().trim()))
                            {
                                track.setTitle( id3v2.getTitle());
                            }
                            if ( track.getAuthor() == null || "".equals( track.getAuthor().trim()))
                            {
                                track.setAuthor( id3v2.getArtist());
                            }
                            if ( track.getAlbum() == null || "".equals( track.getAlbum().trim()))
                            {
                                track.setAlbum(id3v2.getAlbum());
                            }
                        }
                        track.setDurationMillis( mp3file.getLengthInMilliseconds() );
                        track.setDuration( Utils.milisToText( mp3file.getLengthInMilliseconds()));
                    }
                    catch ( Exception e )
                    {
                        Log.e("[YOPLP]", "Error al parsear el fichero: " + track.getFile().getName(), e );
                    }
                }
                if ( track.getTitle() == null || "".equals( plm.get( i ).getTitle().trim()))
                {
                    retriever.setDataSource(track.getFile().getAbsolutePath());
                    track.setAuthor(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR));
                    track.setTitle(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                    track.setAlbum(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                }
                if ( track.getTitle() == null || "".equals( plm.get( i ).getTitle().trim()))
                {
                    track.setTitle(track.getFile().getName());
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
                }
                catch ( IOException e )
                {
                }
            }
        }
        Log.i("[YOPLP]", System.currentTimeMillis() + "" );
        Log.i("[YOPLP]", "---------- Fin ---------");
        Intent message = new Intent( PlayListInfoService.PLAYLISTINFOUPDATED );
        sendBroadcast( message );
    }
}
