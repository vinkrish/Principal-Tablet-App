package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SubActivityMarkDao {

    public static int getSectionAvg(long subActivityId, SQLiteDatabase sqliteDatabase){
        int avg = 0;
        String sql = "SELECT (AVG(Mark)/A.MaximumMark)*100 as Average FROM subactivity A, subactivitymark B WHERE A.SubActivityId = B.SubActivityId" +
        " and B.Mark!='-1' and A.SubActivityId=" + subActivityId ;
        Cursor c = sqliteDatabase.rawQuery(sql, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            avg = c.getInt(c.getColumnIndex("Average"));
            c.moveToNext();
        }
        c.close();
        return avg;
    }

    public static int getStudSubActMark(long studentId, long subActivityId, SQLiteDatabase sqliteDatabase){
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select Mark from subactivitymark where StudentId="+studentId+" and SubActivityId="+subActivityId,null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getInt(c.getColumnIndex("Mark"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int getStudSubActAvg(long studentId, long subActivityId, SQLiteDatabase sqliteDatabase){
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select (Avg(A.Mark)/B.MaximumMark)*100 as avg from subactivitymark A, subactivity B where A.SubActivityId=B.SubActivityId and A.SubActivityId="+subActivityId+
                " and StudentId="+studentId, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getInt(c.getColumnIndex("avg"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int isThereSubActMark(long subActId, int subjectId, SQLiteDatabase sqliteDatabase){
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("select * from subactivitymark where SubActivityId="+subActId+" and SubjectId="+subjectId+" LIMIT 1", null);
        if(c.getCount()>0){
            isThere = 1;
        }
        c.close();
        return isThere;
    }

    public static int isThereSubActGrade(long subActId, int subjectId, SQLiteDatabase sqliteDatabase){
        int isThere = 0;
        String sql = "select * from subactivitygrade where SubActivityId="+subActId+" and SubjectId="+subjectId+" LIMIT 1";
        Cursor c = sqliteDatabase.rawQuery(sql, null);
        if(c.getCount()>0){
            isThere = 1;
        }
        c.close();
        return isThere;
    }

}
