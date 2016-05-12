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

}
