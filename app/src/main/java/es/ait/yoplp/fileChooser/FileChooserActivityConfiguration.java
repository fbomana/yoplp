package es.ait.yoplp.fileChooser;

import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;

/**
 * This class helds the configuration of the FileChooserActivity. The configuration it's set automátically
 * from the parameters passed to the FileChooserActivity
 */
class FileChooserActivityConfiguration
{
    private final boolean createFolder;
    private Comparator<File> fileComparator;
    private FileFilter fileFilter;
    private File initialFolder;
    private boolean multiSelect = true;
    private FileProccessor proccessor;


    @SuppressWarnings("unchecked")
    public FileChooserActivityConfiguration ( Intent intent ) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        createFolder = intent.getBooleanExtra( "FileChooserActivity.createFolder", false);
        try
        {
            fileComparator = (Comparator<File>) Class.forName(intent.getStringExtra("FileChooserActivity.fileComparator")).newInstance();
        }
        catch ( NullPointerException e )
        {
            // If it's not defined we resort to the prodvided default.
            fileComparator = new FileComparator();
        }

        if ( intent.getStringExtra("FileChooserActivity.fileFilter") != null )
        {
            fileFilter = (FileFilter) Class.forName(intent.getStringExtra("FileChooserActivity.fileFilter")).newInstance();
        }

        if ( intent.getStringExtra("FileChooserActivity.initialFolder") != null )
        {
            initialFolder = new File ( intent.getStringExtra("FileChooserActivity.initialFolder") );
            if ( !initialFolder.exists() || !initialFolder.canRead())
            {
                initialFolder = null;
            }
        }

        multiSelect = intent.getBooleanExtra( "FileChooserActivity.multiSelect", true );

        if ( intent.getStringExtra("FileChooserActivity.fileProccessor") != null )
        {
            try
            {
                proccessor = ( FileProccessor ) Class.forName( intent.getStringExtra("FileChooserActivity.fileProccessor") ).newInstance();
            }
            catch ( Exception e )
            {
                Log.e("[YOPLP]", "Error instanciando el processor", e );
            }
        }
    }

    public boolean isCreateFolder()
    {
        return createFolder;
    }

    public Comparator<File> getFileComparator()
    {
        return fileComparator;
    }

    public FileFilter getFileFilter()
    {
        return fileFilter;
    }

    public File getInitialFolder()
    {
        return initialFolder;
    }

    public boolean isMultiSelect()
    {
        return multiSelect;
    }

    public FileProccessor getProccessor()
    {
        return proccessor;
    }
}
