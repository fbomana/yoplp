package es.ait.yoplp.settings;

import java.io.File;
import java.io.FileFilter;

/**
 * Filtro que implementa el interfaz FileFilter para seleccionar solo ficheros que sean directorios.
 */
class FolderFilter implements FileFilter
{
    @Override
    public boolean accept(File pathname)
    {
        return pathname.isDirectory();
    }
}
