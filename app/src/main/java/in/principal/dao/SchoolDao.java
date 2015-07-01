package in.principal.dao;

import java.util.ArrayList;

import in.principal.sqlite.School;
import in.principal.sqlite.Temp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SchoolDao {

	public static ArrayList<School> selectSchool(SQLiteDatabase sqliteDatabase){
		ArrayList<School> schoolList = new ArrayList<School>();
		Cursor c= sqliteDatabase.rawQuery("select * from school", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			School school = new School();
			school.setPrincipalTeacherId(c.getInt(c.getColumnIndex("PrincipalTeacherId")));
			school.setSchoolId(c.getInt(c.getColumnIndex("SchoolId")));
			schoolList.add(school);
			c.moveToNext();
		}
		c.close();
		return schoolList;
	}

	public static int getSchoolId(SQLiteDatabase sqliteDatabase){
		int schoolId = 0;
		Temp t = TempDao.selectTemp(sqliteDatabase);
		schoolId = t.getSchoolId();
		return schoolId;
	}

}
