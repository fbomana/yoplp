package es.ait.yoplp.fileChooser;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import es.ait.yoplp.R;
import es.ait.yoplp.Utils;

/**
 * Actividad que presenta un selector de ficheros al usuario, para que pueda seleccionar uno a uno los
 * ficheros que va a reproducir o crear un nuevo directorio.
 *
 */
public class FileChooserActivity extends AppCompatActivity implements View.OnClickListener
{

    private final Stack<File> folderStack = new Stack<>();
    private FileChooserActivityConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.file_chooser);

            configuration = new FileChooserActivityConfiguration( getIntent());

            ListView listView = (ListView) findViewById(R.id.fcFileList );
            try
            {
                if ( !folderStack.empty())
                {
                    listView.setAdapter(new FolderAdapter(configuration, this, R.id.fcFileList, folderStack.peek() ));
                }
                else
                {
                    listView.setAdapter(new FolderAdapter(configuration, this, R.id.fcFileList ));
                    if ( configuration.getInitialFolder() != null )
                    {
                        File parent = configuration.getInitialFolder();
                        List<File> files = new ArrayList<>();
                        while ( parent != null )
                        {
                            files.add( parent );
                            parent = parent.getParentFile();
                        }
                        for ( int i = files.size() - 1; i >= 0; i-- )
                        {
                            folderStack.push( files.get( i ));
                        }
                    }
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
                                setUrlText( file );
                            }
                            catch (Exception e)
                            {
                                Utils.dumpException( FileChooserActivity.this, e );
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

            setUrlText(!folderStack.empty() ? folderStack.peek() : null);

            Button okButton = (Button) findViewById(R.id.fcOkButton );
            okButton.setOnClickListener(this);

            Button cancelButton = (Button) findViewById(R.id.fcCancelButton );
            cancelButton.setOnClickListener( this );

            ImageButton newFolderButton = (ImageButton) findViewById(R.id.fcNewFolderButton );
            if ( configuration.isCreateFolder())
            {
                newFolderButton.setEnabled( true );
                newFolderButton.setImageResource(R.drawable.folder_add);
            }
            else
            {
                newFolderButton.setEnabled( false );
                newFolderButton.setImageResource( R.drawable.folder_add_disable );
            }
            newFolderButton.setOnClickListener( this );
        }
        catch ( Throwable t )
        {
            Utils.dumpException(getBaseContext(), t);
            Toast.makeText( this, "An error ocurred: " + t.toString() + "\n" + t.getMessage(), Toast.LENGTH_LONG ).show();
            finish();
        }
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
        try
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
                    ((ListView) findViewById(R.id.fcFileList)).setAdapter(new FolderAdapter( configuration, this, R.id.fcFileList ));
                    setUrlText( null );
                }
                else
                {
                    ((FolderAdapter)((ListView) findViewById(R.id.fcFileList )).getAdapter()).navigateTo(folderStack.peek());
                    setUrlText( folderStack.peek() );
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
     * Method to control onclick on both buttons
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        try
        {
            switch (v.getId())
            {
                case R.id.fcOkButton:
                {
                    configuration.getProccessor().process ( ((FolderAdapter) ((ListView) findViewById(R.id.fcFileList)).getAdapter()).getSelectedList()) ;
                    finish();
                    break;
                }
                case R.id.fcCancelButton:
                {
                    finish();
                    break;
                }
                case R.id.fcNewFolderButton:
                {
                    if ( !folderStack.isEmpty())
                    {
                        DialogFragment dialog = new NewFolderDialog();
                        dialog.show( getSupportFragmentManager(), "New Folder");
                    }
                }
            }
        }
        catch ( IOException e )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.setMessage( e.getMessage());
            builder.setTitle("Error");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        catch (Throwable t)
        {
            Utils.dumpException(getBaseContext(), t);
            throw t;
        }
    }

    /**
     * Pone la url con el valor del fichero asignado o "" si es nulo
     * @param file
     */
    private void setUrlText( File file )
    {
        TextView textView= (TextView) findViewById(R.id.fcUrlText );
        textView.setText( file != null ? file.getAbsolutePath() : "");
    }

    public void newFolder( String folderName )
    {
        if ( !folderStack.isEmpty())
        {
            File folder = new File ( folderStack.peek().getAbsolutePath() + "/" + folderName );
            if ( folder.mkdir() )
            {
                ((FolderAdapter)((ListView) findViewById(R.id.fcFileList)).getAdapter()).navigateTo( folderStack.peek());
            }
        }
    }
}
