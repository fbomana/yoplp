package es.ait.yoplp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import es.ait.yoplp.playlist.PlayListManager;
import es.ait.yoplp.playlist.Track;

/**
 * Adapter for the ListView of the play list
 */
public class PlayListAdapter extends ArrayAdapter<File>
{
    public PlayListAdapter(Context context, int resource, List<File> objects)
    {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        PlayListManager<Track> plm =  PlayListManager.getInstance();
        View view = convertView;
        if (view == null) // Comprobamos si estamos creando una vista nueva o reutilizando una que ya hay
        {
            LayoutInflater inflador = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflador.inflate(R.layout.playlist_item, parent, false);
        }

        ((TextView)view.findViewById( R.id.plItemNumber )).setText( String.format("%1$4s", position + 1));
        ((TextView)view.findViewById( R.id.plItemFileName )).setText(plm.get( position ).getFile().getName());
        ((TextView)view.findViewById( R.id.plItemLength )).setText( plm.get( position ).getDuration());
        if ( plm.get( position ).isPlaying())
        {
            view.setBackgroundResource( R.drawable.playlist_item_selected );
        }
        else
        {
            view.setBackgroundResource( R.drawable.playlist_item );
        }

        return view;
    }
}
