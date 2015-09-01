package es.ait.yoplp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.File;

import es.ait.yoplp.fileChooser.FileChooserActivity;

public class YOPLPActivity extends AppCompatActivity implements View.OnClickListener
{

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
    }

    /**
     * Repintamos la playlist cada vez que tenemos un evento onResume.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        ListView listView = (ListView) findViewById( R.id.playListView );
        listView.setAdapter( new PlayListAdapter( this, R.id.playListView, PlayListManager.getInstance()));
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
        switch ( v.getId())
        {
            case R.id.playButton:
            {
                Intent intent = new Intent( MediaPlayerService.ACTION_PLAY, null, this, MediaPlayerService.class );
                startService( intent );
                break;
            }
            case R.id.stopButton:
            {
                Intent intent = new Intent( MediaPlayerService.ACTION_STOP, null, this, MediaPlayerService.class );
                startService( intent );
                break;
            }
            case R.id.pauseButtopn:
            {
                Intent intent = new Intent( MediaPlayerService.ACTION_PAUSE, null, this, MediaPlayerService.class );
                startService( intent );
                break;
            }
        }
    }
}
