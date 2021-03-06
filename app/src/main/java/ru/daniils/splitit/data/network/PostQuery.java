package ru.daniils.splitit.data.network;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.daniils.splitit.utils.Callback;
import ru.daniils.splitit.utils.Utils;

public class PostQuery extends AsyncTask<Void, Void, Void> implements Callback.ISuccessError {
    private final String mMethod;
    private final String mParam;
    private final Context mContext;
    protected JSONObject mResponse = new JSONObject();
    protected boolean mIsError = false;
    protected int mErrorCode = 0;
    protected String mErrorMessage = "";
    protected HashMap<String, Object> mJsonObj;
    private Callback.ISuccessError mCallback = null;

    public PostQuery(Context context, String method) {
        mContext = context;
        mMethod = method;
        mParam = "";
        mJsonObj = new HashMap<>();
    }

    public PostQuery(Context context, String method, String param) {
        mContext = context;
        mMethod = method;
        mParam = param;
        mJsonObj = new HashMap<>();
    }


    public final void put(String a, String b) {
        mJsonObj.put(a, b);
    }

    public final void put(String a, Map b) {
        mJsonObj.put(a, new JSONObject(b));
    }

    public final void put(String a, List b) {
        mJsonObj.put(a, new JSONArray(b));
    }

    @Override
    protected final Void doInBackground(Void... voids) {
        String result;
        if (!Utils.hasConnection(mContext))
            result = "{\"error\":{\"error_code\":-1,\"error_msg\":\"Connection error: No signal\"}}";
        else
            result = sendPOST("https://splitit.babashnik.tech/" + mMethod + "/" + mParam, mJsonObj);

        parseResult(result);
        return null;
    }

    @Override
    protected final void onPostExecute(Void aVoid) {
        if (mIsError) {
            onError();
            if (mCallback != null)
                mCallback.onError();
        } else {
            onSuccess();
            if (mCallback != null)
                mCallback.onSuccess();
        }
    }

    public void onError() {
    }

    public void onSuccess() {
    }

    public final void setCallback(Callback.ISuccessError callback) {
        mCallback = callback;
    }


    private String sendPOST(String url, HashMap<String, Object> params) {
        String conerr;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            con.setDoOutput(true);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                try {
                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {
                }
            }
            OutputStream os = con.getOutputStream();
            os.write(sb.toString().getBytes());
            os.flush();

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder resp = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    resp.append(inputLine);
                }
                con.disconnect();
                String ret = resp.toString();
                ret = ret.replace(":null", ":\"\"");
                return ret;
            }
            con.disconnect();
            conerr = responseCode + "";
        } catch (IOException e) {
            conerr = e.getMessage();
        }
        return "{\"error\":{\"error_code\":-2,\"error_msg\":\"Connection error:" + conerr + "\"}}";
    }

    private void parseResult(String result) {
        try {
            mResponse = new JSONObject(result);
            if (mResponse.opt("error") != null) {
                mIsError = true;
                mResponse = mResponse.getJSONObject("error");
                mErrorCode = mResponse.getInt("error_code");
                mErrorMessage = mResponse.getString("error_msg");
            } else {
                mResponse = mResponse.getJSONObject("response");
            }
        } catch (JSONException ignored) {
        }
    }
}