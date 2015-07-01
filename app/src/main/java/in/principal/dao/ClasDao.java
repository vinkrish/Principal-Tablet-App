package in.principal.dao;

import in.principal.sqlite.Clas;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClasDao {
	
	public static String getClassName(int classId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select ClassName from class where ClassId="+classId, null);
		c.moveToFirst();
		String s = c.getString(c.getColumnIndex("ClassName"));
		c.close();
		return s;
	}
	
	public static ArrayList<Clas> selectClas(SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from class", null);
		ArrayList<Clas> cList = new ArrayList<Clas>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			Clas clas = new Clas();
			clas.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			clas.setClassName(c.getString(c.getColumnIndex("ClassName")));
			cList.add(clas);
			c.moveToNext();
		}
		c.close();
		return cList;
	}
	
	public static int clasPresentStrength(int classId, String date, SQLiteDatabase sqliteDatabase){
		int i=0;
		Cursor c = sqliteDatabase.rawQuery("select SectionId from section where ClassId="+classId, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			int secId = c.getInt(c.getColumnIndex("SectionId"));
			Cursor c2 = sqliteDatabase.rawQuery("select count(*) as count from students where SectionId="+secId+" and SectionId in (select distinct SectionId from studentattendance where dateattendance='"+date+"')", null);
			c2.moveToFirst();
			while(!c2.isAfterLast()){
				i += c2.getInt(c2.getColumnIndex("count"));
				c2.moveToNext();
			}
			c2.close();
			c.moveToNext();
		}
		c.close();
		return i;
	}

}
