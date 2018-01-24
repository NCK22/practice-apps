package com.innovation.neha.tracklocation.Storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 29/06/2017.
 */

public class SPrefUserInfo {

    public SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private static final String SPrefWaiterInfo = "SPrefWaiterInfo";
    private static final String user_id = "";
    private static final String visit_stat="no";

    public SPrefUserInfo(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(SPrefWaiterInfo, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.commit();
    }

    public String getUserInfo() {
        return preferences.getString(user_id, "");
    }

    public void setUserInfo(String user_idi) {
        editor.putString(user_id, user_idi);
        editor.commit();
        editor.apply();
    }

    public String getVisitInfo() {
        return preferences.getString(visit_stat, "");
    }

    public void setVisitInfo(String visit_stati)
    {
        editor.putString(visit_stat,visit_stati);
        editor.commit();
        editor.apply();
    }

    public void clearSPrefWaiterInfo(){
        editor.clear();
        editor.commit();
    }







}
