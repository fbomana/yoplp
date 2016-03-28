package es.ait.yoplp.fileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interfaz para las clases que processan los ficheros seleccionados mediante FileChooser.
 */
public interface FileProccessor
{
    void process( List<File> selectedFiles ) throws IOException;
}
