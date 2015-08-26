package es.ait.yoplp.fileChooser;

import java.io.File;
import java.util.Comparator;

/**
 * Compare two files ordering theme in the usual way folder < file and use lexicographic order between two
 * objets of the same type.
 */
public class FileComparator implements Comparator<File>
{
    @Override
    public int compare(File file1, File file2)
    {
        if ( file1.isDirectory() && !file2.isDirectory())
        {
            return -1;
        }
        else if ( !file1.isDirectory() && file2.isDirectory())
        {
            return 1;
        }
        else
        {
            // both file are the same type
            return file1.getName().compareTo(file2.getName());
        }
    }
}
