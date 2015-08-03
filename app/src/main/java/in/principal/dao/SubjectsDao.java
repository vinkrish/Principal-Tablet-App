package in.principal.dao;

import in.principal.sqlite.Subjects;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SubjectsDao {
	
	public static List<Subjects> selectSubjects(SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from subjects", null);
		List<Subjects> sList = new ArrayList<>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			Subjects sub = new Subjects();
			sub.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
			sub.setSubjectName(c.getString(c.getColumnIndex("SubjectName")));
			sList.add(sub);
			c.moveToNext();
		}
		c.close();
		return sList;
	}
	
	public static String getSubjectName(int subjectId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select SubjectName from subjects where SubjectId="+subjectId, null);
		c.moveToFirst();
		String s = c.getString(c.getColumnIndex("SubjectName"));
		c.close();
		return s;
	}

}
