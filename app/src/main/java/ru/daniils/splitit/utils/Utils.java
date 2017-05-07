package ru.daniils.splitit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Utils {
    private static String pUniqueId = null;

    //Получает уникальный для каждого устройства идентификатор
    public static String getUniqueID(Context context) {
        if (pUniqueId == null)
            pUniqueId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return pUniqueId;
    }

    //Проверяет наличие соединения с сетью интернет
    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    public static String strRepeat(String str, int count) {
        return new String(new char[count]).replace("\0", str);
    }

    public static String MD5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return "NULL";
    }

    public static void toast(Context context, Object o) {
        Toast.makeText(context, o.toString(), Toast.LENGTH_LONG).show();
    }
}