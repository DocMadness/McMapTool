/*
 * Copyright (c) 2015-2016. DCIWE.com
 */

package com.dciwe.mcmaptool;

import java.awt.image.BufferedImage;

public class MapTile {
    public BufferedImage image;
    public int x;
    public int y;
    public MapTile(BufferedImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }
}
