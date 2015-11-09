package es.ait.yoplp.playlist;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;

import es.ait.yoplp.R;
import es.ait.yoplp.Utils;
import es.ait.yoplp.m3u.M3UWriter;

/**
 * Dialog to select the name to save the playlist
 */
public class SavePlayListDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder = builder.setView(getActivity().getLayoutInflater().inflate(R.layout.save_playlist_dialog, null));
        builder = builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try
                {
                    EditText text = ( EditText )SavePlayListDialog.this.getDialog().findViewById(R.id.pldFileNameEditText );
                    if (text.getText() != null && !"".equals(text.getText().toString().trim()))
                    {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( SavePlayListDialog.this.getActivity());
                        String path = preferences.getString("prefDefaultM3UFolder", "" );
                        if ( !path.equals(""))
                        {
                            path += System.getProperty("file.separator");
                        }
                        File file = new File ( path + text.getText() + ".m3u");
                        if ( file.exists())
                        {
                            file.delete();
                        }
                        M3UWriter.getInstance( file ).write(PlayListManager.getInstance());
                    }
                }
                catch ( IOException e )
                {
                    Utils.dumpException(SavePlayListDialog.this.getActivity(), e);;
                    Log.e("[YOPLP", "Error al escribir la playlist", e);
                }
                catch ( Throwable  e )
                {
                    Utils.dumpException( SavePlayListDialog.this.getActivity(), e );;
                    Log.e("[YOPLP", "Error al escribir la playlist", e);
                    throw e;
                }
                SavePlayListDialog.this.getDialog().dismiss();
            }
        });
        builder = builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                SavePlayListDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
