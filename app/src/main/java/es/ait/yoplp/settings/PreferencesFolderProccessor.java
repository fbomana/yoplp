package es.ait.yoplp.settings;

import java.io.File;
import java.util.List;

import es.ait.yoplp.fileChooser.FileProccessor;

/**
 * Created by aitkiar on 6/11/15.
 */
public class PreferencesFolderProccessor implements FileProccessor
{
    @Override
    public void process(List<File> selectedFiles)
    {
        if ( selectedFiles != null && !selectedFiles.isEmpty() && PreferenceFolderEdit.instance != null )
        {
            PreferenceFolderEdit.instance.setText(selectedFiles.get(0).getAbsolutePath());
            PreferenceFolderEdit.instance.setDefaultValue( selectedFiles.get(0).getAbsolutePath() );
        }
    }
}
