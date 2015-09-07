package es.ait.yoplp;

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
}
