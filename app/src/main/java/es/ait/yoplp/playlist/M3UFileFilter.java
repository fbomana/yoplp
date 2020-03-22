package es.ait.yoplp.playlist;

import java.io.File;
import java.io.FileFilter;

/**
 * Clase que implementa el interfaz FileFilter para ficheros m3u
 */
public class M3UFileFilter implements FileFilter
{
    @Override
    public boolean accept(File pathname)
    {
        return pathname.isDirectory() || pathname.getAbsolutePath().toUpperCase().endsWith(".M3U");
    }
}
