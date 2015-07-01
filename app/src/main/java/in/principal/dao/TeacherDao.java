package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TeacherDao {
	
	public static String getTeacherName(int teacherId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select Name from teacher where TeacherId="+teacherId, null);
		c.moveToFirst();
		String s = c.getString(c.getColumnIndex("Name"));
		c.close();
		return s;
	}

}
