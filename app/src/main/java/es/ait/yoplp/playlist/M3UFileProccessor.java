package es.ait.yoplp.playlist;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.ait.yoplp.Utils;
import es.ait.yoplp.fileChooser.FileProccessor;
import es.ait.yoplp.m3u.M3UReader;

/**
 * Created by aitkiar on 7/11/15.
 */
public class M3UFileProccessor implements FileProccessor
{
    @Override
    public void process(List<File> selectedFiles)
    {
        if ( selectedFiles != null && !selectedFiles.isEmpty())
        {
            try
            {
                PlayListManager.getInstance().addAll(M3UReader.getInstance(selectedFiles.get(0)).parse());
            }
            catch ( IOException e )
            {
                Log.e("[YOPLP]", "Error reading playlist", e);
            }
        }
    }
}
