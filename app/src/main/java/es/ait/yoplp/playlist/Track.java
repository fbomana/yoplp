package es.ait.yoplp.playlist;

import java.io.File;

/**
 * Class that represents a track on our playlist.
 * It consist on the File object pointing to the actual file on the device filesystem and several metadata
 * recollected using MediaMetadataRetriever class.
 */
public class Track implements Comparable<Track>
{
    private File file;
    private String duration;
    private Long durationMillis = 0l;
    private String title;
    private String author;
    private String album;
    private boolean selected;

    public Track( File file )
    {
        this.file = file;
        this.setSelected(false);
        this.durationMillis = 0l;
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

    @Override
    public int compareTo(Track another)
    {
        if ( another == null )
        {
            return 1;
        }
        return file.getAbsolutePath().compareTo( ((Track) another).getFile().getAbsolutePath() );
    }

    public Long getDurationMillis()
    {
        return durationMillis;
    }

    public void setDurationMillis(Long durationMillis)
    {
        this.durationMillis = durationMillis;
    }

    public String getAlbum()
    {
        return album;
    }

    public void setAlbum(String album)
    {
        this.album = album;
    }

    public String toM3U()
    {
        return "#EXTINF:" + ( getDurationMillis() / 1000 ) + "," + (
            getTitle() != null ? getTitle() : getFile().getName().substring(0, getFile().getName().lastIndexOf("."))) + "\n" +
            getFile().getAbsolutePath() + "\n";
    }
}
