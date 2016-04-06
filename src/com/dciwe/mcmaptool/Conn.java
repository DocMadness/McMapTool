/*
 * Copyright (c) 2015-2016. DCIWE.com
 */

package com.dciwe.mcmaptool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Conn {
    private static List<String> cookies = new ArrayList<>();
    private static CookieManager manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
    static {CookieHandler.setDefault(manager);}

    private HttpURLConnection conn;
    private boolean ok = false;
    public String doc = "";

    public Conn(String url, String request) {
        boolean b_request = !(request == null || request.isEmpty());
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            if (b_request) conn.setDoOutput(true);
            if (b_request) conn.setRequestMethod("POST");
            else conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "McMapTool/A1.4 (Agent)"); //браузер-агент
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            conn.setRequestProperty("Pragma", "no-cache");
            if ((cookies == null || cookies.isEmpty()) && !b_request) {
                cookies = conn.getHeaderFields().get("Set-Cookie");
                manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(manager);
            } else {
                for (String cookie : cookies) {
                    conn.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
                }
            }

            if (b_request) {
                try {
                    OutputStream os = conn.getOutputStream();
                    os.write(request.getBytes());
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) ok = true;
                } catch (IOException ignored) {}
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String textLine;
                    String doc = "";
                    while ((textLine = reader.readLine()) != null) {
                        doc += textLine + "\n";
                    }
                    reader.close();
                    this.doc = doc;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOk() {
        return ok;
    }
}
