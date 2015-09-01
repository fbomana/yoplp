package es.ait.yoplp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

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
        View view = convertView;
        if (view == null) // Comprobamos si estamos creando una vista nueva o reutilizando una que ya hay
        {
            LayoutInflater inflador = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflador.inflate(R.layout.playlist_item, parent, false);
        }

        ((TextView)view.findViewById( R.id.plItemNumber )).setText( String.format( "%1$4s", position + 1));
        ((TextView)view.findViewById( R.id.plItemFileName )).setText( PlayListManager.getInstance().get( position ).getName());
        ((TextView)view.findViewById( R.id.plItemLength )).setText("*****");

        return view;
    }
}
