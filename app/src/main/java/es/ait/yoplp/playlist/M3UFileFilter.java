package es.ait.yoplp.playlist;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by aitkiar on 7/11/15.
 */
public class M3UFileFilter implements FileFilter
{
    @Override
    public boolean accept(File pathname)
    {
        return pathname.isDirectory() || pathname.getAbsolutePath().toUpperCase().endsWith(".M3U");
    }
}
