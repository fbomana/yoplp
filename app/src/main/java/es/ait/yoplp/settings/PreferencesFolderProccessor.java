package es.ait.yoplp.settings;

import java.io.File;
import java.util.List;

import es.ait.yoplp.fileChooser.FileProccessor;

/**
 * Clase que procesa el resultado de llamar a la actividad de selección de ficheros para un campo de preferencias.
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
