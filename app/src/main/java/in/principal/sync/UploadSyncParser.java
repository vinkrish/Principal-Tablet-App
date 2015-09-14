package in.principal.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Created by vinkrish.
 */

public class UploadSyncParser {
    private static InputStream is = null;
    private static JSONObject jObj = null;
    private static String json = "";
    private static boolean flag;

    public static JSONObject makePostRequest(String urlUlAnswered, JSONObject jsonn) throws ConnectException {
        Log.d("req", jsonn + "");
        try {
            flag = false;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(urlUlAnswered);
            StringEntity se = new StringEntity(jsonn.toString());
            httpPostRequest.setEntity(se);
            httpPostRequest.setHeader("Accept", "application/json");
            httpPostRequest.setHeader("content-type", "application/json");
            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.d("Received data", json + "");
            jObj = new JSONObject(json);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            flag = true;
        } catch (JSONException e) {
            e.printStackTrace();
            flag = true;
        } finally {
            if (flag) {
                try {
                    jObj = new JSONObject();
                    jObj.put("success", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jObj;
    }

}
