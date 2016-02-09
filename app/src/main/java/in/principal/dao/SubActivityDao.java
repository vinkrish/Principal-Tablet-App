package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import in.principal.sqlite.SubActivity;

public class SubActivityDao {

    public static List<Long> getSubActIds(long activityId, SQLiteDatabase sqliteDatabase) {
        Cursor c = sqliteDatabase.rawQuery("select SubActivityId from subactivity where ActivityId=" + activityId, null);
        List<Long> aList = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            aList.add(c.getLong(c.getColumnIndex("SubActivityId")));
            c.moveToNext();
        }
        c.close();
        return aList;
    }

    public static float getSubActMaxMark(long subActivityId, SQLiteDatabase sqliteDatabase) {
        float maxMark = 0;
        Cursor c = sqliteDatabase.rawQuery("select MaximumMark from subactivity where SubActivityId=" + subActivityId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            maxMark = c.getFloat(c.getColumnIndex("MaximumMark"));
            c.moveToNext();
        }
        c.close();
        return maxMark;
    }

    public static void updateSubActivityAvg(SQLiteDatabase sqliteDatabase) {
        String sql = "SELECT A.SubActivityId, (AVG(Mark)/A.MaximumMark)*360 as Average FROM subactivity A, subactivitymark B, students C WHERE B.StudentId=C.StudentId and A.SubActivityId = B.SubActivityId and " +
                " B.Mark!='-1' GROUP BY A.SubActivityId,B.SubActivityId";
        Cursor c = sqliteDatabase.rawQuery(sql, null);
        String sql2 = "update subactivity set SubActivityAvg=? where SubActivityId=?";
        sqliteDatabase.beginTransaction();
        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql2);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            stmt.bindDouble(1, c.getDouble(c.getColumnIndex("Average")));
            stmt.bindLong(2, c.getLong(c.getColumnIndex("SubActivityId")));
            stmt.execute();
            stmt.clearBindings();
            c.moveToNext();
        }
        c.close();
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
    }

    public static void updateSubActivityAvg(List<Long> subActList, SQLiteDatabase sqliteDatabase) {
        for (Long subAct : subActList) {
            sqliteDatabase.execSQL("update subactivity set CompleteEntry=1, SubActivityAvg=(SELECT (AVG(Mark)/A.MaximumMark)*360 as Average FROM subactivity A, subactivitymark B WHERE A.SubActivityId = B.SubActivityId and" +
                    " B.Mark!='-1' and A.SubActivityId=" + subAct + ") where SubActivityId=" + subAct);
        }
    }

    public static void checkSubActivityMarkEmpty(List<Long> subactList, SQLiteDatabase sqliteDatabase) {
        StringBuilder sb = new StringBuilder();
        for (Long subact : subactList) {
            sb.append(",").append(subact + "");
        }
        String s = sb.substring(1, sb.length());
        sqliteDatabase.execSQL("update subactivity set CompleteEntry=1 where SubActivityId in (" + s + ") and (select count(*) from subactivity A, subactivitymark B WHERE A.SubActivityId=B.SubActivityId"
                + " AND A.SubActivityId in (" + s + ") GROUP BY A.SubActivityId HAVING COUNT(*)>0)");
    }

    public static String selectSubActivityName(long subActivityId, SQLiteDatabase sqliteDatabase) {
        String s = null;
        Cursor c = sqliteDatabase.rawQuery("select * from subactivity where SubActivityId=" + subActivityId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            s = c.getString(c.getColumnIndex("SubActivityName"));
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public static List<SubActivity> selectSubActivity(long activityId, SQLiteDatabase sqliteDatabase) {
        Cursor c = sqliteDatabase.rawQuery("select * from subactivity where ActivityId=" + activityId, null);
        List<SubActivity> aList = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            SubActivity a = new SubActivity();
            a.setSubActivityId(c.getLong(c.getColumnIndex("SubActivityId")));
            a.setActivityId(c.getLong(c.getColumnIndex("ActivityId")));
            a.setSubActivityName(c.getString(c.getColumnIndex("SubActivityName")));
            a.setCalculation(c.getInt(c.getColumnIndex("Calculation")));
            a.setClassId(c.getInt(c.getColumnIndex("ClassId")));
            a.setExamId(c.getInt(c.getColumnIndex("ExamId")));
            a.setMaximumMark(c.getInt(c.getColumnIndex("MaximumMark")));
            a.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
            a.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
            a.setWeightage(c.getInt(c.getColumnIndex("Weightage")));
            a.setSubActivityAvg(c.getInt(c.getColumnIndex("SubActivityAvg")));
            a.setCompleteEntry(c.getInt(c.getColumnIndex("CompleteEntry")));
            aList.add(a);
            c.moveToNext();
        }
        c.close();
        return aList;
    }

    public static int isThereSubAct(long actId, SQLiteDatabase sqliteDatabase) {
        Cursor c = sqliteDatabase.rawQuery("select * from subactivity where ActivityId=" + actId, null);
        int count = 0;
        if (c.getCount() > 0) {
            count = 1;
        }
        c.close();
        return count;
    }

}
