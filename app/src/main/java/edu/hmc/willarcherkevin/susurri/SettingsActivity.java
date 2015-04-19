package edu.hmc.willarcherkevin.susurri;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference("example_text"));
        bindPreferenceSummaryToValue(findPreference("avatar_list"));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    // TODO working on limiting the number of name changes
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            ParseUser currentUser = ParseUser.getCurrentUser();

            // cannot just call the makeText sentence one and have the other stuff be variables
            // might be due to us being inside a literal function
            Toast toast;
            CharSequence text;
            int duration = Toast.LENGTH_SHORT;
            Context context = preference.getContext();

            if (preference instanceof ListPreference) {
                text = "Your avatar preferences have been updated!";

                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // also there was code duplication in the same function
                if ((preference.getKey()).toString().equals("avatar_list")) {
                    currentUser.put("avatar", listPreference.getEntries()[index]);
                    currentUser.saveInBackground();
                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                }


                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                // right now it's a little sloppy the way I handle whether the screen name
                // category is being changed
                if ((preference.getKey()).toString().equals("example_text")) {
                    int SNC = (int) ParseUser.getCurrentUser().get("SNC");
                    if (SNC == 0) {
                        text = "You have no more name changes left";
                        toast = Toast.makeText(context, text, duration);
                        toast.show();
                        return true;
                    } else {
                        SNC--;
                        text = "Your screen name has changed. You have " + String.valueOf(SNC)
                                + " changes left to your screen name.";
                        currentUser.put("screenName", stringValue);
                        currentUser.put("SNC", SNC);
                        currentUser.saveInBackground();
                        toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
                preference.setSummary(stringValue);

            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);


        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

    }
}
