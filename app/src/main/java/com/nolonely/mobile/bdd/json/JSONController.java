package com.nolonely.mobile.bdd.json;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nolonely.mobile.listeners.JSONArrayListener;
import com.nolonely.mobile.listeners.JSONObjectListener;
import com.nolonely.mobile.util.CryptoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JSONController {

    private static Gson gson = new Gson();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getJsonArrayFromUrl(@NonNull String url, @NonNull Context context,
                                           @NonNull JSONObject parameters, final JSONArrayListener listener) {
        Volley.newRequestQueue(context)
                .add(new JsonRequest<JSONArray>(Request.Method.POST,
                             url,
                             parameters.toString(),
                             jsonArray -> {
                                 if (listener != null) {
                                     try {
                                         listener.onJSONReceived(CryptoUtils.decryptArray(jsonArray));
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }, volleyError -> {
                         if (listener != null) {
                             listener.onJSONReceivedError(volleyError);
                             System.err.println("Params : " + parameters.toString());
                         }
                     }) {
                         @Override
                         protected Map<String, String> getParams() throws AuthFailureError {
                             return super.getParams();
                         }

                         @Override
                         protected Response<JSONArray> parseNetworkResponse(NetworkResponse networkResponse) {
                             try {
                                 String jsonString = new String(networkResponse.data,
                                         HttpHeaderParser
                                                 .parseCharset(networkResponse.headers));
                                 return Response.success(new JSONArray(jsonString),
                                         HttpHeaderParser
                                                 .parseCacheHeaders(networkResponse));
                             } catch (UnsupportedEncodingException e) {
                                 return Response.error(new ParseError(e));
                             } catch (JSONException je) {
                                 return Response.error(new ParseError(je));
                             }
                         }
                     }
                ).setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getJsonObjectFromUrl(@NonNull String url, @NonNull Context context,
                                            @NonNull JSONObject parameters, final JSONObjectListener listener) {
        Volley.newRequestQueue(context)
                .add(new JsonRequest<JSONObject>(Request.Method.POST,
                             url,
                             parameters.toString(),
                             jsonObject -> {
                                 if (listener != null) {
                                     listener.onJSONReceived(CryptoUtils.decryptObject(jsonObject));
                                 }
                             }, volleyError -> {
                         if (listener != null) {
                             listener.onJSONReceivedError(volleyError);
                             System.err.println("Params : " + parameters.toString());
                         }
                     }) {
                         @Override
                         protected Map<String, String> getParams() throws AuthFailureError {
                             return super.getParams();
                         }

                         @Override
                         protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
                             try {
                                 String jsonString = new String(networkResponse.data,
                                         HttpHeaderParser
                                                 .parseCharset(networkResponse.headers));
                                 return Response.success(new JSONObject(jsonString),
                                         HttpHeaderParser
                                                 .parseCacheHeaders(networkResponse));
                             } catch (UnsupportedEncodingException e) {
                                 return Response.error(new ParseError(e));
                             } catch (JSONException je) {
                                 return Response.error(new ParseError(je));
                             }
                         }
                     }
                ).setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ;
    }

    public static Object convertJSONToObject(JSONObject s, Class c) {
        return gson.fromJson(s.toString(), c);
    }
}
