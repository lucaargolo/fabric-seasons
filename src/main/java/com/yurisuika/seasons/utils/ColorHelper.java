package com.yurisuika.seasons.utils;

import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class ColorHelper {

    private static Color changeSaturation(Color color, float sat) {
        float s = sat/100f;
        float r = color.getRed()/255f;
        float g = color.getGreen()/255f;
        float b = color.getBlue()/255f;
        float p = MathHelper.sqrt((r*r*0.299f)+(g*g*0.597f)+(b*b*0.114f));
        float newRed = p+(r-p)*s;
        float newGreen = p+(g-p)*s;
        float newBlue = p+(b-p)*s;
        if(newRed < 0f) newRed = 0f;
        if(newRed > 1f) newRed = 1f;
        if(newGreen < 0f) newGreen = 0f;
        if(newGreen > 1f) newGreen = 1f;
        if(newBlue < 0f) newBlue = 0f;
        if(newBlue > 1f) newBlue = 1f;
        return new Color(newRed, newGreen, newBlue);
    }

    public static Color changeHueSatBri(Color color, float h, float s, float b) {
        color = changeSaturation(color, s);
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float hue = MathHelper.lerp(hsb[0], 0f, 360f);
        hue += h;
        if(hue > 360f) hue -= 360f;
        if(hue < 0f) hue = 360f + hue;
        hue /= 360f;

        float briDelta = (b < 0) ? (b/100f) * -1f : (b/100f);
        float briLerp = (b < 0) ? 0f : 1f;
        float bri = MathHelper.lerp(briDelta, hsb[2], briLerp);

        color = Color.getHSBColor(hue, hsb[1], bri);
        return color;
    }

}
