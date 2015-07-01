package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StAvgDao {
	
	public static int stClassAvg(int classId, SQLiteDatabase sqliteDatabase){
		int avg = 0;
		String sql = "select AVG(SlipTestAvg) as average from stavg where SlipTestAvg!=0 and ClassId="+classId;
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			avg = c.getInt(c.getColumnIndex("average"));
			c.moveToNext();
		}
		c.close();
		return avg;
	}

	public static int stSecAvg(int sectionId, SQLiteDatabase sqliteDatabase){
		int avg = 0;
		String sql = "select AVG(SlipTestAvg) as average from stavg where SlipTestAvg!=0 and SectionId="+sectionId;
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			avg = c.getInt(c.getColumnIndex("average"));
			c.moveToNext();
		}
		c.close();
		return avg;
	}	

	public static int selectStAvg(int sectionId, int subjectId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from stavg where SectionId="+sectionId+" and SubjectId="+subjectId, null);
		c.moveToFirst();
		int avg = 0;
		while(!c.isAfterLast()){
			int a = c.getInt(c.getColumnIndex("SlipTestAvg"));
			if(a!=0){
				avg = (int)(((double)a/(360.0))*100);
			}
			c.moveToNext();
		}
		c.close();
		return avg;
	}
	
	public static void updateSlipTestAvg(int sectionId, int subjectId, int avg, int schoolId, SQLiteDatabase sqliteDatabase){
		String sql = "update stavg set SlipTestAvg="+avg+" where SectionId="+sectionId+" and SubjectId="+subjectId;
		sqliteDatabase.execSQL(sql);
	}

}
