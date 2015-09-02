package es.ait.yoplp.playlist;

import java.io.File;

/**
 * Class that represents a track on our playlist.
 * It consist on the File object pointing to the actual file on the device filesystem and several metadata
 * recollected using MediaMetadataRetriever class.
 */
public class Track
{
    private File file;
    private String duration;
    private String title;
    private String author;
    private boolean selected;

    public Track( File file )
    {
        this.file = file;
        this.setSelected(false);
    }

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}
