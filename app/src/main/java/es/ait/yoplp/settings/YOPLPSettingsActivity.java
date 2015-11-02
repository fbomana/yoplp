package es.ait.yoplp.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import es.ait.yoplp.R;

/**
 * Actividad que contiene el menú con las preferencias.
 *
 * TO-DO: cambiarla para que use fragments y evitar el probrlema con el método depricated.s
 */
public class YOPLPSettingsActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
