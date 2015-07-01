package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CceAspectPrimaryDao {
	
	public static String getAspectName(int aspectId, SQLiteDatabase sqliteDatabase){
		String aspectName = "";
		Cursor c = sqliteDatabase.rawQuery("select AspectName from cceaspectprimary where AspectId="+aspectId, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			aspectName = c.getString(c.getColumnIndex("AspectName"));
			c.moveToNext();
		}
		c.close();
		return aspectName;
	}

}
