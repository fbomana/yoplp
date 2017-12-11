package es.ait.yoplp.fileChooser;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.ait.yoplp.R;

/**
 * Adapter that permits to pass the contents of a directory to a ListView. It sorts the elements of
 * the list putting folder first than files and sorting them in lexicographical order.
 */
class FolderAdapter extends ArrayAdapter
{

    private final FileChooserActivityConfiguration configuration;
    private View oldView;

    private List<File> selectedList;

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    public FolderAdapter( FileChooserActivityConfiguration configuration, Context context, int resource )
    {
        super( context, resource, folderToList( configuration.getInitialFolder(), configuration ));

        this.configuration = configuration;
        sort( configuration.getFileComparator() );
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    public FolderAdapter( FileChooserActivityConfiguration configuration, Context context, int resource, File folder )
    {
        super( context, resource, folderToList( folder, configuration ));

        this.configuration = configuration;
        sort( configuration.getFileComparator() );
    }

    @SuppressWarnings("unchecked")
    public FolderAdapter( FileChooserActivityConfiguration configuration, Context context, int resource, String folder)
    {
        super( context, resource,folderToList( new File( folder ), configuration));

        this.configuration = configuration;
        sort( configuration.getFileComparator() );
    }

    @SuppressWarnings("unchecked")
    public FolderAdapter( FileChooserActivityConfiguration configuration, Context context, int resource, List<File> files )
    {
        super( context, resource, files );

        this.configuration = configuration;
        sort( configuration.getFileComparator());
    }

    /**
     * This method it's necesary to avoid that the inner list of the ArrayAdapter behaves as an AbstractList
     * that not have the mothods clear or add.
     *
     * @param folder
     * @return
     */
    private static List<File> folderToList( File folder, FileChooserActivityConfiguration configuration )
    {
        List<File> result = new ArrayList<>();
        if ( folder != null )
        {
            File[] files = folder.listFiles( configuration.getFileFilter());

            Collections.addAll(result, files);
        }
        else
        {
            result.add( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_MUSIC));
            result.add( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_RINGTONES));
            result.add( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PODCASTS));
            if ( Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState()))
            {
                result.add(Environment.getExternalStorageDirectory());
                File[] aux = new File( "/storage").listFiles();
                for ( File file : aux )
                {
                    // Search for all mounted media ( sd-crads, usb, ... )
                    if ( file.canRead() && !file.getName().equals( "emulated") && !file.getName().equals("self"))
                    {
                        result.add( file );
                    }
                    
                }
                
                aux = new File("/mnt").listFiles();

                for (File anAux : aux)
                {
                    if (anAux.getName().startsWith("sdcard") )
                    {
                        result.add(anAux);
                    }
                }

            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public void navigateTo( File folder )
    {
        if ( folder.isDirectory() && folder.canRead())
        {
            this.clear();
            this.notifyDataSetChanged();
            File[] ficheros = folder.listFiles(new MusicFileFilter());
            if ( ficheros != null )
            {
                Arrays.sort( ficheros, configuration.getFileComparator());
            }
            for ( int i = 0; ficheros != null && i < ficheros.length; i ++ )
            {
                this.add( ficheros[i]);
            }
            this.notifyDataSetChanged();
            if ( selectedList != null )
            {
                this.selectedList.clear();
            }
            this.oldView = null;
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
            if ( !configuration.isMultiSelect())
            {
                if ( oldView != null )
                {
                    setSelected(oldView, false);
                }
                selectedList.clear();
                oldView = view;
            }
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

    List<File>getSelectedList()
    {
        if ( selectedList == null )
        {
            selectedList = new ArrayList<>();
        }
        return selectedList;
    }

    private void setSelectedList( List<File> selectedList )
    {
        this.selectedList = selectedList;
    }
}
