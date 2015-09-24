/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ait.yoplp.m3u;

import es.ait.yoplp.playlist.PlayList;
import es.ait.yoplp.playlist.Track;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class manage writing playlists as m3u files to disk. The tracks are saved as an absolute path.
 */
public class M3UWriter
{
    private File m3uFile;

    private M3UWriter( File m3uFile )
    {
        this.m3uFile = m3uFile;
    }

    /**
     * Return an instance of a writer configured to write a PlayList object as an
     * m3u file. If the file already exists, the method getInstance remove it in
     * order to have everything prepared.
     *
     * @param m3uFile
     * @return
     * @throws IOException
     */
    public static M3UWriter getInstance( File m3uFile ) throws IOException
    {
        if ( m3uFile == null || ( m3uFile.exists() && !m3uFile.canWrite()))
        {
            throw new IOException("File is null or can't read.");
        }

        if ( m3uFile.isDirectory())
        {
            throw new IOException("Supplied argument it's not an m3u file");
        }

        if ( !m3uFile.isAbsolute() && m3uFile.getAbsolutePath() == null )
        {
            throw new IOException ("Supplied argument must have an absolute path.");
        }

        if ( m3uFile.exists())
        {
            m3uFile.delete();
        }

        return new M3UWriter( m3uFile );
    }

    /**
     * Writes the play list to the file supplied to the getInstance method
     * @param playlist
     * @throws Exception
     */
    public void write( PlayList<Track> playlist ) throws Exception
    {
        if ( playlist == null || playlist.isEmpty())
        {
            throw new Exception( "Empty o null playlist");
        }

        FileWriter fout = new FileWriter( m3uFile, false );
        fout.write("#EXTM3U\n");

        for ( int i = 0; i < playlist.size(); i++ )
        {
            fout.write( playlist.get(i).toM3U());
        }

        fout.close();
    }
}
