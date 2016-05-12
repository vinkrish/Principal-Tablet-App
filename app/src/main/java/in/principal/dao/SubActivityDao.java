package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import in.principal.sqlite.SubActivity;

public class SubActivityDao {

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
