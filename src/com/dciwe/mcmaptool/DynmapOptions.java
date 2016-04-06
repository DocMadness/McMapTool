/*
 * Copyright (c) 2015-2016. DCIWE.com
 */

package com.dciwe.mcmaptool;

import java.util.*;

public class DynmapOptions {
    public URL url = new URL();
    public boolean dynmap_ver_json = false;
    public int maxcount = 0;
    public boolean login_enabled = false;
    public String msg_hiddennamejoin = "";
    public String quitmessage = "";
    public boolean webchat_requires_login = false;
    public HashMap<String/*name*/, Worlds> worlds = new HashMap<>();
    public long confighash = 0;
    public String showlayercontrol = "";
    public String title = "";
    public String msg_chatnotallowed = "";
    public String msg_players = "";
    public boolean showplayerfacesinmenu = false;
    public Worlds.Maps defaultmap = new Worlds.Maps();
    public String defaultmap_name = "";
    public boolean loggedin = false;
    public boolean cyrillic = false;
    public int chatlengthlimit = 0;
    public String webprefix = "";
    public String coreversion = "";
    public String dynmapversion = "";
    public boolean allowchat = false;
    public double updaterate = 0;
    public String sidebaropened = "";
    public String msg_maptypes = "";
    public int defaultzoom = 1;
    public Worlds defaultworld = new Worlds();
    public String defaultworld_name = "";
    public String joinmessage = "";
    public String spammessage = "";
    public boolean grayplayerswhenhidden = false;
    public boolean allowwebchat = false;
    public double webchat_interval = 0;
    public String msg_hiddennamequit = "";
    public String msg_chatrequireslogin = "";
    public HashMap<String/*type*/, Components> components = new HashMap<>();
    public long timestamp = new Date().getTime();

    public Update update = new Update();

    public final List<Long> ts_join = Collections.synchronizedList(new ArrayList<Long>());
    public final List<Long> ts_message = Collections.synchronizedList(new ArrayList<Long>());
    public final List<Long> ts_quit = Collections.synchronizedList(new ArrayList<Long>());

    public static class URL {
        String server;
        Map<String, String> query_pairs = new HashMap<>();
        String configuration;
        String update;
        String tiles;
        String sendmessage;
    }
    public static class Worlds {
        public Center center = new Center(0.0,0.0,0.0);
        public int extrazoomout = 0;
        public String title = "";
        public int worldheight = 0;
        public boolean _protected = false;
        public HashMap<String/*name*/, Maps> maps = new HashMap<>();
        public int sealevel = 64;
        public String name = "";

        public static class Center {
            public double x = 0;
            public double y = 0;
            public double z = 0;
            public Center(double x, double y, double z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }
        }
        public static class Maps {
            public boolean nightandday = false;
            public ArrayList<Double> worldtomap = new ArrayList<>();
            public int scale = 0;
            public Object icon = null;
            public int mapzoomin = 0;
            public String shader = "";
            public ArrayList<Double> maptoworld = new ArrayList<>();
            public boolean bigmap = false;
            public int inclination = 0;
            public String type = "";
            public Object backgroundday = null;
            public Object backgroundnight = null;
            public int mapzoomout = 0;
            public String title = "";
            public boolean _protected = false;
            public String lighting = "";
            public int boostzoom = 0;
            public Object background = null;
            public String prefix = "";
            public String name = "";
            public String compassview = "";
            public String perspective = "";
            public int azimuth = 0;
            public String image_format = "";
        }
    }
    public static class Components {
        public String type = "";
        public boolean offlinehidebydefault = false;
        public boolean showspawnbeds = false;
        public String spawnicon = "";
        public int spawnbedminzoom = 0;
        public String offlinelabel = "";
        public boolean showspawn = false;
        public String spawnbedicon = "";
        public boolean showlabel = false;
        public int maxofflinetime = 0;
        public String spawnbedformat = "";
        public boolean spawnbedhidebydefault = false;
        public String spawnbedlabel = "";
        public String default_sign_set = "";
        public String spawnlabel = "";
        public int offlineminzoom = 0;
        public boolean enablesigns = false;
        public String offlineicon = "";
        public boolean showofflineplayers = false;
        public boolean allowurlname = false;
        public boolean focuschatballoons = false;
        public int messagettl = 0;
        public boolean sendbutton = false;
        public int scrollback = 0;
        public int visiblelines = 0;
        public boolean showplayerfaces = false;
        public int layerprio = 0;
        public String label = "";
        public boolean smallplayerfaces = false;
        public boolean showplayerbody = false;
        public boolean showplayerhealth = false;
        public boolean hidebydefault = false;
        public boolean showdigitalclock = false;
        public boolean showweather = false;
        public boolean hidey = false;
        public boolean show_mcr = false;
    }
    public static class Update {
        public long timestamp = 0l;
        public boolean hasStorm = false;
        public final List<Updates> updates = Collections.synchronizedList(new ArrayList<Updates>());
        public boolean isThundering = false;
        public long servertime = 0l;
        public final List<Players> players = Collections.synchronizedList(new ArrayList<Players>());
        public int currentcount = 0;
        public long confighash = 0l;

        public static class Updates {
            public String type = "";
            public String name = "";
            public String playerName = "";
            public String account = "";
            public String source = "";
            public String message = "";
            public String channel = "";
            public long timestamp = 0l;
        }
        public static class Players {
            public int sort = 0;
            public String name = "";
            public int armor = 0;
            public String account = "";
            public double health = 0d;
            public String type = "";
            public double x = 0d;
            public double y = 0d;
            public double z = 0d;
            public String world = "";
        }
    }
}
