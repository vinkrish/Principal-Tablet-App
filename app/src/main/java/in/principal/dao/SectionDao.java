package in.principal.dao;

import in.principal.sqlite.Section;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SectionDao {
	
	public static String getSecName(int secId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select SectionName from section where SectionId="+secId, null);
		c.moveToFirst();
		String s = c.getString(c.getColumnIndex("SectionName"));
		c.close();
		return s;
	}
	
	public static ArrayList<Section> selectSection(int classId, SQLiteDatabase sqliteDatabase){
		ArrayList<Section> sList = new ArrayList<Section>();
		Cursor c = sqliteDatabase.rawQuery("select * from section where ClassId="+classId+" order by SectionName", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			Section sec = new Section();
			sec.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
			sec.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			sec.setSectionName(c.getString(c.getColumnIndex("SectionName")));
			sec.setClassTeacherId(c.getInt(c.getColumnIndex("ClassTeacherId")));
			sList.add(sec);
			c.moveToNext();
		}
		c.close();
		return sList;
	}

}
