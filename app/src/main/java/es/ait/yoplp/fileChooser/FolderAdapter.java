package es.ait.yoplp.fileChooser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ait.yoplp.playlist.PlayListManager;
import es.ait.yoplp.R;
import es.ait.yoplp.playlist.Track;

/**
 * Adapter that permits to pass the contents of a directory to a ListView. It sorts the elements of
 * the list putting folder first than files and sorting them in lexicographical order.
 */
public class FolderAdapter extends ArrayAdapter
{

    private List<File> selectedList;

    public FolderAdapter(Context context, int resource )
    {
        super( context, resource, folderToList( null ));
        sort(new FileComparator());
    }
    public FolderAdapter(Context context, int resource, File folder) throws Exception
    {
        super( context, resource, folderToList(folder));
        sort( new FileComparator());
    }

    public FolderAdapter(Context context, int resource, String folder) throws Exception
    {
        super( context, resource,folderToList( new File( folder )));
        sort( new FileComparator());
    }

    public FolderAdapter(Context context, int resource, List<File> files ) throws Exception
    {
        super( context, resource, files );
        sort( new FileComparator());
    }

    /**
     * This method it's necesary to avoid that the inner list of the ArrayAdapter behaves as an AbstractList
     * that not have the mothods clear or add.
     *
     * @param folder
     * @return
     */
    private static List<File> folderToList( File folder )
    {
        List<File> result = new ArrayList<File>();
        if ( folder != null )
        {
            File[] files = folder.listFiles(new MusicFileFilter());

            for (int i = 0; i < files.length; i++)
            {
                result.add(files[i]);
            }
        }
        return result;
    }

    public void navigateTo( File folder )
    {
        if ( folder.isDirectory() && folder.canRead())
        {
            this.clear();
            this.notifyDataSetChanged();
            File[] ficheros = folder.listFiles(new MusicFileFilter());
            for ( int i = 0; ficheros != null && i < ficheros.length; i ++ )
            {
                this.add( ficheros[i]);
            }
            this.notifyDataSetChanged();
        }
        else
        {
            System.out.println("Can't read file");
        }
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if ( view == null ) // Comprobamos si estamos creando una vista nueva o reutilizando una que ya hay
        {
            LayoutInflater inflador = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflador.inflate(R.layout.fc_list_item, parent, false);
        }

        ImageView icon = (ImageView) view.findViewById( R.id.fcIcon );

        File file = ( File )getItem(position);
        if ( file.isDirectory())
        {
            icon.setImageResource(R.drawable.folder);
        }
        else
        {
            icon.setImageResource(R.drawable.file);
        }


        TextView fileName = (TextView) view.findViewById( R.id.fcFileName);
        fileName.setText(file.getName());

        setSelected( view, getSelectedList().contains(file) );
        return view;
    }

    public void toggleSelected( int position, View view  )
    {
        List<File> selectedList = getSelectedList();

        File file = ( File )getItem(position);
        if ( selectedList.contains( file ))
        {
            selectedList.remove(file);
            setSelected(view, false);
        }
        else
        {
            selectedList.add(file);
            setSelected(view, true);
        }
    }

    /**
     * Changes a view between selected and !selected state
     * @param view
     * @param selected
     */
    private void setSelected( View view, boolean selected )
    {
        if ( selected )
        {
            view.setSelected(true);
            view.setPressed(true);
            ImageView icon = ( ImageView)view.findViewById(R.id.fcSelectedIcon);
            icon.setImageResource(R.drawable.check_icon);
        }
        else
        {
            view.setSelected(false);
            view.setPressed(false);
            ImageView icon = ( ImageView)view.findViewById(R.id.fcSelectedIcon);
            icon.setImageDrawable(null);
        }
    }

    private List<File>getSelectedList()
    {
        if ( selectedList == null )
        {
            selectedList = new ArrayList<File>();
        }
        return selectedList;
    }

    private void setSelectedList( List<File> selectedList )
    {
        this.selectedList = selectedList;
    }

    /**
     * Loads all selected files into the general playlist.
     * If selected file is a folder, it adds all music files found in that folder in a recursive fashion
     */
    public void loadFiles()
    {
        for ( int i = 0; i < selectedList.size(); i ++ )
        {
            loadFiles( selectedList.get( i ));
        }
    }

    /**
     * Recursive algortihm. It can create a potentially very large number of arrays, so it must be
     * optimized sometime
     *
     * @param file
     */
    private void loadFiles( File file )
    {
        if ( file.canRead())
        {
            if (file.isDirectory())
            {
                File files[] = file.listFiles( new MusicFileFilter());
                Arrays.sort(files, new FileComparator());
                for ( int i = 0; i < files.length; i ++ )
                {
                    if ( files[i].isDirectory())
                    {
                        loadFiles( files[i] );
                    }
                    else // Avoid extra recursive calls that generate overhead
                    {
                        PlayListManager.getInstance().add(new Track( files[i]));
                    }
                }
            }
            else
            {
                PlayListManager.getInstance().add( new Track ( file ));
            }
        }
    }
}
