package es.ait.yoplp.settings;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by aitkiar on 6/11/15.
 */
public class FolderFilter implements FileFilter
{
    @Override
    public boolean accept(File pathname)
    {
        return pathname.isDirectory();
    }
}
