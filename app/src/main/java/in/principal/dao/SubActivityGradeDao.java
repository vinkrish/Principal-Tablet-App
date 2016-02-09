package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.principal.sqlite.GradesClassWise;
import in.principal.util.GradeClassWiseSort;

public class SubActivityGradeDao {
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

    public static int getSectionAvg(int classId, long subActivityId, SQLiteDatabase sqliteDatabase) {
        gradesClassWiseList = GradesClassWiseDao.getGradeClassWise(classId, sqliteDatabase);
        Collections.sort(gradesClassWiseList, new GradeClassWiseSort());
        int avg = 0;
        Cursor c = sqliteDatabase.rawQuery("select Grade from subactivitygrade where SubActivityId=" + subActivityId, null);
        if (c.getCount() > 0){
            c.moveToFirst();
            while (!c.isAfterLast()) {
                avg += getMarkTo(c.getString(c.getColumnIndex("Grade")));
                c.moveToNext();
            }
            c.close();
            return avg/c.getCount();
        } else return 0;
    }

    public static String getSubActivityGrade(long subActId, int studentId, int subjectId, SQLiteDatabase sqLiteDatabase) {
        String grade = "";
        Cursor c = sqLiteDatabase.rawQuery("select Grade from subactivitygrade " +
                "where StudentId = " + studentId + " and SubActivityId = " + subActId + " and SubjectId = " + subjectId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            grade = c.getString(c.getColumnIndex("Grade"));
            c.moveToNext();
        }
        c.close();
        return grade;
    }

    public static int isThereSubActGrade(long subActId, int subjectId, SQLiteDatabase sqliteDatabase) {
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("select * from subactivitygrade where SubActivityId=" + subActId + " and SubjectId=" + subjectId + " LIMIT 1", null);
        if (c.getCount() > 0) {
            isThere = 1;
        }
        c.close();
        return isThere;
    }

    public static List<String> selectSubActivityGrade(long subActivityId, List<Integer> studentId, SQLiteDatabase sqliteDatabase) {
        List<String> aList = new ArrayList<>();
        for (Integer i : studentId) {
            Cursor c = sqliteDatabase.rawQuery("select Grade from subactivitygrade where SubActivityId=" + subActivityId + " and StudentId=" + i, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                aList.add(c.getString(c.getColumnIndex("Grade")));
            } else {
                aList.add("");
            }
            c.close();
        }
        return aList;
    }

}
