package com.manech.manechapp.helper;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MyColor {
    private final int red, green, blue;

    public MyColor(int red, int green, int blue) {
        this.red = (red < 0) ? 0 : (red > 255) ? 255 : red;
        this.green = (green < 0) ? 0 : (green > 255) ? 255 : green;
        this.blue = (blue < 0) ? 0 : (blue > 255) ? 255 : blue;
    }

    public static MyColor random(int min, int max) {
        Random rand = new Random();
        int red = rand.nextInt((max - min) + 1) + min;
        int green = rand.nextInt((max - min) + 1) + min;
        int blue = rand.nextInt((max - min) + 1) + min;
        return new MyColor(red, green, blue);
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject color = new JSONObject();
        color.put("r", red);
        color.put("g", green);
        color.put("b", blue);
        return color;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean b = super.equals(obj);
        if (obj instanceof MyColor) {
            MyColor c = (MyColor) obj;
            b = (b || (this.blue == c.blue && this.green == c.green && this.red == c.red));
        }
        return b;
    }
}
