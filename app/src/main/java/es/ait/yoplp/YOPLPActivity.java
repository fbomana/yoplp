package es.ait.yoplp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import es.ait.yoplp.fileChooser.FileChooserActivity;
import es.ait.yoplp.playlist.PlayListInfoService;
import es.ait.yoplp.playlist.PlayListManager;
import es.ait.yoplp.playlist.PlayListPositionChangeListener;
import es.ait.yoplp.playlist.TimerService;
import es.ait.yoplp.playlist.Track;

public class YOPLPActivity extends AppCompatActivity implements View.OnClickListener, PlayListPositionChangeListener, AdapterView.OnItemClickListener
{
    public int seleccionado = 0;
    PlayListUpdateReciver playListUpateReciver;

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

        ListView listView = ( ListView )findViewById( R.id.playListView );
        listView.setOnItemClickListener( this );
    }

    /**
     * Repintamos la playlist cada vez que tenemos un evento onResume.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        ListView listView = (ListView) findViewById( R.id.playListView );
        listView.setAdapter(new PlayListAdapter(this, R.id.playListView, PlayListManager.getInstance()));

        TextView textView = ( TextView ) findViewById( R.id.textSongTimeLeft );

        if ( playListUpateReciver == null )
        {
            playListUpateReciver = new PlayListUpdateReciver( listView, textView );
            IntentFilter intentFilter = new IntentFilter( );
            intentFilter.addAction( PlayListInfoService.PLAYLISTINFOUPDATED );
            intentFilter.addAction( TimerService.INTENT_TIME_CHANGE );
            registerReceiver(playListUpateReciver, intentFilter);
        }

        if ( !PlayListManager.getInstance().isEmpty() )
        {
            Intent updateService = new Intent("AAAAA", null, this, PlayListInfoService.class);
            startService( updateService );
            Intent timerServie = new Intent( TimerService.ACTION_START, null, this, TimerService.class );
            startService( timerServie );
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
                PlayListManager.getInstance().clear();
                ListView listView = ( ListView )findViewById( R.id.playListView );
                listView.invalidateViews();
                break;
            }
            case R.id.menuSortList:
            {
                PlayListManager.getInstance().sort();
                ListView listView = ( ListView )findViewById( R.id.playListView );
                listView.invalidateViews();
                break;
            }
            case R.id.menuRandomizeList:
            {
                PlayListManager.getInstance().randomize();
                ListView listView = ( ListView )findViewById( R.id.playListView );
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
                Intent intent = new Intent( MediaPlayerService.ACTION_PLAY, null, this, MediaPlayerService.class );
                startService(intent);
                Intent timerServie = new Intent( TimerService.ACTION_START, null, this, TimerService.class );
                startService( timerServie );
                break;
            }
            case R.id.stopButton:
            {
                Intent intent = new Intent( MediaPlayerService.ACTION_STOP, null, this, MediaPlayerService.class );
                startService( intent );
                Intent timerServie = new Intent( TimerService.ACTION_STOP, null, this, TimerService.class );
                startService( timerServie );
                break;
            }
            case R.id.pauseButtopn:
            {
                Intent intent = new Intent( MediaPlayerService.ACTION_PAUSE, null, this, MediaPlayerService.class );
                startService( intent );
                break;
            }
            case R.id.previousButton:
            {
                Intent intent = new Intent( MediaPlayerService.ACTION_PREVIOUS, null, this, MediaPlayerService.class );
                startService( intent );
                break;
            }
            case R.id.nextButton:
            {
                Intent intent = new Intent( MediaPlayerService.ACTION_NEXT, null, this, MediaPlayerService.class );
                startService( intent );
                break;
            }
        }
    }

    @Override
    public void playListPositionChanged(int pointer)
    {
        ((PlayListManager<Track>)PlayListManager.getInstance()).get( seleccionado ).setSelected( false );
        ((PlayListManager<Track>)PlayListManager.getInstance()).get( pointer ).setSelected(true);

        (( ListView )findViewById( R.id.playListView )).invalidateViews();
        seleccionado = pointer;

        Track track = (Track) PlayListManager.getInstance().get( pointer );
        (( TextView )findViewById( R.id.textSongName )).setText( track.getTitle());
        (( TextView )findViewById( R.id.textSongTimeLeft )).setText( track.getDuration());


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        if ( PlayListManager.getInstance().navigateTo( position ))
        {
            Intent intent = new Intent( MediaPlayerService.ACTION_STOP, null, this, MediaPlayerService.class );
            startService(intent);
            intent = new Intent( MediaPlayerService.ACTION_PLAY, null, this, MediaPlayerService.class );
            startService(intent);
            Intent timerServie = new Intent( TimerService.ACTION_START, null, this, TimerService.class );
            startService( timerServie );
        }
    }

    /**
     * In this method we cleanup whatever it's still running, and save the state in order to restore it in
     * the onResume call.
     */
    @Override
    protected void onPause()
    {
/*        Intent timerServie = new Intent( TimerService.ACTION_STOP, null, this, TimerService.class );
        startService( timerServie );*/
        super.onPause();
    }

    class PlayListUpdateReciver extends BroadcastReceiver
    {
        private ListView listView;
        private TextView textView;

        public PlayListUpdateReciver( ListView listView, TextView textView )
        {
            super();
            this.listView = listView;
            this.textView = textView;
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (TimerService.INTENT_TIME_CHANGE.equals(intent.getAction()) )
            {
                textView.setText( Utils.milisToText( intent.getIntExtra("newtime", 0)));
            }
            else
            {
                listView.invalidateViews();
            }
        }
    }
}
