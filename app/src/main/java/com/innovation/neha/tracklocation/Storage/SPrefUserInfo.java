package com.innovation.neha.tracklocation.Storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 29/06/2017.
 */

public class SPrefUserInfo {

    public SharedPreferences preferences1,preferences2,preferences3,preferences4;
    private SharedPreferences.Editor editor1,editor2,editor3,editor4;
    private Context context;
    private static final String SPrefUserInfo = "SPrefUserInfo";
    private static final String SPrefVisitInfo = "SPrefVisitInfo";
    private static final String SPrefVisitIdInfo = "SPrefVisitIdInfo";
    private static final String SPrefTimeInfo = "SPrefTimeInfo";
    private static final String user_id = "";
    private static final String visit_stat="no";
    private static final String start_time_info="";
    private static final String visit_id="";

    public SPrefUserInfo(Context context) {
        this.context = context;
        preferences1 = this.context.getSharedPreferences(SPrefUserInfo, Context.MODE_PRIVATE);
        editor1 = preferences1.edit();
        editor1.commit();

        preferences2 = this.context.getSharedPreferences(SPrefVisitInfo, Context.MODE_PRIVATE);
        editor2 = preferences2.edit();
        editor2.commit();

        preferences3 = this.context.getSharedPreferences(SPrefVisitIdInfo, Context.MODE_PRIVATE);
        editor3 = preferences3.edit();
        editor3.commit();

        preferences4 = this.context.getSharedPreferences(SPrefTimeInfo, Context.MODE_PRIVATE);
        editor4 = preferences4.edit();
        editor4.commit();
    }

    public String getUserInfo() {
        return preferences1.getString(user_id, "");
    }
    public void setUserInfo(String user_idi) {
        editor1.putString(user_id, user_idi);
        editor1.commit();
        editor1.apply();
    }


    public String getVisitInfo() {
        return preferences2.getString(visit_stat, "");
    }
    public void setVisitInfo(String visit_stati)
    {
        editor2.putString(visit_stat,visit_stati);
        editor2.commit();
        editor2.apply();
    }

    public String getVisitId() {
        return preferences3.getString(visit_id, "");
    }

    public void setVisitId(String visit_idi)
    {
        editor3.putString(visit_id,visit_idi);
        editor3.commit();
        editor3.apply();
    }


    public void setStartTimeInfo(String start_time)
    {
        editor4.putString(start_time_info,start_time);
        editor4.commit();
        editor4.apply();
    }
    public String getStartTimeInfo(){ return preferences4.getString(start_time_info,"");}




    public void clearSPrefWaiterInfo(){
       /* editor.clear();
        editor.commit();*/
    }







}
