package es.ait.yoplp.fileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import es.ait.yoplp.m3u.M3UReader;
import es.ait.yoplp.playlist.PlayListManager;
import es.ait.yoplp.playlist.Track;

/**
 * Load the files selected into the actual playlist.
 */
public class MusicFileProccessor implements FileProccessor

{

    /**
     * Loads all selected files into the general playlist.
     * If selected file is a folder, it adds all music files found in that folder in a recursive fashion
     */
    @Override
    public void process(List<File> selectedFiles)
    {
        for ( int i = 0; selectedFiles != null && i < selectedFiles.size(); i ++ )
        {
            loadFiles( selectedFiles.get( i ));
        }
    }

    /**
     * Recursive algortihm. It can create a potentially very large number of arrays, so it must be
     * optimized sometime
     *
     * @param file
     */
    private void loadFiles( File file )
    {
        if ( file.canRead())
        {
            if (file.isDirectory())
            {
                File files[] = file.listFiles( new MusicFileFilter());
                Arrays.sort(files, new FileComparator());
                for ( int i = 0; i < files.length; i ++ )
                {
                    if ( files[i].isDirectory())
                    {
                        loadFiles( files[i] );
                    }
                    else // Avoid extra recursive calls that generate overhead
                    {
                        if ( "m3u".equals( files[i].getName().substring( files[i].getName().lastIndexOf(".") + 1 )))
                        {
                            // Avoid reading m3u files in a directory
                        }
                        else
                        {
                            PlayListManager.getInstance().add(new Track(files[i]));
                        }
                    }
                }
            }
            else
            {
                if ( "m3u".equals( file.getName().substring( file.getName().lastIndexOf(".") + 1 )))
                {
                    try
                    {
                        PlayListManager.getInstance().addAll(M3UReader.getInstance(file).parse());
                    }
                    catch ( IOException e )
                    {
                    }
                }
                else
                {
                    PlayListManager.getInstance().add(new Track(file));
                }
            }
        }
    }
}
