package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.principal.sqlite.GradesClassWise;
import in.principal.util.GradeClassWiseSort;

public class MarksDao {

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

    public static int getSectionAvg(int classId, int subjectId, long examId, SQLiteDatabase sqliteDatabase) {
        gradesClassWiseList = GradesClassWiseDao.getGradeClassWise(classId, sqliteDatabase);
        Collections.sort(gradesClassWiseList, new GradeClassWiseSort());
        int avg = 0;
        Cursor c = sqliteDatabase.rawQuery("select Grade from marks where ExamId=" + examId + " and SubjectId=" + subjectId , null);
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

    public static int getSectionAvg(long examId, int subjectId, int sectionId, SQLiteDatabase sqliteDatabase){
        int avg = 0;
        String sql = "SELECT (AVG(Mark)/C.MaximumMark)*100 as Average FROM exams A, marks B, subjectexams C WHERE A.ExamId = B.ExamId and C.ExamId=A.ExamId and A.ExamId = " + examId +
                " and B.SubjectId="+subjectId+" and B.StudentId in (select StudentId from Students where SectionId = "+sectionId+") and B.Mark!='-1'" ;
        Cursor c = sqliteDatabase.rawQuery(sql, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            avg = c.getInt(c.getColumnIndex("Average"));
            c.moveToNext();
        }
        c.close();
        return avg;
    }

    public static int getStudExamAvg(int studentId, int subjectId, long examId, SQLiteDatabase sqliteDatabase) {
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select (AVG(A.Mark)/B.MaximumMark)*100 as avg from marks A, subjectexams B where A.ExamId=B.ExamId and A.StudentId=" + studentId + " and" +
                " A.SubjectId=B.SubjectId and A.SubjectId=" + subjectId + " and A.ExamId=" + examId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            i = c.getInt(c.getColumnIndex("avg"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int getStudExamMark(int studentId, int subjectId, long examId, SQLiteDatabase sqliteDatabase) {
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select Mark from Marks where ExamId=" + examId + " and SubjectId=" + subjectId + " and StudentId=" + studentId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            i = c.getInt(c.getColumnIndex("Mark"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int getExamMaxMark(int subjectId, long examId, SQLiteDatabase sqliteDatabase) {
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select MaximumMark from subjectexams where ExamId=" + examId + " and SubjectId=" + subjectId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            i = c.getInt(c.getColumnIndex("MaximumMark"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int isThereExamMark(long examId, int sectionId, int subjectId, SQLiteDatabase sqliteDatabase) {
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("SELECT A.Mark from marks A, students B where A.ExamId=" + examId + " and A.StudentId=B.StudentId and B.SectionId=" + sectionId
                + " and A.SubjectId=" + subjectId + " and A.Mark!=0", null);
        if (c.getCount() > 0) {
            isThere = 1;
        }
        c.close();
        return isThere;
    }

    public static int isThereExamGrade(long examId, int sectionId, int subjectId, SQLiteDatabase sqliteDatabase) {
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("SELECT A.Grade from marks A, students B where A.ExamId=" + examId + " and A.StudentId=B.StudentId and B.SectionId=" + sectionId
                + " and A.SubjectId=" + subjectId + " LIMIT 1", null);
        if (c.getCount() > 0) {
            isThere = 1;
        }
        c.close();
        return isThere;
    }

}
