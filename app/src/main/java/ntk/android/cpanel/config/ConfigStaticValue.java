package ntk.android.cpanel.config;

import android.content.Context;

import ntk.android.cpanel.utillity.EasyPreference;


public class ConfigStaticValue {
    public ConfigStaticValue(Context context) {
        privateContext = context;
        ApiBaseAppId = 0;
        ApiBaseUrl = null;
    }

    private Context privateContext;
    public String ApiBaseUrl;

    public void UrlPreferenceUseed() {
        if (privateContext != null) {
            int ApiBaseUrlPreferenceUseed = EasyPreference.with(privateContext).getInt("ApiBaseUrlUseed", 0);
            ApiBaseUrlPreferenceUseed++;
            EasyPreference.with(privateContext).addInt("ApiBaseUrlUseed", ApiBaseUrlPreferenceUseed);
        }
    }

    public String GetApiBaseUrl() {
        if (privateContext != null) {
            String ApiBaseUrlPreference = "";
            int ApiBaseUrlPreferenceUseed = 0;
            ApiBaseUrlPreference = EasyPreference.with(privateContext).getString("ApiBaseUrl", "");
            ApiBaseUrlPreferenceUseed = EasyPreference.with(privateContext).getInt("ApiBaseUrlUseed", 0);
            if (ApiBaseUrlPreference != null && !ApiBaseUrlPreference.isEmpty() && ApiBaseUrlPreferenceUseed < 10) {
                UrlPreferenceUseed();
                return ApiBaseUrlPreference;
            }
        }
        return ApiBaseUrl;
    }

    public int ApiBaseAppId;
}
