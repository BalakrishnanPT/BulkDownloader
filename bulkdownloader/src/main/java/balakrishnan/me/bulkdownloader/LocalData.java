package balakrishnan.me.bulkdownloader;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * A Class created to use the shared preference related functions
 * Created by Jaison.
 */

public class LocalData
{
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public LocalData(Context context)
    {
        this.appSharedPrefs = context.getSharedPreferences("ImageDownloader", Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    /**
     * A method to clear the all the values from the shared preference
     */
    public void resetAll() {
        prefsEditor.clear();
        prefsEditor.commit();

    }

    // Get & Set String values

    /**
     * A method to get the String value from the shared preference
     *
     * @param keyName a keyName of the preference value
     * @return it returns the String value of the keyName
     */
    public String getStringPreferenceValue(String keyName) {
        return appSharedPrefs.getString(keyName, "");
    }

    /**
     * A method to store the string value in the shared preference
     * @param keyName a keyName of the preference value
     * @param setValue a value of the preference value
     */
    public void setStringPreferenceValue(String keyName, String setValue) {
        prefsEditor.putString(keyName, setValue);
        prefsEditor.commit();
    }

    // Get & Set Int values

    /**
     * A method to get the int value from the shared preference
     * @param keyName a keyName of the preference value
     * @return it returns the int value of the keyName
     */
    public int getIntegerPreferenceValue(String keyName) {
        return appSharedPrefs.getInt(keyName, 0);
    }

    /**
     * A method to store the int value in the shared preference
     * @param keyName a keyName of the preference value
     * @param setValue a value of the preference value
     */
    public void setIntegerPreferenceValue(String keyName, int setValue) {
        prefsEditor.putInt(keyName, setValue);
        prefsEditor.commit();
    }

    // Get & Set Boolean values

    /**
     * A method to get the boolean value from the shared preference
     * @param keyName a keyName of the preference value
     * @return it returns the boolean value of the keyName
     */
    public boolean getBooleanPreferenceValue(String keyName) {
        return appSharedPrefs.getBoolean(keyName, false);
    }

    /**
     * A method to store the boolean value in the shared preference
     * @param keyName a keyName of the preference value
     * @param setValue a value of the preference value
     */
    public void setBooleanPreferenceValue(String keyName, boolean setValue) {
        prefsEditor.putBoolean(keyName, setValue);
        prefsEditor.commit();
    }

    public boolean delete(String name) {
        return prefsEditor.remove(name).commit();

    }
}
