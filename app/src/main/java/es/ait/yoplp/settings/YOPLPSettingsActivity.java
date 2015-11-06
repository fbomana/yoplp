package es.ait.yoplp.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import es.ait.yoplp.MediaPlayerAdapter;
import es.ait.yoplp.R;
import es.ait.yoplp.playlist.PlayListManager;

/**
 * Actividad que contiene el menú con las preferencias. Usamos una actividad normal y no una
 * PreferenceActivity para que resulte más fácil usar el filechooser.
 *
 * The check for remember time it's only enabled when the check for autoplay is checked. Otherwise it
 * has no sense.
 *
 * prefAutoplay
 * prefRememberTime
 *
 */
public class YOPLPSettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener
{
    /**
     * Inflate the layout and asign the initial values and listeners
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( this );

        CheckBox prefAutoplayCheck = ( CheckBox ) findViewById( R.id.prefAutoplayCheck );
        prefAutoplayCheck.setOnCheckedChangeListener( this );
        prefAutoplayCheck.setChecked( sharedPref.getBoolean( "prefAutoplay", false ));

        CheckBox prefRememberTimeCheck = ( CheckBox ) findViewById( R.id.prefRememberTimeCheck );
        prefRememberTimeCheck.setOnCheckedChangeListener( this );
        prefRememberTimeCheck.setChecked(sharedPref.getBoolean("prefRememberTime", false));
        prefRememberTimeCheck.setEnabled( sharedPref.getBoolean( "prefAutoplay", false ));
    }

    /**
     * Checks for changes in the controls and saves theme on the preferences.
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch ( buttonView.getId() )
        {
            case R.id.prefAutoplayCheck:
            {
                editor.putBoolean("prefAutoplay", isChecked);
                findViewById( R.id.prefRememberTimeCheck ).setEnabled( isChecked );
                break;
            }
            case R.id.prefRememberTimeCheck:
            {
                editor.putBoolean("prefRememberTime", isChecked);
                break;
            }
        }
        editor.commit();
    }
}
