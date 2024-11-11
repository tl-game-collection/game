package com.xiuxiu.app.server;

public class ProbConfig {
    public static int FGF_235                   = 0050;
    public static int FGF_NONE                  = 6300 + FGF_235;
    public static int FGF_DOUBLE                = 2000 + FGF_NONE;
    public static int FGF_LINE                  = 1000 + FGF_DOUBLE;
    public static int FGF_SAME_COLOR            = 0500 + FGF_LINE;
    public static int FGF_SAME_COLOR_AND_LINE   = 0100 + FGF_SAME_COLOR;
    public static int FGF_THREE                 = 0050 + FGF_SAME_COLOR_AND_LINE;

    public static void setPropFgf(String value) {
        String[] temp = value.split(",");
        try {
            FGF_235 = Integer.parseInt(temp[0]) * 100;
            FGF_NONE = Integer.parseInt(temp[1]) * 100 + FGF_235;
            FGF_DOUBLE = Integer.parseInt(temp[2]) * 100 + FGF_NONE;
            FGF_LINE= Integer.parseInt(temp[3]) * 100 + FGF_DOUBLE;
            FGF_SAME_COLOR = Integer.parseInt(temp[4]) * 100 + FGF_LINE;
            FGF_SAME_COLOR_AND_LINE = Integer.parseInt(temp[5]) * 100 + FGF_SAME_COLOR;
            FGF_THREE = Integer.parseInt(temp[6]) * 100 + FGF_SAME_COLOR_AND_LINE;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
