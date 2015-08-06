package in.principal.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlDbHelper extends SQLiteOpenHelper implements SqlConstant {
	private static SqlDbHelper dbHelper;
	private SQLiteDatabase sqliteDatabase;

	private SqlDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
	}

	public static SqlDbHelper getInstance(Context context){
		if(dbHelper==null){
			dbHelper = new SqlDbHelper(context.getApplicationContext());
		}
		return dbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CLASS);
		db.execSQL(CREATE_SCHOOL);
		db.execSQL(CREATE_SECTION);
		db.execSQL(CREATE_STUDENTS);
		db.execSQL(CREATE_STUDENT_ATTENDANCE);
		db.execSQL(CREATE_SUBJECTS);
		db.execSQL(CREATE_SUBJECT_TEACHER);
		db.execSQL(CREATE_TEACHER);
		db.execSQL(CREATE_EXAMS);
		db.execSQL(CREATE_MARKS);
		db.execSQL(CREATE_TEMP);
		db.execSQL(CREATE_ACTIVITY);
		db.execSQL(CREATE_ACTIVITY_MARK);
		db.execSQL(CREATE_SUB_ACTIVITY);
		db.execSQL(CREATE_SUB_ACTIVITY_MARK);
		db.execSQL(CREATE_DOWNLOADED_FILE);
		db.execSQL(CREATE_PORTION);
		db.execSQL(CREATE_SLIPTEST);
		db.execSQL(CREATE_HOMEWORK);
		db.execSQL(CREATE_EXMAVG);
		db.execSQL(CREATE_STAVG);
		db.execSQL(CREATE_ACK);
		db.execSQL(CREATE_SUBJECT_EXAMS);
		db.execSQL(CREATE_DATE_TRACKER);
		db.execSQL(CREATE_COMP);
		db.execSQL(CREATE_GCW);
		db.execSQL(CREATE_CCECOSCHOLASTIC);
		db.execSQL(CREATE_CCESECTIONHEADING);
		db.execSQL(CREATE_CCETOPICPRIMARY);
		db.execSQL(CREATE_CCEASPECTPRIMARY);
		db.execSQL(CREATE_CCETOPICGRADE);
		db.execSQL(CREATE_CCECOSCHOLASTICGRADE);
		db.execSQL(CREATE_AVGTRACK);
		db.execSQL(CREATE_LOCKED);
		db.execSQL("insert into temp(id,DeviceId, SchoolId, ClassId, SectionId, ClassName, SectionName, TeacherId, StudentId, SubjectId, "
				+ "ExamId, ExamId2, ActivityId, SubActivityId, SlipTestId, SelectedDate, SyncTime, IsSync) "+
				"values(1,0,0,0,0,0,'A',0,0,0,0,0,0,0,0,'2014-1-1','Not Yet Synced',0)");
		db.execSQL("insert into datetracker(id, FirstDate, LastDate, NoOfDays, SelectedMonth) values(1,'','',0,0)");

		db.execSQL(HOMEWORK_TRIGGER);
		db.execSQL(MARKS_TRIGGER);
		db.execSQL(ACTIVITYMARK_TRIGGER);
		db.execSQL(SUBACTMARK_TRIGGER);
		db.execSQL(CREATE_CCE_STUDENT_PROFILE);
		db.execSQL(VOICE_CALL);
		db.execSQL(TEXT_SMS);
		db.execSQL(CREATE_SUB_GROUPS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS class");
		db.execSQL("DROP TABLE IF EXISTS school");
		db.execSQL("DROP TABLE IF EXISTS section");
		db.execSQL("DROP TABLE IF EXISTS studentattendance");
		db.execSQL("DROP TABLE IF EXISTS students");
		db.execSQL("DROP TABLE IF EXISTS subjectteacher");
		db.execSQL("DROP TABLE IF EXISTS subjects");
		db.execSQL("DROP TABLE IF EXISTS teacher");
		db.execSQL("DROP TABLE IF EXISTS exams");
		db.execSQL("DROP TABLE IF EXISTS marks");
		db.execSQL("DROP TABLE IF EXISTS temp");
		db.execSQL("DROP TABLE IF EXISTS activity");
		db.execSQL("DROP TABLE IF EXISTS activitymark");
		db.execSQL("DROP TABLE IF EXISTS subactivity");
		db.execSQL("DROP TABLE IF EXISTS subactivitymark");
		db.execSQL("DROP TABLE IF EXISTS downloadedfile");
		db.execSQL("DROP TABLE IF EXISTS portion");
		db.execSQL("DROP TABLE IF EXISTS sliptest");
		db.execSQL("DROP TABLE IF EXISTS homeworkmessage");
		db.execSQL("DROP TABLE IF EXISTS exmavg");
		db.execSQL("DROP TABLE IF EXISTS stavg");
		db.execSQL("DROP TABLE IF EXISTS ack");
		db.execSQL("DROP TABLE IF EXISTS subjectexams");
		db.execSQL("DROP TABLE IF EXISTS datetracker");
		db.execSQL("DROP TABLE IF EXISTS comp");
		db.execSQL("DROP TABLE IF EXISTS gradesclasswise");
		db.execSQL("DROP TABLE IF EXISTS ccecoscholastic");
		db.execSQL("DROP TABLE IF EXISTS ccesectionheading");
		db.execSQL("DROP TABLE IF EXISTS ccetopicprimary");
		db.execSQL("DROP TABLE IF EXISTS cceaspectprimary");
		db.execSQL("DROP TABLE IF EXISTS ccetopicgrade");
		db.execSQL("DROP TABLE IF EXISTS ccecoscholasticgrade");
		db.execSQL("DROP TABLE IF EXISTS avgtrack");
		db.execSQL("DROP TABLE IF EXISTS locked");
		db.execSQL("DROP TABLE IF EXISTS ccestudentprofile");
		db.execSQL("DROP TABLE IF EXISTS voicecall");
		db.execSQL("DROP TABLE IF EXISTS textsms");
		db.execSQL("DROP TABLE IF EXISTS subjects_groups");
		onCreate(db);
	}

	public void createIndex(SQLiteDatabase sqliteDatabase){
		sqliteDatabase.execSQL("CREATE INDEX marks_index ON marks(ExamId,SubjectId,StudentId,StudentId,Mark)");
		sqliteDatabase.execSQL("CREATE INDEX actmarks_index ON activitymark(ExamId,SubjectId,ActivityId,StudentId,Mark)");
		sqliteDatabase.execSQL("CREATE INDEX subactmarks_index ON subactivitymark(ExamId,ActivityId,SubjectId,SubActivityId,StudentId,Mark)");
	}

	public void removeIndex(SQLiteDatabase sqliteDatabase){
		sqliteDatabase.execSQL("DROP INDEX IF EXISTS marks_index");
		sqliteDatabase.execSQL("DROP INDEX IF EXISTS actmarks_index");
		sqliteDatabase.execSQL("DROP INDEX IF EXISTS subactmarks_index");
	}

	public void createTrigger(int schoolId, SQLiteDatabase sqliteDatabase){
		sqliteDatabase.execSQL(INSERT_MARK_TRIGGER);
		sqliteDatabase.execSQL(UPDATE_MARK_TRIGGER);
		sqliteDatabase.execSQL(IN_ACTMARK_TRIGGER);
		sqliteDatabase.execSQL(UP_ACTMARK_TRIGGER);
		sqliteDatabase.execSQL(IN_SUBACTMARK_TRIGGER);
		sqliteDatabase.execSQL(UP_SUBACTMARK_TRIGGER);
		sqliteDatabase.execSQL(ATTENDANCE_TRIGGER);
		sqliteDatabase.execSQL(SLIPTEST_TRIGGER);
		sqliteDatabase.execSQL(CSP_TRIGGER);
		sqliteDatabase.execSQL("CREATE TRIGGER insert_stmark BEFORE INSERT ON sliptestmark_"+schoolId+
				" FOR EACH ROW " +
				"BEGIN " +
				"INSERT INTO avgtrack(SubjectId,SectionId,Type) values(NEW.SubjectId, NEW.SectionId, 0); " +
				"END");
		sqliteDatabase.execSQL("CREATE TRIGGER update_stmark BEFORE UPDATE ON sliptestmark_"+schoolId+
				" FOR EACH ROW " +
				"BEGIN " +
				"INSERT INTO avgtrack(SubjectId,SectionId,Type) values(NEW.SubjectId, NEW.SectionId, 1); " +
				"END");

		sqliteDatabase.execSQL("CREATE TRIGGER stmark_trigger BEFORE INSERT ON sliptestmark_"+schoolId+
				" WHEN ((SELECT count() FROM sliptestmark_"+schoolId+" WHERE sliptestmark_"+schoolId+".SlipTestId=NEW.SlipTestId and sliptestmark_"+schoolId+".StudentId=NEW.StudentId)>0) " +
				"BEGIN " +
				"DELETE FROM sliptestmark_"+schoolId+" WHERE sliptestmark_"+schoolId+".SlipTestId=NEW.SlipTestId and sliptestmark_"+schoolId+".StudentId=NEW.StudentId; " +
				"END");
	}

	public void dropTrigger(SQLiteDatabase sqliteDatabase){
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS insert_mark");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS update_mark");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS insert_act_mark");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS update_act_mark");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS insert_subact_mark");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS update_subact_mark");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS insert_stmark");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS update_stmark");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS stmark_trigger");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS before_attendance");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS before_sliptest");
		sqliteDatabase.execSQL("DROP TRIGGER IF EXISTS before_csp");
	}

	public void deleteTable(String tableName, SQLiteDatabase sqliteDatabase){
		//	sqliteDatabase.delete(tableName, null, null);
		sqliteDatabase.execSQL("delete from "+tableName);
	}

	public void deleteDefault(SQLiteDatabase sqliteDatabase){
		sqliteDatabase.execSQL("delete from exams where ExamId=0");
		sqliteDatabase.execSQL("delete from activity where ActivityId=0");
		sqliteDatabase.execSQL("delete from subactivity where SubActivityId=0");
		sqliteDatabase.execSQL("delete from class where ClassId=0");
		sqliteDatabase.execSQL("delete from section where SectionId=0");
		sqliteDatabase.execSQL("delete from students where StudentId=0");
		sqliteDatabase.execSQL("delete from teacher where TeacherId=0");
	}

	public void initStAvg(int classId, int sectionId, int subjectId, int avg){
		sqliteDatabase = dbHelper.getWritableDatabase();
		String sql = "insert into stavg(ClassId, SectionId, SubjectId, SlipTestAvg) values("+classId+","+sectionId+","+
				subjectId+","+avg+")";
		try{
			sqliteDatabase.execSQL(sql);
		}catch(SQLException e){

		}
	}

	public DateTracker selectDateTracker(){
		sqliteDatabase = dbHelper.getReadableDatabase();
		DateTracker dateTracker = new DateTracker();
		Cursor c = sqliteDatabase.rawQuery("select * from datetracker where id = 1", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			dateTracker.setId(c.getInt(c.getColumnIndex("id")));
			dateTracker.setFirstDate(c.getString(c.getColumnIndex("FirstDate")));
			dateTracker.setLastDate(c.getString(c.getColumnIndex("LastDate")));
			dateTracker.setNoOfDays(c.getInt(c.getColumnIndex("NoOfDays")));
			dateTracker.setSelectedMonth(c.getInt(c.getColumnIndex("SelectedMonth")));
			c.moveToNext();
		}
		c.close();
		return dateTracker;
	}

	public void updateDateTracker(DateTracker dt){
		sqliteDatabase = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("FirstDate", dt.getFirstDate());
		cv.put("LastDate", dt.getLastDate());
		cv.put("NoOfDays", dt.getNoOfDays());
		cv.put("SelectedMonth", dt.getSelectedMonth());
		sqliteDatabase.update("datetracker", cv, "id=1", null);
	}

	public void insertComp(int secId){
		sqliteDatabase = dbHelper.getWritableDatabase();
		sqliteDatabase.execSQL("insert into comp(SecId) values("+secId+")");
	}
	public void removeComp(int secId){
		sqliteDatabase = dbHelper.getWritableDatabase();
		sqliteDatabase.execSQL("delete from comp where SecId="+secId);
	}

	public int getHighestGrade(int classId){
		sqliteDatabase = dbHelper.getWritableDatabase();
		int i = 0;
		Cursor c = sqliteDatabase.rawQuery("select GradePoint from gradesclasswise where ClassId="+classId+" Order by GradePoint DESC LIMIT 1", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			i = c.getInt(c.getColumnIndex("GradePoint"));
			c.moveToNext();
		}
		c.close();
		return i;
	}

	public List<GradesClassWise> getGradesClassWise(int classId){
		sqliteDatabase = dbHelper.getWritableDatabase();
		List<GradesClassWise> gradesList = new ArrayList<GradesClassWise>();
		Cursor c = sqliteDatabase.rawQuery("select * from gradesclasswise where ClassId="+classId, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			GradesClassWise gcw = new GradesClassWise();
			gcw.setClassId(c.getInt(c.getColumnIndex("ClassId")));
			gcw.setGrade(c.getString(c.getColumnIndex("Grade")));
			gcw.setGradePoint(c.getInt(c.getColumnIndex("GradePoint")));
			gcw.setMarkFrom(c.getInt(c.getColumnIndex("MarkFrom")));
			gcw.setMarkTo(c.getInt(c.getColumnIndex("MarkTo")));
			gradesList.add(gcw);
			c.moveToNext();
		}
		c.close();
		return gradesList;
	}

	public int isThereCoSchGrade(int aspectId){
		int isThere = 0;
		sqliteDatabase = dbHelper.getReadableDatabase();
		Cursor c = sqliteDatabase.rawQuery("select * from ccecoscholasticgrade where AspectId="+aspectId, null);
		if(c.getCount()>0){
			isThere = 1;
		}
		c.close();
		return isThere;
	}

	public void insertDownloadedFile(String fileName){
		sqliteDatabase = dbHelper.getWritableDatabase();
		try{
			sqliteDatabase.execSQL("insert into downloadedfile(filename) values('"+fileName+"')");
		}catch(SQLException e){}
	}
	
	public void deleteLocked(SQLiteDatabase sqliteDatabase){
		sqliteDatabase.delete("locked", null, null);
	}

	public void deleteTables(SQLiteDatabase sqliteDatabase){
		sqliteDatabase.delete("exmavg", null, null);
		sqliteDatabase.delete("stavg", null, null);
		sqliteDatabase.delete("ack", null, null);
		sqliteDatabase.delete("comp", null, null);
		sqliteDatabase.delete("downloadedfile", null, null);
		sqliteDatabase.delete("activity", null, null);
		sqliteDatabase.delete("activitymark", null, null);
		sqliteDatabase.delete("cceaspectprimary", null, null);
		sqliteDatabase.delete("ccecoscholastic", null, null);
		sqliteDatabase.delete("ccecoscholasticgrade", null, null);
		sqliteDatabase.delete("ccesectionheading", null, null);
		sqliteDatabase.delete("ccetopicgrade", null, null);
		sqliteDatabase.delete("ccetopicprimary", null, null);
		sqliteDatabase.delete("class", null, null);
		sqliteDatabase.delete("exams", null, null);
		sqliteDatabase.delete("homeworkmessage", null, null);
		sqliteDatabase.delete("marks", null, null);
		sqliteDatabase.delete("portion", null, null);
		sqliteDatabase.delete("school", null, null);
		sqliteDatabase.delete("section", null, null);
		sqliteDatabase.delete("sliptest", null, null);
		sqliteDatabase.delete("students", null, null);
		sqliteDatabase.delete("subactivity", null, null);
		sqliteDatabase.delete("subactivitymark", null, null);
		sqliteDatabase.delete("subjectexams", null, null);
		sqliteDatabase.delete("subjects", null, null);
		sqliteDatabase.delete("subjectteacher", null, null);
		sqliteDatabase.delete("teacher", null, null);
		sqliteDatabase.delete("gradesclasswise", null, null);
		sqliteDatabase.delete("studentattendance", null, null);
		sqliteDatabase.delete("ccestudentprofile", null, null);
		sqliteDatabase.delete("voicecall", null, null);
		sqliteDatabase.delete("textsms", null, null);
	}

}
