package in.principal.dao;

import in.principal.sqlite.Students;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StudentsDao {
	
	public static ArrayList<Students> selectStudents(int sectionId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from students where SectionId="+sectionId+" group by RollNoInClass", null);
		ArrayList<Students> sList = new ArrayList<>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			Students stud = new Students();
			stud.setStudentId(c.getInt(c.getColumnIndex("StudentId")));
			stud.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			stud.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
			stud.setRollNoInClass(c.getInt(c.getColumnIndex("RollNoInClass")));
			stud.setName(c.getString(c.getColumnIndex("Name")));
			sList.add(stud);
			c.moveToNext();
		}
		c.close();
		return sList;
	}
	
	public static String getStudentName(int studentId, SQLiteDatabase sqliteDatabase){
		String name = null;
		Cursor c = sqliteDatabase.rawQuery("select Name from students where StudentId="+studentId, null);
		c.moveToFirst();
		try{
			name = c.getString(c.getColumnIndex("Name"));
		}catch(IndexOutOfBoundsException e){
			e.printStackTrace();
		}

		c.close();
		return name;
	}

	public static List<Students> selectAbsentStudents(List<Integer> ids, SQLiteDatabase sqliteDatabase){
		List<Students> sList = new ArrayList<Students>();
		for(Integer id: ids){
			Cursor c = sqliteDatabase.rawQuery("select * from students where StudentId="+id, null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				Students stud = new Students();
				stud.setStudentId(c.getInt(c.getColumnIndex("StudentId")));
				stud.setClassId(c.getInt(c.getColumnIndex("ClassId")));
				stud.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
				stud.setRollNoInClass(c.getInt(c.getColumnIndex("RollNoInClass")));
				stud.setName(c.getString(c.getColumnIndex("Name")));
				sList.add(stud);
				c.moveToNext();
			}
			c.close();
		}
		return sList;
	}
	
	public static int clasTotalStrength(int classId, SQLiteDatabase sqliteDatabase){
		int i=0;
		String sql = "select Count(*) as count from students where ClassId="+classId;
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			i = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return i;
	}
	
	public static int secTotalStrength(int secId, SQLiteDatabase sqliteDatabase){
		int i=0;
		String sql = "select Count(*) as count from students where SectionId="+secId;
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			i = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return i;
	}

}
