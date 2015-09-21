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
        long trackInfoTime = 0;
        long trackDurationTime = 0;
        long t1 = 0;
        long t2 = 0;
        long t3 = 0;
        Log.i("[YOPLP]", "---------- Inicio ---------");
        Log.i("[YOPLP]", System.currentTimeMillis() + "" );
        for ( int i = 0; i < plm.size();i ++ )
        {
            track = plm.get( i );
            t1 = System.currentTimeMillis();
            if ( track.getDuration() == null )
            {
                if ( track.getFile().getName().toLowerCase().endsWith(".mp3"))
                {
                    try
                    {
                        Mp3File mp3file = new Mp3File(track.getFile().getAbsolutePath());
                        if ( mp3file.hasId3v2Tag())
                        {
                            ID3v2 id3v2 = mp3file.getId3v2Tag();
                            track.setTitle( id3v2.getTitle());
                            track.setAuthor( id3v2.getArtist());
                            track.setAlbum(id3v2.getAlbum());
                        }
                        else if ( mp3file.hasId3v1Tag())
                        {
                            ID3v1 id3v1 = mp3file.getId3v1Tag();
                            track.setTitle( id3v1.getTitle());
                            track.setAuthor( id3v1.getArtist());
                            track.setAlbum( id3v1.getAlbum());
                        }
//                        track.setDurationMillis( mp3file.getLengthInMilliseconds() );
//                        track.setDuration( Utils.milisToText( mp3file.getLengthInMilliseconds()));
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
            t2 = System.currentTimeMillis();
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
            t3 = System.currentTimeMillis();
            trackInfoTime += ( t2 - t1 );
            trackDurationTime += ( t3 - t2 );
        }

        Log.i("[YOPLP]", "TrackInfoTime=" + trackInfoTime);
        Log.i("[YOPLP]", "TrackDurationTime=" + trackDurationTime );
        Log.i("[YOPLP]", "TotalTime=" + System.currentTimeMillis() );
        Log.i("[YOPLP]", "---------- Fin ---------");
        Intent message = new Intent( PlayListInfoService.PLAYLISTINFOUPDATED );
        sendBroadcast( message );
    }
}
