package es.ait.yoplp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import es.ait.yoplp.exoplayer.YOPLPAudioPlayer;
import es.ait.yoplp.fileChooser.FileChooserActivity;
import es.ait.yoplp.fileChooser.FileComparator;
import es.ait.yoplp.fileChooser.MusicFileFilter;
import es.ait.yoplp.fileChooser.MusicFileProccessor;
import es.ait.yoplp.m3u.M3UReader;
import es.ait.yoplp.m3u.M3UWriter;
import es.ait.yoplp.message.BusManager;
import es.ait.yoplp.message.NewTimeMessage;
import es.ait.yoplp.message.NextMessage;
import es.ait.yoplp.message.PauseMessage;
import es.ait.yoplp.message.PlayListUpdatedMessage;
import es.ait.yoplp.message.PlayMessage;
import es.ait.yoplp.message.PreviousMessage;
import es.ait.yoplp.message.StopMessage;
import es.ait.yoplp.message.TrackEndedMessage;
import es.ait.yoplp.playlist.M3UFileFilter;
import es.ait.yoplp.playlist.M3UFileProccessor;
import es.ait.yoplp.playlist.PlayListManager;
import es.ait.yoplp.playlist.PlayListPositionChangeListener;
import es.ait.yoplp.playlist.PlayListService;
import es.ait.yoplp.playlist.SavePlayListDialog;
import es.ait.yoplp.playlist.Track;
import es.ait.yoplp.playlist.YOPLPPlayingThread;
import es.ait.yoplp.settings.YOPLPSettingsActivity;

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
    private AtomicBoolean iniciarReproduccion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);

            YOPLPPlayingThread.getInstance( this ); // This way the runnable item it's created and associated to the bus before onResume method

            Intent audioServiceIntent = new Intent(this, PlayListService.class );
            startService( audioServiceIntent );

            setContentView(R.layout.yoplp);

            ImageButton button = (ImageButton) findViewById(R.id.playButton);
            button.setOnClickListener(this);

            button = (ImageButton) findViewById(R.id.stopButton);
            button.setOnClickListener(this);

            button = (ImageButton) findViewById(R.id.pauseButtopn);
            button.setOnClickListener(this);

            button = (ImageButton) findViewById(R.id.previousButton);
            button.setOnClickListener(this);

            button = (ImageButton) findViewById(R.id.nextButton);
            button.setOnClickListener(this);
            PlayListManager.getInstance().addPlayListPositionChangeListener(this);

            upButton = (ImageButton) findViewById(R.id.buttonUp);
            upButton.setOnClickListener(this);

            downButton = (ImageButton) findViewById(R.id.buttonDown);
            downButton.setOnClickListener(this);

            deleteButton = (ImageButton) findViewById(R.id.buttonDelete);
            deleteButton.setOnClickListener(this);

            listView = (ListView) findViewById(R.id.playListView);
            listView.setOnItemClickListener(this);

            textSongAlbum = (TextView) findViewById(R.id.textSongAlbum);
            textSongAuthor = (TextView) findViewById(R.id.textSongAuthor);

            iniciarReproduccion = new AtomicBoolean( false );
            if (PlayListManager.getInstance().isEmpty())
            {
                try
                {
                    File file = new File(getBaseContext().getFilesDir(), "yoplpsavedplaylist.m3u");
                    if (file.exists())
                    {
                        Log.i("[YOPLP]", "Reading last playlist from internal storage");
                        PlayListManager.getInstance().addAll(M3UReader.getInstance(file).parse());
                        file.delete();
                        if ( !PlayListManager.getInstance().isEmpty())
                        {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                            int defaultValue = sharedPref.getInt("Selected track", 0);
                            PlayListManager.getInstance().setPointer(defaultValue);
                            iniciarReproduccion.set(true);
                        }
                    }
                } catch (Exception e)
                {
                    Log.e("[YOPLP]", "Error while reading last playlist from internal storage", e);
                }
            }

            selecciontModeButton = (ToggleButton) findViewById(R.id.buttonSelecctionMode);
            selecciontModeButton.setOnClickListener(this);
        }
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
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
        try
        {
            super.onResume();
            BusManager.getBus().register(this);
            Log.e("[YOPLP]", "En el método onResume de YOPLPActivity");
            Log.e("[YOPLP]", "Bus: " + BusManager.getBus().toString());
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
            }
            selecciontModeButton.setEnabled( !PlayListManager.getInstance().isEmpty() );

            if ( iniciarReproduccion.get() )
            {
                iniciarReproduccion.set(false);
                PlayListManager.getInstance().navigateTo(seleccionado);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( this );
                if ( sharedPref.getBoolean("prefAutoplay", false) )
                {
                    Log.e("[YOPLP]", new Date().getTime() + " Mensaje de iniciar reproducción");
                    BusManager.getBus().post(new PlayMessage(sharedPref.getLong("playing position", 0)));
                }
            }
        }
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
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
        try
        {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            switch( id )
            {
                case R.id.menuAddFiles:
                {
                    Intent intent = new Intent(this, FileChooserActivity.class);
                    intent.putExtra("FileChooserActivity.fileFilter", MusicFileFilter.class.getName());
                    intent.putExtra("FileChooserActivity.fileComparator", FileComparator.class.getName());
                    intent.putExtra("FileChooserActivity.fileProccessor", MusicFileProccessor.class.getName());
                    startActivity(intent);
                    break;
                }
                case R.id.menuClearList:
                {
                    YOPLPServiceController.getInstance( this ).playListInfoServiceKill();
                    BusManager.getBus().post( new StopMessage());
                    PlayListManager.getInstance().clear();
                    this.seleccionado = 0;
                    listView.invalidateViews();
                    break;
                }
                case R.id.menuLoadList:
                {
                    Intent intent = new Intent(this, FileChooserActivity.class);
                    intent.putExtra("FileChooserActivity.fileFilter", M3UFileFilter.class.getName());
                    intent.putExtra("FileChooserActivity.fileComparator", FileComparator.class.getName());
                    intent.putExtra("FileChooserActivity.fileProccessor", M3UFileProccessor.class.getName());
                    intent.putExtra("FileChooserActivity.multiSelect", false);
                    if (!PreferenceManager.getDefaultSharedPreferences( this ).getString("prefDefaultM3UFolder","").equals( "" ))
                    {
                        intent.putExtra("FileChooserActivity.initialFolder", PreferenceManager.getDefaultSharedPreferences( this ).getString("prefDefaultM3UFolder",""));
                    }
                    startActivity(intent);
                    break;
                }
                case R.id.menuSaveList:
                {
                    if ( !PlayListManager.getInstance().isEmpty())
                    {
                        DialogFragment dialog = new SavePlayListDialog();
                        dialog.show(getSupportFragmentManager(), "Save PlayList");
                    }
                    break;
                }
                case R.id.menuSortList:
                {
                    YOPLPServiceController.getInstance( this ).playListInfoServiceKill();
                    PlayListManager.getInstance().sort();
                    seleccionado = PlayListManager.getInstance().getPointer();
                    YOPLPServiceController.getInstance( this ).playListInfoServiceStart();
                    listView.invalidateViews();
                    break;
                }
                case R.id.menuRandomizeList:
                {
                    YOPLPServiceController.getInstance( this ).playListInfoServiceKill();
                    PlayListManager.getInstance().randomize();
                    seleccionado = PlayListManager.getInstance().getPointer();
                    YOPLPServiceController.getInstance( this ).playListInfoServiceStart();
                    listView.invalidateViews();
                    break;
                }
                case R.id.action_settings:
                {
                    Intent intent = new Intent(this, YOPLPSettingsActivity.class);
                    startActivity(intent);
                    break;
                }
            }

            return super.onOptionsItemSelected(item);
        }
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
    }

    @Override
    public void onClick(View v)
    {
        try
        {
            if ( seleccionado == 0 )
            {
                PlayListManager.getInstance().first();
            }

            switch ( v.getId())
            {
                case R.id.playButton:
                {
                    BusManager.getBus().post( new PlayMessage());
                    break;
                }
                case R.id.stopButton:
                {
                    BusManager.getBus().post(new StopMessage());
                    break;
                }
                case R.id.pauseButtopn:
                {
                    BusManager.getBus().post(new PauseMessage());
                    break;
                }
                case R.id.previousButton:
                {
                    BusManager.getBus().post(new PreviousMessage());
                    break;
                }
                case R.id.nextButton:
                {
                    BusManager.getBus().post( new NextMessage());
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
                    playListPositionChanged(PlayListManager.getInstance().getPointer());
                    break;
                }
                case R.id.buttonDown:
                {
                    PlayListManager.getInstance().moveSelectedDown();
                    seleccionado = PlayListManager.getInstance().getPointer();
                    playListPositionChanged(PlayListManager.getInstance().getPointer());
                    break;
                }
                case R.id.buttonDelete:
                {
                    boolean wasPlaying = false;
                    if ( !PlayListManager.getInstance().isEmpty() && ((Track)PlayListManager.getInstance().get()).isSelected())
                    {
                        wasPlaying = YOPLPAudioPlayer.getInstance().isPlaying();
                        BusManager.getBus().post( new StopMessage());

                    }
                    PlayListManager.getInstance().removeSelected();
                    seleccionado = PlayListManager.getInstance().getPointer();
                    if ( !PlayListManager.getInstance().isEmpty() && wasPlaying )
                    {
                        BusManager.getBus().post( new PlayMessage());
                    }
                    playListPositionChanged(PlayListManager.getInstance().getPointer());
                    break;
                }

            }
        }
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
    }

    @Override
    public void playListPositionChanged(int pointer)
    {
        try
        {
            if (PlayListManager.getInstance().size() > seleccionado)
            {
                ((PlayListManager<Track>) PlayListManager.getInstance()).get(seleccionado).setPlaying(false);
            }
            ((PlayListManager<Track>) PlayListManager.getInstance()).get(pointer).setPlaying(true);

            listView.invalidateViews();

            if (pointer > seleccionado && pointer < listView.getAdapter().getCount() - 1)
            {
                listView.smoothScrollToPosition(pointer + 1);
            }
            else if (pointer < seleccionado && pointer > 0)
            {
                listView.smoothScrollToPosition(pointer - 1);
            }
            else
            {
                listView.smoothScrollToPosition(pointer);
            }
            seleccionado = pointer;

            Track track = (Track) PlayListManager.getInstance().get(pointer);
            ((TextView) findViewById(R.id.textSongName)).setText(track.getTitle());
            ((TextView) findViewById(R.id.textSongTimeLeft)).setText(track.getDuration());
            if (textSongAlbum != null)
            {
                textSongAlbum.setText(track.getAlbum());
            }
            if (textSongAuthor != null)
            {
                textSongAuthor.setText(track.getAuthor());
            }
        }
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        try
        {
            if (selecciontModeButton.isChecked())
            {
                ((Track) PlayListManager.getInstance().get(position)).toggleSelected();
                ((PlayListAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
            else
            {
                if (PlayListManager.getInstance().navigateTo(position))
                {
                    BusManager.getBus().post(new StopMessage());
                    BusManager.getBus().post(new PlayMessage());
                }
            }
        }
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
    }

    /**
     * In this method we cleanup whatever it's still running, and save the state in order to restore it in
     * the onResume call.
     */
    @Override
    protected void onPause()
    {
        try
        {
            BusManager.getBus().unregister(this);
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
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if ( !PlayListManager.getInstance().isEmpty())
        {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("Selected track", PlayListManager.getInstance().getPointer());
            if ( sharedPref.getBoolean("prefRememberTime", false ))
            {
                editor.putLong("playing position", YOPLPAudioPlayer.getInstance().getCurrentPosition());
            }
            editor.commit();
        }
        // Detenemos el servicio de reperoducción al detener la aplicación,.
        /* Intent audioServiceIntent = new Intent( this, PlayListService.class );
        stopService( audioServiceIntent );*/
    }

    /*
     ***************************** Bus listeners **************************************
     */

    @Subscribe
    public void newTimeMessage( final NewTimeMessage message )
    {
        try
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
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
    }

    @Subscribe
    public void playListUpdatedMessage( PlayListUpdatedMessage message )
    {
        try
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
        catch ( Throwable t )
        {
            Utils.dumpException( getBaseContext(), t );
            throw t;
        }
    }
}
