package es.ait.yoplp.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import es.ait.yoplp.fileChooser.FileChooserActivity;
import es.ait.yoplp.fileChooser.FileComparator;

/**
 * Clase que extiende la clase EditTextPreference para crear un cuadro de texto que al ser clickado lanza
 * la actividad de selección de ficheros.
 */
public class PreferenceFolderEdit extends EditTextPreference
{
    private Activity activity;
    static PreferenceFolderEdit instance;
    private String defaultValue;

    public PreferenceFolderEdit(Context context, AttributeSet attrs)
    {
        super(context, attrs );
    }

    public PreferenceFolderEdit(Context context)
    {
        super(context, null);
    }

    public void setActivity( Activity activity )
    {
        this.activity = activity;
    }

    @Override
    protected void onClick()
    {
        Intent intent = new Intent( activity, FileChooserActivity.class);
        intent.putExtra("FileChooserActivity.fileFilter", FolderFilter.class.getName());
        intent.putExtra("FileChooserActivity.fileComparator", FileComparator.class.getName());
        intent.putExtra("FileChooserActivity.createFolder", true );
        intent.putExtra("FileChooserActivity.multiSelect", false );
        intent.putExtra("FileChooserActivity.fileProccessor", PreferencesFolderProccessor.class.getName());
        if ( defaultValue != null )
        {
            intent.putExtra("FileChooserActivity.initialFolder", defaultValue );
        }

        instance = this;
        activity.startActivity(intent);
    }

    @Override
    public void setDefaultValue(Object defaultValue)
    {
        super.setDefaultValue( defaultValue );
        if ( defaultValue != null && !"".equals( defaultValue.toString()))
        {
            this.defaultValue = defaultValue.toString();
        }
    }

}
