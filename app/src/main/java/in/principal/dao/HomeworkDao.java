package in.principal.dao;

import in.principal.sqlite.Homework;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HomeworkDao {
	
	public static List<Homework> selectHomework(SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("SELECT * FROM homeworkmessage ORDER BY HomeworkId ASC LIMIT 1", null);
		List<Homework> hwList = new ArrayList<Homework>();
		c.moveToFirst();
		Homework hw = new Homework();
		hw.setClassId(c.getInt(c.getColumnIndex("ClassId")));
		hw.setHomework(c.getString(c.getColumnIndex("Homework")));
		hw.setHomeworkDate(c.getString(c.getColumnIndex("HomeworkDate")));
		hw.setSchoolId(c.getInt(c.getColumnIndex("SchoolId")));
		hw.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
		hw.setSubjectIDs(c.getString(c.getColumnIndex("SubjectIDs")));
		hw.setTeacherId(c.getInt(c.getColumnIndex("TeacherId")));		
		hwList.add(hw);
		c.close();
		return hwList;
	}
	
	public static List<Homework> selectHomework(int sectionId,String homeworkDate, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from homeworkmessage where SectionId="+sectionId+" and HomeworkDate='"+homeworkDate+"' limit 1", null);
		List<Homework> hList = new ArrayList<Homework>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			Homework h = new Homework();
			h.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			h.setHomework(c.getString(c.getColumnIndex("Homework")));
			h.setHomeworkDate(c.getString(c.getColumnIndex("HomeworkDate")));
			h.setHomeworkId(c.getLong(c.getColumnIndex("HomeworkId")));
			h.setMessageFrom(c.getString(c.getColumnIndex("MessageFrom")));
			h.setMessageVia(c.getString(c.getColumnIndex("MessageVia")));
			h.setSchoolId(c.getInt(c.getColumnIndex("SchoolId")));
			h.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
			h.setSubjectIDs(c.getString(c.getColumnIndex("SubjectIDs")));
			h.setTeacherId(c.getInt(c.getColumnIndex("TeacherId")));
			hList.add(h);
			c.moveToNext();
		}
		c.close();
		return hList;
	}
	
	public static List<Integer> getHWGiven(String date, SQLiteDatabase sqliteDatabase){
		List<Integer> idList = new ArrayList<Integer>();
		Cursor c = sqliteDatabase.rawQuery("select A.SectionId from section A, homeworkmessage B where B.HomeworkDate='"+date+"' and A.SectionId=B.SectionId", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			idList.add(c.getInt(c.getColumnIndex("SectionId")));
			c.moveToNext();
		}
		c.close();
		return idList;
	}

}
