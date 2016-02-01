package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ExamsDao {
	
	public static String selectExamName(long examId, SQLiteDatabase sqliteDatabase){
		String s = null;
		Cursor c = sqliteDatabase.rawQuery("select * from exams where ExamId="+examId, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			s = c.getString(c.getColumnIndex("ExamName"));
			c.moveToNext();
		}
		c.close();
		return s;
	}

}
