package es.ait.yoplp.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import es.ait.yoplp.R;

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
public class YOPLPSettingsActivity extends PreferenceActivity
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
        //noinspection deprecation
        addPreferencesFromResource(R.xml.preferences);

        @SuppressWarnings("deprecation") PreferenceFolderEdit text = ( PreferenceFolderEdit ) findPreference("prefDefaultM3UFolder");
        text.setActivity(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if ( !sharedPref.getString("prefDefaultM3UFolder", "").equals( "" ))
        {
            text.setDefaultValue( sharedPref.getString("prefDefaultM3UFolder", "") );
        }
    }
}
