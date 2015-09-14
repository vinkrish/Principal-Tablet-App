package in.principal.util;

/**
 * Created by vinkrish.
 */

public class PKGenerator {

    public static long returnPrimaryKey(int schoolId) {
        long unixTime = System.currentTimeMillis() / 1L;
        String s = schoolId + "" + unixTime;
        return Long.parseLong(s, 10);
    }

    public static long getPrimaryKey() {
        return System.currentTimeMillis() / 1L;
    }

    public static String trim(int pos1, int pos2, String s) {
        if (s.length() > pos2) {
            StringBuilder sb = new StringBuilder(s.substring(pos1, pos2)).append("..");
            return sb.toString();
        } else {
            return s.substring(pos1);
        }
    }

}
