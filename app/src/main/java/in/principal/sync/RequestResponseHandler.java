package in.principal.sync;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vinkrish on 30/09/15.
 */
public class RequestResponseHandler {
    public static String reachServer(String urlString, JSONObject json) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String response = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(8000);
            urlConnection.setReadTimeout(20000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            // OutputStream os = httpCon.getOutputStream();
            // OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
            osw.write(json.toString());
            osw.flush();
            osw.close();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                response = convertInputStreamToString(inputStream);
            } else {
                response = "";
            }

        } catch (Exception e) {
            Log.d("RequestResponseHandler", e.getLocalizedMessage());
        }
        return response;
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line + "\n");
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return sb.toString();
    }
}
