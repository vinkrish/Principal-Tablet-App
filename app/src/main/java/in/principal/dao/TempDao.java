package in.principal.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import in.principal.sqlite.Temp;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TempDao {
	
	public static Temp selectTemp(SQLiteDatabase sqliteDatabase){
		Temp t = new Temp();
		Cursor c = sqliteDatabase.rawQuery("select * from temp where id = 1", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			t.setId(c.getInt(c.getColumnIndex("id")));
			t.setDeviceId(c.getString(c.getColumnIndex("DeviceId")));
			t.setSchoolId(c.getInt(c.getColumnIndex("SchoolId")));
			t.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			t.setClassName(c.getString(c.getColumnIndex("ClassName")));
			t.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
			t.setSectionName(c.getString(c.getColumnIndex("SectionName")));
			t.setTeacherId(c.getInt(c.getColumnIndex("TeacherId")));
			t.setStudentId(c.getInt(c.getColumnIndex("StudentId")));
			t.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
			t.setExamId(c.getInt(c.getColumnIndex("ExamId")));
			t.setExamId2(c.getInt(c.getColumnIndex("ExamId2")));
			t.setActivityId(c.getLong(c.getColumnIndex("ActivityId")));
			t.setSubActivityId(c.getLong(c.getColumnIndex("SubActivityId")));
			t.setSlipTestId(c.getLong(c.getColumnIndex("SlipTestId")));
			t.setSelectedDate(c.getString(c.getColumnIndex("SelectedDate")));
			t.setYesterDate(c.getString(c.getColumnIndex("YesterDate")));
			t.setOtherDate(c.getString(c.getColumnIndex("OtherDate")));
			t.setSyncTime(c.getString(c.getColumnIndex("SyncTime")));
			t.setIsSync(c.getInt(c.getColumnIndex("IsSync")));
			c.moveToNext();
		}
		c.close();	
		return t;
	}
	
	public static void updateTemp(Temp t, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("ClassId", t.getClassId());
		cv.put("ClassName", t.getClassName());
		cv.put("SectionId", t.getSectionId());
		cv.put("SectionName", t.getSectionName());
		cv.put("SubjectId", t.getSubjectId());
		cv.put("TeacherId", t.getTeacherId());
		cv.put("SelectedDate", t.getSelectedDate());
		cv.put("YesterDate", t.getYesterDate());
		cv.put("OtherDate", t.getOtherDate());
		sqliteDatabase.update("temp", cv, "id=1", null);
	}
	
	public static void updateDate(Temp t, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SelectedDate", t.getSelectedDate());
		cv.put("YesterDate", t.getYesterDate());
		cv.put("OtherDate", t.getOtherDate());
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateClass(Temp t, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("ClassId", t.getClassId());
		cv.put("ClassName", t.getClassName());
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateSection(Temp t, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SectionId", t.getSectionId());
		cv.put("SectionName", t.getSectionName());
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateStudentId(int studentId, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("StudentId", studentId);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateSubjectId(int subjectId, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SubjectId", subjectId);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateSelectedDate(String date, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SelectedDate", date);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateDeviceId(String id, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("DeviceId", id);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateSlipId(long id, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SlipTestId", id);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}
	
	public static void updateTeacherId(int id, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("TeacherId", id);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateSchoolId(int schoolId, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SchoolId", schoolId);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}
	
	public static void updateExamId(int id, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("ExamId", id);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}
	public static void updateExamId2(int id, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("ExamId2", id);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}
	
	public static void updateActivityId(long id, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("ActivityId", id);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}
	public static void updateSubActivityId(long id, SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SubActivityId", id);
		sqliteDatabase.update("temp", cv, "id=1", null);
	}
	
	public static void setThreeAbsDays(int year, int month, int day, SQLiteDatabase sqliteDatabase){
		Temp t = new Temp();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(year, month, day);
		Date today = cal.getTime();
		t.setSelectedDate(dateFormat.format(today));

		cal.add( Calendar.DAY_OF_YEAR, -1);
		Date yesterDate = cal.getTime();
		t.setYesterDate(dateFormat.format(yesterDate));

		cal.add( Calendar.DAY_OF_YEAR, -1);
		Date otherDate = cal.getTime();
		t.setOtherDate(dateFormat.format(otherDate));

		TempDao.updateDate(t, sqliteDatabase);
	}
	
	public static void updateSyncTimer(SQLiteDatabase sqliteDatabase){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		Date today = new Date();
		
		ContentValues cv = new ContentValues();
		cv.put("SyncTime", dateFormat.format(today));
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateSyncComplete(SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SyncTime", "Successfully synced the tablet");
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateSyncFailure(SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SyncTime", "Failed to sync, try again..");
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

	public static void updateSyncProgress(SQLiteDatabase sqliteDatabase){
		ContentValues cv = new ContentValues();
		cv.put("SyncTime", "Sync is in Progress..");
		sqliteDatabase.update("temp", cv, "id=1", null);
	}

}
