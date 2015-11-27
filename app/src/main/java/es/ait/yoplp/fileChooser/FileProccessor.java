package es.ait.yoplp.fileChooser;

import java.io.File;
import java.util.List;

/**
 * Created by aitkiar on 6/11/15.
 */
public interface FileProccessor
{
    void process( List<File> selectedFiles );
}
