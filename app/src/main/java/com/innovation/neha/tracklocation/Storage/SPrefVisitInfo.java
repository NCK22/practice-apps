package com.innovation.neha.tracklocation.Storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 29/06/2017.
 */

public class SPrefVisitInfo {

    public SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private static final String SPrefVisitInfo = "SPrefVisitInfo";

    private static final String visit_stat="no";
    private static final String start_time_info="";
    private static final String visit_id="";

    public SPrefVisitInfo(Context context) {
        this.context = context;
        preferences = this.context.getSharedPreferences(SPrefVisitInfo, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.commit();
    }


    public void setStartTimeInfo(String start_time)
    {
        editor.putString(start_time_info,start_time);
        editor.commit();
        editor.apply();
    }

    public String getStartTimeInfo(){ return preferences.getString(start_time_info,"");}

    public String getVisitInfo() {
        return preferences.getString(visit_stat, "");
    }

    public void setVisitInfo(String visit_stati)
    {
        editor.putString(visit_stat,visit_stati);
        editor.commit();
        editor.apply();
    }

    public String getVisitId() {
        return preferences.getString(visit_id, "");
    }

    public void setVisitId(String visit_idi)
    {
        editor.putString(visit_id,visit_idi);
        editor.commit();
        editor.apply();
    }

    public void SPrefVisitInfo(){
        editor.clear();
        editor.commit();
    }







}
