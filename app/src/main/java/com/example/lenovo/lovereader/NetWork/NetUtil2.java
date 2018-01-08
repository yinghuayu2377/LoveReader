package com.example.lenovo.lovereader.NetWork;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lenovo on 2016/12/5.
 */
public class NetUtil2 {
    public static final int NETWORK_NONE=0;
    public static final int NETWORK_WIFI=1;
    public static final int NETWORK_MOBILE=2;

    public static int getNetworkState(Context context){
        ConnectivityManager connManager=(ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connManager.getActiveNetworkInfo();
        if(networkInfo==null){
            return NETWORK_NONE;  //无网络连接
        }

        int nType=networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_MOBILE){
            return NETWORK_MOBILE;  //移动网络
        }
        else if(nType==ConnectivityManager.TYPE_WIFI){
            return NETWORK_WIFI;  //WIFI
        }
        return NETWORK_NONE;
    }
}
