package es.ait.yoplp.exoplayer;

import android.content.Context;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import java.util.concurrent.atomic.AtomicBoolean;

import es.ait.yoplp.Utils;
import es.ait.yoplp.message.BusManager;
import es.ait.yoplp.message.TrackEndedMessage;
import es.ait.yoplp.playlist.Track;

/**
 * This class wrap and exoplayer instance and provides standard play, pause, stop, ... methods.
 */
public class YOPLPAudioPlayer implements MediaCodecAudioTrackRenderer.EventListener, ExoPlayer.Listener
{
    // Singleton managmente
    private static YOPLPAudioPlayer instance;

    public static YOPLPAudioPlayer getInstance( Context context )
    {
        if ( instance == null )
        {
            instance = new YOPLPAudioPlayer( context );
        }
        return instance;
    }

    public static YOPLPAudioPlayer getInstance()
    {
        return instance;
    }

    private YOPLPAudioPlayer( Context context )
    {
        this.context = context;
        player = ExoPlayer.Factory.newInstance( 1 ); // We only play audio, so we only need one renderer
        dataSource = new DefaultUriDataSource(context, Util.getUserAgent( context, "ExoPlayerTest"));
        mainHandler = new Handler();
        allocator = new DefaultAllocator( BUFFER_SEGMENT_SIZE );
    }

    // ----------------------------------------------------------
    // Actual class.
    //-----------------------------------------------------------

    private final ExoPlayer player;

    private final Handler mainHandler;
    private final DataSource dataSource;
    private final Allocator allocator;
    private final Context context;
    private final AtomicBoolean playing = new AtomicBoolean( false );
    private final AtomicBoolean paused = new AtomicBoolean( false );

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;


    // ----------------------------------------------------------
    // Command methods
    //-----------------------------------------------------------


    /**
     * Starts playing a new track from the begining.
     *
     * @param track the track we want to play
     */
    public void start( Track track )
    {
        start(track, 0);
    }

    /**
     * Starts playing the track from the position parameter.
     *
     * @param track The track to play
     * @param position The position to start to play
     */
    public void start( Track track, long position )
    {
        if ( player.getPlaybackState() == ExoPlayer.STATE_READY ||
            player.getPlaybackState() == ExoPlayer.STATE_BUFFERING ||
            player.getPlaybackState() == ExoPlayer.STATE_PREPARING )
        {
            player.stop();
        }
        Uri uri = Uri.fromFile( track.getFile() );
        SampleSource sampleSource = null;

        // Work arround for more formats
        if ( "flac".equals(Utils.getExtension( track.getFile())) || "ogg".equals(Utils.getExtension( track.getFile())) )
        {
            //noinspection deprecation
            sampleSource = new FrameworkSampleSource( context, uri, null);
        }
        if ( sampleSource == null )
        {
            sampleSource = new ExtractorSampleSource(uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
        }

        MediaCodecAudioTrackRenderer audioTrackRenderer = new MediaCodecAudioTrackRenderer( sampleSource, MediaCodecSelector.DEFAULT,
            null, true, mainHandler, this );

        player.addListener(this);
        player.prepare(audioTrackRenderer);
        player.seekTo(position);
        player.setPlayWhenReady( true );
        playing.set(true);
        paused.set( false );
    }

    /**
     * Stops the player and set the current position to 0.
     */
    public void stop()
    {
        if ( playing.get())
        {
            playing.set( false );
            paused.set( false );
            player.stop();
            player.seekTo(0);
        }
    }

    /**
     * Alters between play and pause.
     */
    public void togglePausePlay()
    {
        if ( player.getPlaybackState() != ExoPlayer.STATE_IDLE )
        {
            player.setPlayWhenReady(!player.getPlayWhenReady());
            paused.set( !paused.get());
        }
    }


    public void seekTo( long position )
    {
        player.seekTo( position );
        if ( !playing.get())
        {
            player.setPlayWhenReady( true );
            playing.set( true );
            paused.set( false );
        }
    }
    /**
     * Check if the player is trying to play something. When the player it's in IDDLE or ENDED state
     * it's considered that it's not playing.
     * @return
     */
    public boolean isPlaying()
    {
        return playing.get();
    }

    public boolean isPaused() {
        return paused.get();
    }


    /**
     * Returns the current playing position or 0 if it's stopped or not playing.
     * @return
     */
    public long getCurrentPosition()
    {
        if ( isPlaying())
        {
            return player.getCurrentPosition();
        }
        return 0;
    }

    public long getDuration()
    {
        return player.getDuration() != ExoPlayer.UNKNOWN_TIME ? player.getDuration() : 0l;
    }

    // ----------------------------------------------------------
    // Listener Methods
    //-----------------------------------------------------------
    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e)
    {

    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e)
    {

    }

    @Override
    public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs)
    {

    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e)
    {

    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e)
    {

    }

    @Override
    public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs)
    {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState)
    {
        if ( playbackState == ExoPlayer.STATE_ENDED )
        {
            playing.set( false );
            paused.set( false );
            BusManager.getBus().post( new TrackEndedMessage() );
        }
    }

    @Override
    public void onPlayWhenReadyCommitted()
    {
        playing.set( true );
        paused.set( false );
    }

    @Override
    public void onPlayerError(ExoPlaybackException error)
    {
    }
}
