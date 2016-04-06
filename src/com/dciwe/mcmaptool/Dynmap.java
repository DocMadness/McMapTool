/*
 * Copyright (c) 2015-2016. DCIWE.com
 */

package com.dciwe.mcmaptool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Dynmap {
    private static boolean starting = false;
    private static boolean stoped = false;
    private boolean connecting = false;
    public static Dynmap dynmap;

    private Mcmaptool ui;
    public DynmapOptions options;
    boolean nogui = false;
    String initfollow = "";

    public Dynmap(Mcmaptool ui, String url, DynmapOptions options) {
        this.ui = ui;
        try {
            URI uri = new URI(url);
            options.url.server = uri.getScheme() + "://" + uri.getAuthority() + "/";
            Map<String, String> query_pairs = new HashMap<>();
            String query = uri.getQuery();
            try {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                }
            } catch (NullPointerException ignored) {}
            options.url.query_pairs = query_pairs;
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.options = options;
        Conn conn;
        if (options.dynmap_ver_json) {
            conn = new Conn(options.url.server + String.format(options.url.configuration, options.timestamp), null);
            options.timestamp = new Date().getTime();
        }
        else conn = new Conn(options.url.server + options.url.configuration, null);
        if (conn.isOk()) {
            connecting = true;
            configure(conn.doc);
            initialize();
        }
    }
    private void configure(String configuration) {
        try {
            JSONObject o = new JSONObject(configuration);
            if (o.has("maxcount")) options.maxcount = o.getInt("maxcount");
            if (o.has("login-enabled")) options.login_enabled = o.getBoolean("login-enabled");
            if (o.has("msg-hiddennamejoin")) options.msg_hiddennamejoin = o.getString("msg-hiddennamejoin");
            if (o.has("quitmessage")) options.quitmessage = o.getString("quitmessage");
            if (o.has("webchat-requires-login")) options.webchat_requires_login = o.getBoolean("webchat-requires-login");
            if (o.has("confighash")) options.confighash = o.getLong("confighash");
            if (o.has("showlayercontrol")) options.showlayercontrol = o.getString("showlayercontrol");
            if (o.has("title")) options.title = o.getString("title");
            if (o.has("msg-chatnotallowed")) options.msg_chatnotallowed = o.getString("msg-chatnotallowed");
            if (o.has("msg-players")) options.msg_players = o.getString("msg-players");
            if (o.has("msg-showplayerfacesinmenu")) options.showplayerfacesinmenu = o.getBoolean("msg-showplayerfacesinmenu");
            if (o.has("defaultmap")) options.defaultmap_name = o.getString("defaultmap");
            if (o.has("loggedin")) options.loggedin = o.getBoolean("loggedin");
            if (o.has("cyrillic")) options.cyrillic = o.getBoolean("cyrillic");
            if (o.has("chatlengthlimit")) options.chatlengthlimit = o.getInt("chatlengthlimit");
            if (o.has("webprefix")) options.webprefix = o.getString("webprefix");
            if (o.has("coreversion")) options.coreversion = o.getString("coreversion");
            if (o.has("dynmapversion")) options.dynmapversion = o.getString("dynmapversion");
            if (o.has("allowchat")) options.allowchat = o.getBoolean("allowchat");
            if (o.has("updaterate")) options.updaterate = o.getDouble("updaterate");
            if (o.has("sidebaropened")) options.sidebaropened = o.getString("sidebaropened");
            if (o.has("msg-maptypes")) options.msg_maptypes = o.getString("msg-maptypes");
            if (o.has("defaultzoom")) options.defaultzoom = o.getInt("defaultzoom");
            if (o.has("defaultworld")) options.defaultworld_name = o.getString("defaultworld");
            if (o.has("joinmessage")) options.joinmessage = o.getString("joinmessage");
            if (o.has("spammessage")) options.spammessage = o.getString("spammessage");
            if (o.has("grayplayerswhenhidden")) options.grayplayerswhenhidden = o.getBoolean("grayplayerswhenhidden");
            if (o.has("allowwebchat")) options.allowwebchat = o.getBoolean("allowwebchat");
            if (o.has("webchat-interval")) options.webchat_interval = o.getDouble("webchat-interval");
            if (o.has("msg-hiddennamequit")) options.msg_hiddennamequit = o.getString("msg-hiddennamequit");
            if (o.has("msg-chatrequireslogin")) options.msg_chatrequireslogin = o.getString("msg-chatrequireslogin");
            if (o.has("worlds")) {
                JSONArray a = o.getJSONArray("worlds");
                HashMap<String, DynmapOptions.Worlds> worlds = new HashMap<>();
                String defaultworld = "";
                String defaultmap = "";
                boolean defaultworld_have = false;
                boolean defaultmap_have = false;
                for (int i = 0; i < a.length(); i++) {
                    JSONObject o2 = a.getJSONObject(i);
                    DynmapOptions.Worlds world = new DynmapOptions.Worlds();
                    if (o2.has("center")) {
                        JSONObject o3 = o2.getJSONObject("center");
                        double x = 0.0;
                        double y = 0.0;
                        double z = 0.0;
                        if (o3.has("x")) x = o3.getDouble("x");
                        if (o3.has("y")) y = o3.getDouble("y");
                        if (o3.has("z")) z = o3.getDouble("z");
                        world.center = new DynmapOptions.Worlds.Center(x, y, z);
                    }
                    if (o2.has("extrazoomout")) world.extrazoomout = o2.getInt("extrazoomout");
                    if (o2.has("title")) world.title = o2.getString("title");
                    if (o2.has("worldheight")) world.worldheight = o2.getInt("worldheight");
                    if (o2.has("protected")) world._protected = o2.getBoolean("protected");
                    if (o2.has("sealevel")) world.sealevel = o2.getInt("sealevel");
                    if (o2.has("name")) world.name = o2.getString("name");
                    if (i == 0) defaultworld = world.name;
                    if (options.defaultworld_name.equals(world.name)) defaultworld_have = true;
                    if (o2.has("maps")) {
                        JSONArray a2 = o2.getJSONArray("maps");
                        HashMap<String, DynmapOptions.Worlds.Maps> maps = new HashMap<>();
                        for (int j = 0; j < a2.length(); j++) {
                            JSONObject o3 = a2.getJSONObject(j);
                            DynmapOptions.Worlds.Maps map = new DynmapOptions.Worlds.Maps();
                            if (o3.has("nightandday")) map.nightandday = o3.getBoolean("nightandday");
                            if (o3.has("worldtomap")) {
                                JSONArray a3 = o3.getJSONArray("worldtomap");
                                ArrayList<Double> worldtomap = new ArrayList<>();
                                for (int k = 0; k < a3.length(); k++){
                                    worldtomap.add(a3.getDouble(i));
                                }
                                map.worldtomap = worldtomap;
                            }
                            if (o3.has("scale")) map.scale = o3.getInt("scale");
                            if (o3.has("icon")) map.icon = o3.get("icon");
                            if (o3.has("mapzoomin")) map.mapzoomin = o3.getInt("mapzoomin");
                            if (o3.has("shader")) map.shader = o3.getString("shader");
                            if (o3.has("maptoworld")) {
                                JSONArray a3 = o3.getJSONArray("maptoworld");
                                ArrayList<Double> maptoworld = new ArrayList<>();
                                for (int k = 0; k < a3.length(); k++){
                                    maptoworld.add(a3.getDouble(i));
                                }
                                map.maptoworld = maptoworld;
                            }
                            if (o3.has("bigmap")) map.bigmap = o3.getBoolean("bigmap");
                            if (o3.has("inclination")) map.inclination = o3.getInt("inclination");
                            if (o3.has("type")) map.type = o3.getString("type");
                            if (o3.has("backgroundday")) map.backgroundday = o3.get("backgroundday");
                            if (o3.has("backgroundnight")) map.backgroundnight = o3.get("backgroundnight");
                            if (o3.has("mapzoomout")) map.mapzoomout = o3.getInt("mapzoomout");
                            if (o3.has("title")) map.title = o3.getString("title");
                            if (o3.has("protected")) map._protected = o3.getBoolean("protected");
                            if (o3.has("lighting")) map.lighting = o3.getString("lighting");
                            if (o3.has("boostzoom")) map.boostzoom = o3.getInt("boostzoom");
                            if (o3.has("background")) map.background = o3.get("background");
                            if (o3.has("prefix")) map.prefix = o3.getString("prefix");
                            if (o3.has("name")) map.name = o3.getString("name");
                            if (i == 0 && j == 0) defaultmap = map.name;
                            if (options.defaultmap_name.equals(map.name)) defaultmap_have = true;
                            if (o3.has("compassview")) map.compassview = o3.getString("compassview");
                            if (o3.has("perspective")) map.perspective = o3.getString("perspective");
                            if (o3.has("azimuth")) map.azimuth = o3.getInt("azimuth");
                            if (o3.has("image-format")) map.image_format = o3.getString("image-format");
                            maps.put(map.name, map);
                        }
                        worlds.put(world.name, world);
                    }
                }
                options.worlds = worlds;
                if (!defaultworld_have) options.defaultworld_name = defaultworld;
                if (!defaultmap_have) options.defaultmap_name = defaultmap;
            }
            if (o.has("components")) {
                JSONArray a = o.getJSONArray("components");
                HashMap<String/*type*/, DynmapOptions.Components> components = new HashMap<>();
                for (int i = 0; i < a.length(); i++) {
                    JSONObject o2 = a.getJSONObject(i);
                    DynmapOptions.Components component = new DynmapOptions.Components();
                    if (o2.has("type")) component.type = o2.getString("type");
                    if (o2.has("offlinehidebydefault")) component.offlinehidebydefault = o2.getBoolean("offlinehidebydefault");
                    if (o2.has("showspawnbeds")) component.showspawnbeds = o2.getBoolean("showspawnbeds");
                    if (o2.has("spawnicon")) component.spawnicon = o2.getString("spawnicon");
                    if (o2.has("spawnbedminzoom")) component.spawnbedminzoom = o2.getInt("spawnbedminzoom");
                    if (o2.has("offlinelabel")) component.offlinelabel = o2.getString("offlinelabel");
                    if (o2.has("showspawn")) component.showspawn = o2.getBoolean("showspawn");
                    if (o2.has("spawnbedicon")) component.spawnbedicon = o2.getString("spawnbedicon");
                    if (o2.has("showlabel")) component.showlabel = o2.getBoolean("showlabel");
                    if (o2.has("maxofflinetime")) component.maxofflinetime = o2.getInt("maxofflinetime");
                    if (o2.has("spawnbedformat")) component.spawnbedformat = o2.getString("spawnbedformat");
                    if (o2.has("spawnbedhidebydefault")) component.spawnbedhidebydefault = o2.getBoolean("spawnbedhidebydefault");
                    if (o2.has("spawnbedlabel")) component.spawnbedlabel = o2.getString("spawnbedlabel");
                    if (o2.has("default-sign-set")) component.default_sign_set = o2.getString("default-sign-set");
                    if (o2.has("spawnlabel")) component.spawnlabel = o2.getString("spawnlabel");
                    if (o2.has("offlineminzoom")) component.offlineminzoom = o2.getInt("offlineminzoom");
                    if (o2.has("enablesigns")) component.enablesigns = o2.getBoolean("enablesigns");
                    if (o2.has("offlineicon")) component.offlineicon = o2.getString("offlineicon");
                    if (o2.has("showofflineplayers")) component.showofflineplayers = o2.getBoolean("showofflineplayers");
                    if (o2.has("allowurlname")) component.allowurlname = o2.getBoolean("allowurlname");
                    if (o2.has("focuschatballoons")) component.focuschatballoons = o2.getBoolean("focuschatballoons");
                    if (o2.has("messagettl")) component.messagettl = o2.getInt("messagettl");
                    if (o2.has("sendbutton")) component.sendbutton = o2.getBoolean("sendbutton");
                    if (o2.has("scrollback")) component.scrollback = o2.getInt("scrollback");
                    if (o2.has("visiblelines")) component.visiblelines = o2.getInt("visiblelines");
                    if (o2.has("showplayerfaces")) component.showplayerfaces = o2.getBoolean("showplayerfaces");
                    if (o2.has("layerprio")) component.layerprio = o2.getInt("layerprio");
                    if (o2.has("label")) component.label = o2.getString("label");
                    if (o2.has("smallplayerfaces")) component.smallplayerfaces = o2.getBoolean("smallplayerfaces");
                    if (o2.has("showplayerbody")) component.showplayerbody = o2.getBoolean("showplayerbody");
                    if (o2.has("showplayerhealth")) component.showplayerhealth = o2.getBoolean("showplayerhealth");
                    if (o2.has("hidebydefault")) component.hidebydefault = o2.getBoolean("hidebydefault");
                    if (o2.has("showdigitalclock")) component.showdigitalclock = o2.getBoolean("showdigitalclock");
                    if (o2.has("showweather")) component.showweather = o2.getBoolean("showweather");
                    if (o2.has("hidey")) component.hidey = o2.getBoolean("hidey");
                    if (o2.has("show-mcr")) component.show_mcr = o2.getBoolean("show-mcr");
                    components.put(component.type, component);
                }
            }
            if (options.url.query_pairs.containsKey("worldname")) {
                String urlarg = options.url.query_pairs.get("worldname");
                if (!urlarg.isEmpty()) options.defaultworld_name = urlarg;
                options.defaultworld = options.worlds.get(urlarg);
            } else options.defaultworld = options.worlds.get(options.defaultworld_name);
            if (options.url.query_pairs.containsKey("mapname")) {
                String urlarg = options.url.query_pairs.get("mapname");
                if (!urlarg.isEmpty()) options.defaultmap_name = urlarg;
                options.defaultmap = options.defaultworld.maps.get(urlarg);
            } else options.defaultmap = options.defaultworld.maps.get(options.defaultmap_name);
            if (options.url.query_pairs.containsKey("x")) {
                String urlarg = options.url.query_pairs.get("x");
                if (!urlarg.isEmpty()) options.defaultworld.center.x = Integer.parseInt(urlarg);
            }
            if (options.url.query_pairs.containsKey("y")) {
                String urlarg = options.url.query_pairs.get("y");
                if (!urlarg.isEmpty()) options.defaultworld.center.y = Integer.parseInt(urlarg);
            }
            if (options.url.query_pairs.containsKey("z")) {
                String urlarg = options.url.query_pairs.get("z");
                if (!urlarg.isEmpty()) options.defaultworld.center.z = Integer.parseInt(urlarg);
            }
            if (options.url.query_pairs.containsKey("nogui")) {
                String urlarg = options.url.query_pairs.get("nogui");
                if (!urlarg.isEmpty()) nogui = urlarg.equals("true");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initialize() {
        ui.frame.setTitle(options.title);
        if (options.url.query_pairs.containsKey("zoom")) {
            String urlzoom = options.url.query_pairs.get("zoom");
            if (!urlzoom.isEmpty()) options.defaultzoom = Integer.parseInt(urlzoom);
        }
        if (options.url.query_pairs.containsKey("showlayercontrol")) {
            String showlayerctl = options.url.query_pairs.get("showlayercontrol");
            if (!showlayerctl.isEmpty()) options.showlayercontrol = showlayerctl;
        }
        if (options.url.query_pairs.containsKey("playername")) {
            String initfollowplayer = options.url.query_pairs.get("playername");
            if (!initfollowplayer.isEmpty()) initfollow = initfollowplayer;
        }
        if (options.url.query_pairs.containsKey("sidebaropened")) {
            String sidebaropen = options.url.query_pairs.get("sidebaropened");
            if (sidebaropen.equals("false") || sidebaropen.equals("true") || sidebaropen.equals("painned")) {
                options.sidebaropened = sidebaropen;
            }
        }

        ui.frame.setTitle(options.title);
    }

    public boolean isConnecting() {
        return connecting;
    }
    public void stop() {
        starting = false;
    }
    public boolean isStoped() {
        return stoped;
    }
    public void update() {
        Conn conn = new Conn(options.url.server + String.format(options.url.update, options.defaultworld.name, options.timestamp), null);
        if (conn.isOk() && !conn.doc.isEmpty()) {
            try {
                JSONObject o = new JSONObject(conn.doc);
                options.update = new DynmapOptions.Update();
                if (o.has("timestamp")) options.timestamp = options.update.timestamp = o.getLong("timestamp");
                if (o.has("hasStorm")) options.update.hasStorm = o.getBoolean("hasStorm");
                if (o.has("updates")) {
                    JSONArray a = o.getJSONArray("updates");
                    DynmapOptions.Update.Updates update = new DynmapOptions.Update.Updates();
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject o2 = a.getJSONObject(i);
                        if (o2.has("type")) {
                            String s = o2.getString("type");
                            if (s.equals("title")) {
                                update.type = "title";
                                if (o2.has("name")) update.name = o2.getString("name");
                                if (o2.has("timestamp")) update.timestamp = o2.getLong("timestamp");
                            } else if (s.equals("playerjoin")) {
                                update.type = "playerjoin";
                                if (o2.has("playerName")) update.playerName = o2.getString("playerName");
                                if (o2.has("account")) update.account = o2.getString("account");
                                if (o2.has("timestamp")) update.timestamp = o2.getLong("timestamp");
                            } else if (s.equals("chat")) {
                                update.type = "chat";
                                if (o2.has("source")) update.source = o2.getString("source");
                                if (o2.has("playerName")) update.playerName = o2.getString("playerName");
                                if (o2.has("message")) update.message = o2.getString("message");
                                //if (o2.has("account")) update.account = o2.getString("account"); //TODO
                                //if (o2.has("channel")) update.channel = o2.getString("channel"); //TODO
                                if (o2.has("timestamp")) update.timestamp = o2.getLong("timestamp");
                            } else if (s.equals("playerquit")) {
                                update.type = "playerquit";
                                if (o2.has("playerName")) update.playerName = o2.getString("playerName");
                                if (o2.has("account")) update.account = o2.getString("account");
                                if (o2.has("timestamp")) update.timestamp = o2.getLong("timestamp");
                            }
                        }
                        synchronized (options.update.updates) {
                            options.update.updates.add(update);
                        }
                    }
                }
                if (o.has("isThundering")) options.update.isThundering = o.getBoolean("isThundering");
                if (o.has("servertime")) options.update.servertime = o.getLong("servertime");
                if (o.has("players")) {
                    JSONArray a = o.getJSONArray("players");
                    for (int i = 0; i < a.length(); i++) {
                        DynmapOptions.Update.Players player = new DynmapOptions.Update.Players();
                        JSONObject o2 = a.getJSONObject(i);
                        if (o2.has("sort")) player.sort = o2.getInt("sort");
                        if (o2.has("name")) player.name = o2.getString("name");
                        if (o2.has("armor")) player.armor = o2.getInt("armor");
                        if (o2.has("account")) player.account = o2.getString("account");
                        if (o2.has("health")) player.health = o2.getDouble("health");
                        if (o2.has("type")) player.type = o2.getString("type");
                        if (o2.has("x")) player.x = o2.getDouble("x");
                        if (o2.has("y")) player.y = o2.getDouble("y");
                        if (o2.has("z")) player.z = o2.getDouble("z");
                        if (o2.has("world")) player.world = o2.getString("world");
                        synchronized (options.update.players) {
                            options.update.players.add(player);
                        }
                    }
                }
                if (o.has("currentcount")) options.update.currentcount = o.getInt("currentcount");
                if (o.has("confighash")) options.update.confighash = o.getLong("confighash");
                process();
                toUI();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void process() {

    }
    private void toUI() {
        ui.content.center.west.online.count.setText(options.update.currentcount + " / " + options.maxcount);

        long time = options.update.servertime;
        int h = (int)(time / 1000 + 6) % 24;
        int m = (int)(time / 1000d % 1d * 60);
        ui.content.center.west.online.serverTime.setText(String.format("%02d:%02d", h, m));

        ui.spawn_x = (int) options.defaultworld.center.x;
        ui.spawn_y = (int) options.defaultworld.center.z;

        ui.dynmap = this;

        ui.content.center.main.action.repaint();
    }

    public static boolean start(Mcmaptool ui, String url) {
        if (!starting) {
            starting = true;
            DynmapOptions options = new DynmapOptions();
            options.url.configuration = "up/configuration";
            options.url.update = "up/world/%s/%s";
            options.url.tiles = "tiles/";
            options.url.sendmessage = "up/sendmessage";
            stoped = false;
            dynmap = new Dynmap(ui, url, options);
            if (dynmap.isConnecting()) {
                ui.content.south.label.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (starting) {
                            dynmap.update();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        stoped = true;
                    }
                }).start();
            } else {
                options = new DynmapOptions();
                options.dynmap_ver_json = true;
                options.url.configuration = "standalone/dynmap_config.json?_=%s";
                options.url.update = "standalone/dynmap_%s.json?_=%s";
                options.url.tiles = "tiles/";
                options.url.sendmessage = "standalone/sendmessage.php";
                stoped = false;
                dynmap = new Dynmap(ui, url, options);
                if (dynmap.isConnecting()) {
                    ui.content.south.label.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (starting) {
                                dynmap.update();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            stoped = true;
                        }
                    }).start();
                } else {
                    ui.content.north.start.button.setIcon(new ImageIcon("resources/img/start.png"));
                    ui.content.north.more.button.setEnabled(true);
                    ui.content.north.url.textField.setEnabled(true);
                    ui.frame.setTitle("McMapTool " + Config.VERSION);
                    starting = false;
                    stoped = true;
                    ui.content.south.label.setText("Ошибка сети или введен неверный адресс!");
                }
            }
        }
        return true;
    }
}
