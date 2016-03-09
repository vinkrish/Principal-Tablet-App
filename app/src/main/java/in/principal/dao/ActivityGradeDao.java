package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.principal.sqlite.GradesClassWise;
import in.principal.util.GradeClassWiseSort;

public class ActivityGradeDao {
    static List<GradesClassWise> gradesClassWiseList = new ArrayList<>();

    private static int getMarkTo(String grade) {
        int markTo = 0;
        for (GradesClassWise gcw : gradesClassWiseList) {
            if (grade.equals(gcw.getGrade())) {
                markTo = gcw.getMarkTo();
                break;
            }
        }
        return markTo;
    }

    public static int getSectionAvg(int classId, long activityId, SQLiteDatabase sqliteDatabase) {
        gradesClassWiseList = GradesClassWiseDao.getGradeClassWise(classId, sqliteDatabase);
        Collections.sort(gradesClassWiseList, new GradeClassWiseSort());
        int avg = 0;
        Cursor c = sqliteDatabase.rawQuery("select Grade from activitygrade where ActivityId=" + activityId, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                avg += getMarkTo(c.getString(c.getColumnIndex("Grade")));
                c.moveToNext();
            }
            c.close();
            return avg / c.getCount();
        } else return 0;
    }

    public static String getActivityGrade(long actId, long studentId, int subjectId, SQLiteDatabase sqLiteDatabase) {
        String grade = "";
        Cursor c = sqLiteDatabase.rawQuery("select Grade from activitygrade " +
                "where StudentId = " + studentId + " and ActivityId = " + actId + " and SubjectId = " + subjectId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            grade = c.getString(c.getColumnIndex("Grade"));
            c.moveToNext();
        }
        c.close();
        return grade;
    }

    public static int isThereActGrade(long actId, int subjectId, SQLiteDatabase sqliteDatabase) {
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("select * from activitygrade where ActivityId=" + actId + " and SubjectId=" + subjectId + " LIMIT 1", null);
        if (c.getCount() > 0) {
            isThere = 1;
        }
        c.close();
        return isThere;
    }

    public static List<String> selectActivityGrade(long activityId, List<Long> studentId, SQLiteDatabase sqliteDatabase) {
        List<String> mList = new ArrayList<>();
        for (Long i : studentId) {
            Cursor c = sqliteDatabase.rawQuery("select Grade from activitygrade where ActivityId=" + activityId + " and StudentId=" + i, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                mList.add(c.getString(c.getColumnIndex("Grade")));
            } else {
                mList.add("");
            }
            c.close();
        }
        return mList;
    }
}
