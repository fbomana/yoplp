package es.ait.yoplp.fileChooser;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * This class it's used as a filter to only include folder and music files supported on android.
 *
 */
public class MusicFileFilter implements FileFilter
{
    private String[] supportedExtensions = { "3gp", "aac", "flac", "imy", "m3u", "m4a", "mid", "mkv", "mp3", "mp4", "mxmf", "ogg", "ota", "rtttl", "rtx", "wav", "xmf" };

    @Override
    public boolean accept(File file)
    {
        return (file.isDirectory() || Arrays.binarySearch( supportedExtensions, file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase()) >= 0 ) && file.canRead();
    }
}
