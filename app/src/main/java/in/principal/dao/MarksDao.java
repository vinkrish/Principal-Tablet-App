package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MarksDao {

    public static int getStudExamAvg(int studentId, int subjectId, long examId, SQLiteDatabase sqliteDatabase){
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select (AVG(A.Mark)/B.MaximumMark)*100 as avg from marks A, subjectexams B where A.ExamId=B.ExamId and A.StudentId="+studentId+" and" +
                " A.SubjectId=B.SubjectId and A.SubjectId="+subjectId+" and A.ExamId="+examId, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getInt(c.getColumnIndex("avg"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int getStudExamMark(int studentId, int subjectId, long examId, SQLiteDatabase sqliteDatabase){
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select Mark from Marks where ExamId="+examId+" and SubjectId="+subjectId+" and StudentId="+studentId, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getInt(c.getColumnIndex("Mark"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int getExamMaxMark(int subjectId, long examId, SQLiteDatabase sqliteDatabase){
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select MaximumMark from subjectexams where ExamId="+examId+" and SubjectId="+subjectId, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getInt(c.getColumnIndex("MaximumMark"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int isThereExamMark(long examId, int sectionId, int subjectId, SQLiteDatabase sqliteDatabase){
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("SELECT A.Mark from marks A, students B where A.ExamId="+examId+" and A.StudentId=B.StudentId and B.SectionId="+sectionId
                +" and A.SubjectId="+subjectId+" and A.Mark!=0", null);
        if(c.getCount()>0){
            isThere = 1;
        }
        c.close();
        return isThere;
    }

    public static int isThereExamGrade(long examId, int sectionId, int subjectId, SQLiteDatabase sqliteDatabase){
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("SELECT A.Grade from marks A, students B where A.ExamId="+examId+" and A.StudentId=B.StudentId and B.SectionId="+sectionId
                +" and A.SubjectId="+subjectId+" LIMIT 1", null);
        if(c.getCount()>0){
            isThere = 1;
        }
        c.close();
        return isThere;
    }

}
