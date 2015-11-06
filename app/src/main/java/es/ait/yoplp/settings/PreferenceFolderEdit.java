package es.ait.yoplp.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import java.io.File;
import java.util.List;

import es.ait.yoplp.fileChooser.FileChooserActivity;
import es.ait.yoplp.fileChooser.FileComparator;
import es.ait.yoplp.fileChooser.FileProccessor;

/**
 * Created by aitkiar on 6/11/15.
 */
public class PreferenceFolderEdit extends EditTextPreference
{
    public Activity activity;
    protected static PreferenceFolderEdit instance;
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
