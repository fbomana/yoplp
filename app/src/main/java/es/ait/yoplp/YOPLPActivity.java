package es.ait.yoplp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;

import es.ait.yoplp.fileChooser.FileChooserActivity;
import es.ait.yoplp.m3u.M3UReader;
import es.ait.yoplp.m3u.M3UWriter;
import es.ait.yoplp.message.BusManager;
import es.ait.yoplp.message.NewTimeMessage;
import es.ait.yoplp.message.PlayListUpdatedMessage;
import es.ait.yoplp.playlist.PlayListInfoService;
import es.ait.yoplp.playlist.PlayListManager;
import es.ait.yoplp.playlist.PlayListPositionChangeListener;
import es.ait.yoplp.playlist.TimerService;
import es.ait.yoplp.playlist.Track;

public class YOPLPActivity extends AppCompatActivity implements View.OnClickListener, PlayListPositionChangeListener, AdapterView.OnItemClickListener
{
    private int seleccionado = 0;
    private TextView textSongAlbum;
    private TextView textSongAuthor;
    private ToggleButton selecciontModeButton;
    private ImageButton upButton;
    private ImageButton downButton;
    private ImageButton deleteButton;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yoplp);

        ImageButton button = (ImageButton) findViewById( R.id.playButton );
        button.setOnClickListener(this);

        button = (ImageButton) findViewById( R.id.stopButton );
        button.setOnClickListener( this );

        button = (ImageButton) findViewById( R.id.pauseButtopn );
        button.setOnClickListener( this );

        button = (ImageButton) findViewById( R.id.previousButton );
        button.setOnClickListener( this );

        button = (ImageButton) findViewById( R.id.nextButton );
        button.setOnClickListener(this);
        PlayListManager.getInstance().addPlayListPositionChangeListener(this);

        upButton = (ImageButton) findViewById( R.id.buttonUp );
        upButton.setOnClickListener(this);

        downButton = (ImageButton) findViewById( R.id.buttonDown );
        downButton.setOnClickListener( this );

        deleteButton = (ImageButton) findViewById( R.id.buttonDelete );
        deleteButton.setOnClickListener( this );

        listView = ( ListView )findViewById( R.id.playListView );
        listView.setOnItemClickListener( this );

        textSongAlbum = ( TextView )findViewById( R.id.textSongAlbum );
        textSongAuthor = ( TextView )findViewById( R.id.textSongAuthor );

        if ( PlayListManager.getInstance().isEmpty())
        {
            try
            {
                File file = new File(getBaseContext().getFilesDir(), "yoplpsavedplaylist.m3u");
                if ( file.exists() )
                {
                    Log.i("[YOPLP]", "Reading last playlist from internal storage");
                    PlayListManager.getInstance().addAll(M3UReader.getInstance(file).parse());
                    file.delete();
                }
            }
            catch ( Exception e )
            {
                Log.e("[YOPLP]", "Error while reading last playlist from internal storage", e );
            }
        }

        selecciontModeButton = ( ToggleButton )findViewById( R.id.buttonSelecctionMode );
        selecciontModeButton.setOnClickListener( this );
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    /**
     * Repintamos la playlist cada vez que tenemos un evento onResume.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        BusManager.getBus().register( this );
        this.seleccionado = PlayListManager.getInstance().getPointer();

        listView.setAdapter(new PlayListAdapter(this, R.id.playListView, PlayListManager.getInstance()));

        TextView textView = ( TextView ) findViewById( R.id.textSongTimeLeft );



        if ( !PlayListManager.getInstance().isEmpty() )
        {
            if ( textSongAlbum != null )
            {
                textSongAlbum.setText(((Track) PlayListManager.getInstance().get()).getAlbum());
                textSongAuthor.setText(((Track) PlayListManager.getInstance().get()).getAuthor());
            }
            YOPLPServiceController.getInstance( this ).playListInfoServiceStart();
            YOPLPServiceController.getInstance( this ).timerServiceStart();
        }
        selecciontModeButton.setEnabled( !PlayListManager.getInstance().isEmpty() );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch( id )
        {
            case R.id.menuAddFiles:
            {
                Intent intent = new Intent(this, FileChooserActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menuClearList:
            {
                YOPLPServiceController.getInstance( this ).playListInfoServiceKill();
                PlayListManager.getInstance().clear();
                listView.invalidateViews();
                break;
            }
            case R.id.menuSortList:
            {
                YOPLPServiceController.getInstance( this ).playListInfoServiceKill();
                PlayListManager.getInstance().sort();
                YOPLPServiceController.getInstance( this ).playListInfoServiceStart();
                listView.invalidateViews();
                break;
            }
            case R.id.menuRandomizeList:
            {
                YOPLPServiceController.getInstance( this ).playListInfoServiceKill();
                PlayListManager.getInstance().randomize();
                YOPLPServiceController.getInstance( this ).playListInfoServiceStart();
                listView.invalidateViews();
                break;
            }
            case R.id.action_settings:
            {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        if ( seleccionado == 0 )
        {
            PlayListManager.getInstance().first();
        }

        switch ( v.getId())
        {
            case R.id.playButton:
            {
                MediaPlayerServiceController.getInstance( this ).play();
                YOPLPServiceController.getInstance( this ).timerServiceStart();
                break;
            }
            case R.id.stopButton:
            {
                MediaPlayerServiceController.getInstance( this ).stop();
                YOPLPServiceController.getInstance( this ).timerServiceStop();
                break;
            }
            case R.id.pauseButtopn:
            {
                MediaPlayerServiceController.getInstance( this ).pause();
                break;
            }
            case R.id.previousButton:
            {
                MediaPlayerServiceController.getInstance( this ).previous();
                break;
            }
            case R.id.nextButton:
            {
                MediaPlayerServiceController.getInstance( this ).next();
                break;
            }
            case R.id.buttonSelecctionMode:
            {
                if ( selecciontModeButton.isChecked())
                {
                    upButton.setEnabled( true );
                    downButton.setEnabled( true );
                    deleteButton.setEnabled( true );
                }
                else
                {
                    upButton.setEnabled( false );
                    downButton.setEnabled( false );
                    deleteButton.setEnabled( false );
                    PlayListManager.getInstance().deSelectAll();
                    ((PlayListAdapter)listView.getAdapter()).notifyDataSetChanged();
                }
                break;
            }
            case R.id.buttonUp:
            {
                PlayListManager.getInstance().moveSelectedUp();
                seleccionado = PlayListManager.getInstance().getPointer();
                try
                {
                    MediaPlayerAdapter.getInstance().refreshNextPlayer();
                }
                catch ( IOException e )
                {
                    Log.e("[YOPLP]", "Error refreshing nextPlayer after buttonUp click", e);
                }
                playListPositionChanged(PlayListManager.getInstance().getPointer());
                break;
            }
            case R.id.buttonDown:
            {
                PlayListManager.getInstance().moveSelectedDown();
                seleccionado = PlayListManager.getInstance().getPointer();
                try
                {
                    MediaPlayerAdapter.getInstance().refreshNextPlayer();
                }
                catch ( IOException e )
                {
                    Log.e("[YOPLP]", "Error refreshing nextPlayer after buttonDown click", e);
                }
                playListPositionChanged(PlayListManager.getInstance().getPointer());
                break;
            }
            case R.id.buttonDelete:
            {
                if ( ((Track)PlayListManager.getInstance().get()).isSelected())
                {
                    MediaPlayerAdapter.getInstance().stop();
                    YOPLPServiceController.getInstance( this ).timerServiceStop();
                }
                PlayListManager.getInstance().removeSelected();
                seleccionado = PlayListManager.getInstance().getPointer();
                if ( !PlayListManager.getInstance().isEmpty())
                {
                    MediaPlayerServiceController.getInstance( this ).play();
                    YOPLPServiceController.getInstance( this ).timerServiceStart();
                }
                playListPositionChanged(PlayListManager.getInstance().getPointer());
                break;
            }

        }
    }

    @Override
    public void playListPositionChanged(int pointer)
    {
        ((PlayListManager<Track>)PlayListManager.getInstance()).get( seleccionado ).setPlaying( false );
        ((PlayListManager<Track>)PlayListManager.getInstance()).get( pointer ).setPlaying( true );

        listView.invalidateViews();
        seleccionado = pointer;

        Track track = (Track) PlayListManager.getInstance().get(pointer);
        (( TextView )findViewById( R.id.textSongName )).setText( track.getTitle());
        (( TextView )findViewById( R.id.textSongTimeLeft )).setText(track.getDuration());
        if ( textSongAlbum != null )
        {
            textSongAlbum.setText(track.getAlbum());
        }
        if ( textSongAuthor != null )
        {
            textSongAuthor.setText(track.getAuthor());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if ( selecciontModeButton.isChecked())
        {
            ((Track)PlayListManager.getInstance().get( position )).toggleSelected();
            ((PlayListAdapter)listView.getAdapter()).notifyDataSetChanged();
        }
        else
        {
            if (PlayListManager.getInstance().navigateTo(position))
            {
                MediaPlayerServiceController.getInstance( this ).stop();
                MediaPlayerServiceController.getInstance( this ).play();
                YOPLPServiceController.getInstance(this).timerServiceStart();
            }
        }
    }

    /**
     * In this method we cleanup whatever it's still running, and save the state in order to restore it in
     * the onResume call.
     */
    @Override
    protected void onPause()
    {
        BusManager.getBus().unregister( this );
        YOPLPServiceController.getInstance( this ).timerServiceStop();
        if ( !PlayListManager.getInstance().isEmpty())
        {
            Log.i("[YOPLP]", "Writing playlist to internal storage");
            File file = new File(getBaseContext().getFilesDir(), "yoplpsavedplaylist.m3u");
            try
            {
                M3UWriter.getInstance( file ).write( PlayListManager.getInstance());
            }
            catch ( Exception e )
            {
                Log.e("[YOPLP]", "Error while writing last playlist to internal storage", e );
            }
        }
        super.onPause();
    }

    /*
     ***************************** Bus listeners **************************************
     */

    @Subscribe
    public void newTimeMessage( final NewTimeMessage message )
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((TextView) findViewById(R.id.textSongTimeLeft)).setText(message.getNewTimeAsString());
            }
        });
    }

    @Subscribe
    public void playListUpdatedMessage( PlayListUpdatedMessage message )
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                ((ListView) findViewById(R.id.playListView)).invalidateViews();
            }
        });
    }
}
