package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ActivityMarkDao {

    public static int getStudActMark(int studentId, long activityId, SQLiteDatabase sqliteDatabase){
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select Mark from activitymark where StudentId="+studentId+" and ActivityId="+activityId,null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getInt(c.getColumnIndex("Mark"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int getStudActAvg(int studentId, long activityId, SQLiteDatabase sqliteDatabase){
        int i = 0;
        Cursor c = sqliteDatabase.rawQuery("select (Avg(A.Mark)/B.MaximumMark)*100 as avg from activitymark A, activity B where A.ActivityId=B.ActivityId and A.ActivityId="+activityId+
                " and StudentId="+studentId, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getInt(c.getColumnIndex("avg"));
            c.moveToNext();
        }
        c.close();
        return i;
    }

    public static int isThereActMark(long actId, int subjectId, SQLiteDatabase sqliteDatabase){
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("select * from activitymark where ActivityId="+actId+" and SubjectId="+subjectId+" LIMIT 1", null);
        if(c.getCount()>0){
            isThere = 1;
        }
        c.close();
        return isThere;
    }

    public static int isThereActGrade(long actId, int subjectId, SQLiteDatabase sqliteDatabase){
        int isThere = 0;
        Cursor c = sqliteDatabase.rawQuery("select * from activitygrade where ActivityId="+actId+" and SubjectId="+subjectId+" LIMIT 1", null);
        if(c.getCount()>0){
            isThere = 1;
        }
        c.close();
        return isThere;
    }

}
