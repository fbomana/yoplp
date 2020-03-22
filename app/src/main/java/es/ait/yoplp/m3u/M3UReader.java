package es.ait.yoplp.m3u;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import es.ait.yoplp.Utils;
import es.ait.yoplp.playlist.PlayList;
import es.ait.yoplp.playlist.Track;

/**
 * Class that generates a PlayList from a m3u File. It will support #EXTINF to load any metadata available.
 */
public class M3UReader
{
    private final File m3uFile;

    private M3UReader( File m3uFile )
    {
        this.m3uFile = m3uFile;
    }

    /**
     * Gets a reader for the supplied file after checking that the file it's accesible and it has
     * an absolute path.
     * @param m3uFile
     * @return
     * @throws IOException
     */
    public static M3UReader getInstance( File m3uFile ) throws IOException
    {
        if ( m3uFile == null || !m3uFile.canRead() )
        {
            throw new IOException("File is null or can't read.");
        }

        if ( m3uFile.isDirectory())
        {
            throw new IOException("Supplied argument it's not an m3u file");
        }

        if ( !m3uFile.isAbsolute() )
        {
            throw new IOException ("Supplied argument must have an absolute path.");
        }
        return new M3UReader( m3uFile );
    }

    /**
     * Parses the m3u file returning the playlist. If the file has a wrong format it reads what it cans
     * and return an empty playLIist. If there are files that no longer are readable, it excludes them.
     *
     * @return
     */
    public PlayList<Track> parse()
    {
        BufferedReader buff = null;
        PlayList<Track> result = new PlayList<>();
        try
        {
            buff = new BufferedReader( new FileReader( m3uFile ));
            String line;
            Track track;
            String extinf = null;
            while ( ((line = buff.readLine())) != null )
            {
                if ( line.trim().startsWith("#EXTINF"))
                {
                    extinf = line.trim();
                }
                else if ( !line.trim().isEmpty() && !line.trim().startsWith("#"))
                {
                    try
                    {
                        File musicFile = new File(line.trim());
                        if ( !musicFile.isAbsolute())
                        {
                            if ( musicFile.getPath().startsWith(System.getProperty("file.separator")))
                            {
                                musicFile = new File( m3uFile.getParent() + musicFile.getPath());
                            }
                            else
                            {
                                musicFile = new File( m3uFile.getParent() + System.getProperty("file.separator") + musicFile.getPath());
                            }
                        }
                        if ( musicFile.exists() && musicFile.canRead() )
                        {
                            track = new Track(musicFile);
                            processExtInf( track, extinf );
                            result.add( track );
                        }
                    }
                    catch ( Exception e )
                    {
                        //unreadable entry, do nothing.
                    }
                    extinf = null;
                }

            }
        }
        catch ( IOException e )
        {
            // Error leyendo el fichero
        }
        finally
        {
            if ( buff != null )
            {
                //noinspection EmptyCatchBlock
                try
                {
                    buff.close();
                }
                catch ( IOException ignored)
                {
                }
            }
        }

        return result;
    }

    /**
     * Proccess the extended information and add it to the track if able.
     * @param track
     * @param extinf
     */
    private void processExtInf( Track track, String extinf )
    {
        if ( extinf != null )
        {
            int i = extinf.indexOf(":");
            int j = extinf.indexOf(",");
            if (i > -1 || j > i)
            {
                int seconds = Integer.parseInt(extinf.substring(i + 1, j));
                track.setDurationMillis(seconds * 1000l);
                track.setDuration(Utils.milisToText(track.getDurationMillis()));
                track.setTitle(extinf.substring(j + 1).trim());
            }

        }
    }
}
