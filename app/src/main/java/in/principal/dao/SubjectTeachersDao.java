package in.principal.dao;

import in.principal.sqlite.SubjectTeacher;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SubjectTeachersDao {
	
	public static List<SubjectTeacher> selectSubjectTeacher(SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from subjectteacher", null);
		List<SubjectTeacher> sList = new ArrayList<SubjectTeacher>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			SubjectTeacher subTec = new SubjectTeacher();
			subTec.setSchoolId(c.getInt(c.getColumnIndex("SchoolId")));
			subTec.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			subTec.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
			subTec.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
			subTec.setTeacherId(c.getInt(c.getColumnIndex("TeacherId")));
			sList.add(subTec);
			c.moveToNext();
		}
		c.close();
		return sList;
	}
	
	public static List<SubjectTeacher> selectSubjTeacher(int sectionId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from subjectteacher where SectionId="+sectionId, null);
		List<SubjectTeacher> sList = new ArrayList<SubjectTeacher>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			SubjectTeacher subTec = new SubjectTeacher();
			subTec.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			subTec.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
			subTec.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
			subTec.setTeacherId(c.getInt(c.getColumnIndex("TeacherId")));
			sList.add(subTec);
			c.moveToNext();
		}
		c.close();
		return sList;
	}

}
