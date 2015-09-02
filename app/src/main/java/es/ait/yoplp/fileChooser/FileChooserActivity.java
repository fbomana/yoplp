package es.ait.yoplp.fileChooser;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import es.ait.yoplp.R;
import es.ait.yoplp.playlist.PlayListInfoService;

/**
 * Created by aitkiar on 24/08/15.
 */
public class FileChooserActivity extends AppCompatActivity implements View.OnClickListener
{

    private Stack<File> folderStack = new Stack<File>();



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_chooser);


        ListView listView = (ListView) findViewById(R.id.fcFileList );
        try
        {
            if ( !folderStack.empty())
            {
                listView.setAdapter(new FolderAdapter(this, R.id.fcFileList, folderStack.peek()));
            }
            else
            {
                List<File> files = new ArrayList<File>();
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()))
                {
                    File[] aux = new File("/mnt").listFiles();

                    for (int i = 0; i < aux.length; i++)
                    {
                        if ( aux[i].getName().startsWith("sdcard"))
                        {
                            files.add(aux[i]);
                        }
                    }
                }
                files.add( Environment.getDataDirectory());
                files.add( Environment.getRootDirectory());
                listView.setAdapter(new FolderAdapter(this, R.id.fcFileList, files));
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    ((FolderAdapter)parent.getAdapter()).toggleSelected( position, view  );
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
                {
                    File file = (File) parent.getItemAtPosition(position);
                    if (file.isDirectory())
                    {
                        try
                        {
                            folderStack.push( file );
                            ((FolderAdapter) parent.getAdapter()).navigateTo(file);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            System.out.println("------------------------------");
                        }
                    }
                    return true;
                }
            });
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        TextView textView= (TextView) findViewById(R.id.fcUrlText );
        textView.setText(!folderStack.empty() ? folderStack.peek().getAbsolutePath() : "");

        Button okButton = (Button) findViewById(R.id.fcOkButton );
        okButton.setOnClickListener(this);

        Button cancelButton = (Button) findViewById(R.id.fcCancelButton );
        cancelButton.setOnClickListener( this );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_chooser, menu);
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed()
    {
        if ( folderStack.empty())
        {
            super.onBackPressed();
        }
        else
        {
            folderStack.pop();
            if ( folderStack.empty() )
            {
                List<File> files = new ArrayList<File>();
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()))
                {
                    File[] aux = new File("/mnt").listFiles();

                    for (int i = 0; i < aux.length; i++)
                    {
                        if ( aux[i].getName().startsWith("sdcard"))
                        {
                            files.add(aux[i]);
                        }
                    }
                }
                files.add( Environment.getDataDirectory());
                files.add( Environment.getRootDirectory());
                try
                {
                    ((ListView) findViewById(R.id.fcFileList)).setAdapter(new FolderAdapter(this, R.id.fcFileList, files));
                }
                catch ( Exception e )
                {
                }
            }
            else
            {
                ((FolderAdapter)((ListView) findViewById(R.id.fcFileList )).getAdapter()).navigateTo( folderStack.peek());
            }

        }
    }

    /**
     * Method to control onclick on both buttons
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        switch( v.getId())
        {
            case R.id.fcOkButton:
            {
                ((FolderAdapter)((ListView) findViewById(R.id.fcFileList )).getAdapter()).loadFiles();
                finish();
                break;
            }
            case R.id.fcCancelButton:
            {
                finish();
                break;
            }
        }
    }
}
