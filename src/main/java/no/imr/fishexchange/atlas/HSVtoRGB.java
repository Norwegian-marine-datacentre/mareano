package no.imr.fishexchange.atlas;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author trondwe
 */
public class HSVtoRGB {

    public static List<Color> makeColorScale(float hue, Integer nstep) {
        List<Color> colorlist = new ArrayList<Color>();
        float saturationstep = 1.0f / nstep;
        float saturation = 0.0f;
        for (int i = 0; i < nstep; i++) {
            saturation = saturation + saturationstep;
            Color aColor = Color.getHSBColor(hue, saturation, 0.5f);
            colorlist.add(aColor);
        }
        return colorlist;
    }
    public static List<String> makeHexColorScale(float hue, Integer nstep) {
        List<String> colorlist = new ArrayList<String>();
        float saturationstep = 1.0f / nstep;
        float saturation = 0.0f;
        for (int i = 0; i < nstep; i++) {
            saturation = saturation + saturationstep;
            Color aColor = Color.getHSBColor(hue, saturation, 0.5f);
            String R = Integer.toHexString(aColor.getRed());
            if (R.length() == 1) {R = "0" + R;}
            String G = Integer.toHexString(aColor.getGreen());
            if (G.length() == 1) {G = "0" + G;}
            String B = Integer.toHexString(aColor.getBlue());
            if (B.length() == 1) {B = "0" + B;}
            String hexcolor = R + G + B;
            colorlist.add(hexcolor);
        }
        return colorlist;
    }
}
