package es.ait.yoplp.playlist;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.ait.yoplp.fileChooser.FileProccessor;
import es.ait.yoplp.m3u.M3UReader;

/**
 * Clase que implementa el interfaz FileProcessor para leer el primer fichero m3U de la lista de ficheros.
 */
public class M3UFileProccessor implements FileProccessor
{
    @SuppressWarnings("unchecked")
    @Override
    public void process(List<File> selectedFiles) throws IOException
    {
        if ( selectedFiles != null && !selectedFiles.isEmpty())
        {
            PlayListManager.getInstance().addAll(M3UReader.getInstance(selectedFiles.get(0)).parse());
        }
    }
}
