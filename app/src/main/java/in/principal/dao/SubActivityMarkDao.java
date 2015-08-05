package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SubActivityMarkDao {

    public static int getStudSubActAvg(int studentId, int subActivityId, SQLiteDatabase sqliteDatabase){
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

}
