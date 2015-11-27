package es.ait.yoplp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aitkiar on 7/09/15.
 */
public class Utils
{
    public static String milisToText ( long milis )
    {
        long seconds = milis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;

        if ( hours > 0 )
        {
            return String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
        }
        else
        {
            return String.format("%1$02d:%2$02d", minutes, seconds);
        }

    }

    /**
     * Dumps an exception to a file in the app directory so we can trace erros happened during the app use.
     * @param context
     * @param t
     */
    public static void dumpException( Context context, Throwable t )
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(context.getFilesDir().getAbsolutePath() + "/exceptionLog.txt", true);
            fos.write(("Dumping error produced at:" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date()) + "\n").getBytes("utf-8"));
            t.printStackTrace(new PrintStream(fos));
            fos.close();
        }
        catch ( Exception ex )
        {
            Log.e("[YOPLP]", "Error dumping exception trace", ex );
        }
    }

    /**
     * Gets the las .separated suffix of a file name in lowercase
     * @param file
     * @return
     */
    public static String getExtension( File file )
    {
        String name = file.getName().toLowerCase();
        if ( name.lastIndexOf(".") != -1 )
        {
            return name.substring( name.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static boolean isMyServiceRunning( Context context, String className )
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for ( ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (className.equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
}
