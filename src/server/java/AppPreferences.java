import java.util.prefs.Preferences;

public class AppPreferences {
    private static Preferences prefs = Preferences.userRoot().node(AppPreferences.class.getName());

    /* 設定の取得メソッド*/
    public static String getString(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }

    /* 設定の保存メソッド*/
    public static void putString(String key, String value) {
        prefs.put(key, value);
    }

    public static int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        prefs.putInt(key, value);
    }
    
    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, Boolean value) {
        prefs.putBoolean(key, value);
    }
}