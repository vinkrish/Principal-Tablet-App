package in.principal.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import in.principal.sqlite.DateTracker;
import in.principal.sqlite.SqlDbHelper;

public class DateTrackerModel {

	public static DateTracker getDateTracker(int month, int year){
		DateTracker dt = new DateTracker();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		int noOfDays = 0;
		Calendar cal = new GregorianCalendar(year, month, 1);
		dt.setSelectedMonth(month);
		dt.setFirstDate(dateFormat.format(cal.getTime()));
		while (cal.get(Calendar.MONTH) == month){
			int day = cal.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SUNDAY) {
			}else{
				noOfDays += 1;
			}
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		dt.setNoOfDays(noOfDays);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		dt.setLastDate(dateFormat.format(cal.getTime()));
		return dt;
	}

	public static DateTracker getDateTracker1(int day1, int day2, int month, int year){
		DateTracker dt = new DateTracker();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		int noOfDays = 0;
		Calendar cal = new GregorianCalendar(year, month, day1);
		dt.setSelectedMonth(month);
		dt.setFirstDate(dateFormat.format(cal.getTime()));
		while (cal.get(Calendar.MONTH) == month && cal.get(Calendar.DAY_OF_MONTH)<=day2){
			int day = cal.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SUNDAY) {
			}else{
				noOfDays += 1;
			}
			cal.add(Calendar.DAY_OF_YEAR, 1);		}
		dt.setNoOfDays(noOfDays);
		cal = new GregorianCalendar(year, month, day2);
		dt.setLastDate(dateFormat.format(cal.getTime()));
		return dt;
	}

	public static DateTracker getDateTracker2(int day2, int month, int year){
		DateTracker dt = new DateTracker();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		int noOfDays = 0;
		Calendar cal = new GregorianCalendar(year, month, day2);
		dt.setSelectedMonth(month);
		dt.setFirstDate(dateFormat.format(cal.getTime()));
		while (cal.get(Calendar.MONTH) == month){
			int day = cal.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SUNDAY) {
			}else{
				noOfDays += 1;
			}
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		dt.setNoOfDays(noOfDays);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		dt.setLastDate(dateFormat.format(cal.getTime()));
		return dt;
	}
	
	public static DateTracker getDateTracker3(int day2, int month, int year){
		DateTracker dt = new DateTracker();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		int noOfDays = 0;
		Calendar cal = new GregorianCalendar(year, month, day2);
		Calendar cal2 = new GregorianCalendar(year, month, 1);
		dt.setSelectedMonth(month);
		dt.setFirstDate(dateFormat.format(cal2.getTime()));
		while (cal.get(Calendar.MONTH) == month){
			int day = cal.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SUNDAY) {
			}else{
				noOfDays += 1;
			}
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		dt.setNoOfDays(noOfDays);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		dt.setLastDate(dateFormat.format(cal.getTime()));
		return dt;
	}

	public static long getNoOfDays(Date start, Date end){
		Calendar c1 = Calendar.getInstance();
		c1.setTime(start);
		int w1 = c1.get(Calendar.DAY_OF_WEEK);
		c1.add(Calendar.DAY_OF_WEEK, -w1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(end);
		int w2 = c2.get(Calendar.DAY_OF_WEEK);
		c2.add(Calendar.DAY_OF_WEEK, -w2);

		long days = (c2.getTimeInMillis()-c1.getTimeInMillis())/(1000*60*60*24);
		long daysWithoutSunday = days-(days*1/7);

		return (daysWithoutSunday-w1+w2+1);
	}

	public static int getTotalStudents(Context context, List<Integer> secIdList){
		int i=0;
		SQLiteDatabase sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
		for(Integer secId: secIdList){
			Cursor c = sqliteDatabase.rawQuery("select count(*) as count from students where SectionId="+secId, null);
			c.moveToFirst();
			i += c.getInt(c.getColumnIndex("count"));
			c.close();
		}
		return i;
	}

	public static int getTotalStudents(Context context){
		int i=0;
		SQLiteDatabase sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
		Cursor c = sqliteDatabase.rawQuery("select count(*) as count from students", null);
		c.moveToFirst();
		i = c.getInt(c.getColumnIndex("count"));
		c.close();
		return i;
	}

	public static int getAbsentCount(Context context, String date){
		int i=0; 
		SQLiteDatabase sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
		Cursor c = sqliteDatabase.rawQuery("select count(*) as count from studentattendance where DateAttendance='"+date+"' and StudentId!=0", null);
		c.moveToFirst();
		i += c.getInt(c.getColumnIndex("count"));
		c.close();
		return i;
	}

}
