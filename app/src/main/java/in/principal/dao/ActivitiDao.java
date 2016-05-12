package in.principal.dao;

import in.principal.sqlite.Activiti;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class ActivitiDao {

	public static float getActivityMaxMark(long activityId, SQLiteDatabase sqliteDatabase){
		float maxMark = 0;
		Cursor c = sqliteDatabase.rawQuery("select MaximumMark from activity where ActivityId="+activityId, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			maxMark = c.getFloat(c.getColumnIndex("MaximumMark"));
			c.moveToNext();
		}
		c.close();
		return maxMark;
	}
	
	public static List<Activiti> selectActiviti(long examId, int subjectId, int sectionId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from activity  where ExamId="+examId+" and SubjectId="+subjectId+" and SectionId="+sectionId, null);
		List<Activiti> aList = new ArrayList<>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			Activiti a = new Activiti();
			a.setActivityId(c.getLong(c.getColumnIndex("ActivityId")));
			a.setActivityName(c.getString(c.getColumnIndex("ActivityName")));
			a.setCalculation(c.getInt(c.getColumnIndex("Calculation")));
			a.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			a.setExamId(c.getLong(c.getColumnIndex("ExamId")));
			a.setMaximumMark(c.getInt(c.getColumnIndex("MaximumMark")));
			a.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
			a.setSubActivity(c.getInt(c.getColumnIndex("SubActivity")));
			a.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
			a.setWeightage(c.getInt(c.getColumnIndex("Weightage")));
			a.setActivityAvg(c.getInt(c.getColumnIndex("ActivityAvg")));
			a.setCompleteEntry(c.getInt(c.getColumnIndex("CompleteEntry")));
			aList.add(a);
			c.moveToNext();
		}
		c.close();
		return aList;
	}
	
	public static String selectActivityName(long activityId, SQLiteDatabase sqliteDatabase){
		String s = null;
		Cursor c = sqliteDatabase.rawQuery("select * from activity where ActivityId="+activityId, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			s = c.getString(c.getColumnIndex("ActivityName"));
			c.moveToNext();
		}
		c.close();
		return s;
	}
	
	public static int isThereActivity(int sectionId, int subjectId, long examId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from activity where ExamId="+examId+" and SubjectId="+subjectId+" and SectionId="+sectionId, null);
		int count=0;
		if(c.getCount()>0){
			count=1;
		}
		c.close();
		return count;
	}

}
