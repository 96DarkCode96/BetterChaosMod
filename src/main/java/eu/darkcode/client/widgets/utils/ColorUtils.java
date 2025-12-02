package eu.darkcode.client.widgets.utils;

public final class ColorUtils {

    public static int hsvToArgb(int a, float h, float s, float v) {
        // h: 0..360, s:0..1, v:0..1
        float c = v * s;
        float hh = h / 60.0f;
        float x = c * (1 - Math.abs(hh % 2 - 1));
        float r1 = 0;
        float g1 = 0;
        float b1 = 0;
        if (0 <= hh && hh < 1) {
            r1 = c;
            g1 = x;
            b1 = 0;
        } else if (1 <= hh && hh < 2) {
            r1 = x;
            g1 = c;
            b1 = 0;
        } else if (2 <= hh && hh < 3) {
            r1 = 0;
            g1 = c;
            b1 = x;
        } else if (3 <= hh && hh < 4) {
            r1 = 0;
            g1 = x;
            b1 = c;
        } else if (4 <= hh && hh < 5) {
            r1 = x;
            g1 = 0;
            b1 = c;
        } else if (5 <= hh && hh < 6) {
            r1 = c;
            g1 = 0;
            b1 = x;
        }
        float m = v - c;
        int r = (int) ((r1 + m) * 255.0f + 0.5f);
        int g = (int) ((g1 + m) * 255.0f + 0.5f);
        int b = (int) ((b1 + m) * 255.0f + 0.5f);
        return (a << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static int hueToArgb(float h) {
        final int x = (int) ((1 - Math.abs(h % 2 - 1)) * 255f + 0.5f);
        if (h < 0 || h >= 6)
            return 0xFF000000;
        if (h >= 5)
            return 0xFFFF0000 | (x & 0xFF);
        if (h >= 4)
            return 0xFF0000FF | ((x & 0xFF) << 16);
        if (h >= 3)
            return 0xFF0000FF | ((x & 0xFF) << 8);
        if (h >= 2)
            return 0xFF00FF00 | (x & 0xFF);
        if (h >= 1)
            return 0xFF00FF00 | ((x & 0xFF) << 16);
        if (h >= 0)
            return 0xFFFF0000 | ((x & 0xFF) << 8);
        return 0xFF000000;
    }

    public static float[] rgbToHsv(int argb) {
        int rI = (argb >> 16) & 0xFF;
        int gI = (argb >> 8) & 0xFF;
        int bI = argb & 0xFF;
        float r = rI / 255.0f;
        float g = gI / 255.0f;
        float b = bI / 255.0f;

        float v = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = v - min;
        float s = (v == 0) ? 0 : delta / v;
        float h;
        if (delta == 0) {
            h = 0;
        } else if (v == r) {
            h = 60 * (((g - b) / delta) % 6);
        } else if (v == g) {
            h = 60 * (((b - r) / delta) + 2);
        } else {
            h = 60 * (((r - g) / delta) + 4);
        }
        if (h < 0) h += 360;
        return new float[]{h, s, v};
    }

}
