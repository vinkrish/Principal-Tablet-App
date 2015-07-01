package in.principal.dao;

import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StudentAttendanceDao {

	public static int noOfWorkingClassDays(String startDate, String endDate, int classId, SQLiteDatabase sqliteDatabase){
		int i = 0;
		Cursor c = sqliteDatabase.rawQuery("select count(*) as count from (select distinct DateAttendance from studentattendance where " +
				"ClassId="+classId+" and DateAttendance>='"+startDate+"' and DateAttendance<='"+endDate+"' group by DateAttendance)", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			i = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return i;
	}

	public static int noOfWorkingSecDays(String startDate, String endDate, int secId, SQLiteDatabase sqliteDatabase){
		int i = 0;
		Cursor c = sqliteDatabase.rawQuery("select count(*) as count from (select distinct DateAttendance from studentattendance where " +
				"SectionId="+secId+" and DateAttendance>='"+startDate+"' and DateAttendance<='"+endDate+"' group by DateAttendance)", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			i = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return i;
	}

	public static List<Integer> selectStudentAbsent(String date,int secId, SQLiteDatabase sqliteDatabase){
		Cursor c = sqliteDatabase.rawQuery("select * from studentattendance where DateAttendance='"+date+"' and SectionId="+secId+" and TypeOfLeave!='NA'", null);
		List<Integer> sList = new ArrayList<Integer>();
		c.moveToFirst();
		while(!c.isAfterLast()){
			sList.add(c.getInt(c.getColumnIndex("StudentId")));
			c.moveToNext();
		}
		c.close();
		return sList;
	}

	public static int clasAbsentCount(int classId, String date, SQLiteDatabase sqliteDatabase){
		int i=0;
		String sql = "select Count(*) as count from studentattendance where ClassId="+classId+" and DateAttendance='"+date+"' and TypeOfLeave!='NA'";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			i = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return i;
	}

	public static int secAbsentCount(int secId, String date, SQLiteDatabase sqliteDatabase){
		int i=0;
		String sql = "select Count(*) as count from studentattendance where SectionId="+secId+" and DateAttendance='"+date+"' and TypeOfLeave!='NA'";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			i = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return i;
	}

	public static List<String> clasAbsenteeCnt(List<Integer> classIdList, String today, String yesterday, String otherday, SQLiteDatabase sqliteDatabase){
		List<String> absenteeCntList = new ArrayList<String>();
		for(Integer classId: classIdList){
			String sql = "select A.StudentId,B.Name, count(*) as absentee_cnt from studentattendance A,students B where A.DateAttendance in ('"+today+"','"+yesterday+"','"+otherday+"')"+
					" and A.ClassId="+classId+" and A.StudentId!=0 and A.StudentId=B.StudentId group by A.StudentId order by absentee_cnt desc";
			Cursor c = sqliteDatabase.rawQuery(sql, null);
			c.moveToFirst();
			StringBuilder sb = new StringBuilder();
			while(!c.isAfterLast()){
				int cnt = c.getInt(c.getColumnIndex("absentee_cnt"));
				if(cnt==3){
					sb = sb.append(c.getString(c.getColumnIndex("Name"))).append("(3)..");
				}
				c.moveToNext();
			}
			c.close();
			if(sb.toString().equals("")){
				absenteeCntList.add("NA");
			}else if(sb.length()>27){
				absenteeCntList.add(sb.substring(0, 27).toString()+"..");
			}else{
				absenteeCntList.add(sb.toString());
			}
		}
		return absenteeCntList;
	}

	public static List<String> secAbsenteeCnt(List<Integer> secIdList, String today, String yesterday, String otherday, SQLiteDatabase sqliteDatabase){
		List<String> absenteeCntList = new ArrayList<String>();
		for(Integer secId: secIdList){
			String sql = "select A.StudentId,B.Name, count(*) as absentee_cnt from studentattendance A,students B where A.DateAttendance in ('"+today+"','"+yesterday+"','"+otherday+"')"+
					" and A.SectionId="+secId+" and A.StudentId!=0 and A.StudentId=B.StudentId group by A.StudentId order by absentee_cnt desc ";
			Cursor c = sqliteDatabase.rawQuery(sql, null);
			c.moveToFirst();
			StringBuilder sb = new StringBuilder();
			while(!c.isAfterLast()){
				int cnt = c.getInt(c.getColumnIndex("absentee_cnt"));
				if(cnt==3){
					sb = sb.append(c.getString(c.getColumnIndex("Name"))).append("(3)..");
				}
				c.moveToNext();
			}
			c.close();
			if(sb.toString().equals("")){
				absenteeCntList.add("NA");
			}else if(sb.length()>27){
				absenteeCntList.add(sb.substring(0, 27).toString()+"..");
			}else{
				absenteeCntList.add(sb.toString());
			}
		}
		return absenteeCntList;
	}

	public static List<Integer> studMonthlyAttendance(List<String>startDate, List<String>endDate, int studId, SQLiteDatabase sqliteDatabase){
		List<Integer> absList = new ArrayList<Integer>();
		for(int i=0; i<startDate.size(); i++){
			absList.add(StudentAttendanceDao.studMontAbsCnt(startDate.get(i), endDate.get(i), studId, sqliteDatabase));
		}
		return absList;
	}

	public static int studMontAbsCnt(String startDate, String endDate, int studId, SQLiteDatabase sqliteDatabase){
		int cnt = 0;
		String sql = "SELECT count(*) as count FROM studentattendance where DateAttendance>='"+startDate+"' and DateAttendance<='"+endDate+"' and StudentId="+studId;
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			cnt = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return cnt;
	}

	public static int clasDailyAbcCnt(String date, int clasId, SQLiteDatabase sqliteDatabase){
		int cnt = 0;
		String sql = "SELECT count(*) as count FROM studentattendance where DateAttendance='"+date+"' and ClassId="+clasId+" and TypeOfLeave!='NA'";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			cnt = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return cnt;
	}

	public static int clasDailyMarked(String date, int clasId, SQLiteDatabase sqliteDatabase){
		int cnt = -1;
		String sql = "select * from studentattendance where DateAttendance='"+date+"' and ClassId="+clasId+" and TypeOfLeave='NA'";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		if(c.getCount()>0){
			cnt = 0;
		}
		return cnt;
	}

	public static int secDailyMarked(String date, int secId, SQLiteDatabase sqliteDatabase){
		int cnt = -1;
		String sql = "select * from studentattendance where DateAttendance='"+date+"' and SectionId="+secId+" and TypeOfLeave='NA'";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		if(c.getCount()>0){
			cnt = 0;
		}
		return cnt;
	}

	public static List<Integer> clasMonthlyAttendance(List<String>startDate, List<String>endDate, int clasId, SQLiteDatabase sqliteDatabase){
		List<Integer> absList = new ArrayList<Integer>();
		for(int i=0; i<startDate.size(); i++){
			absList.add(StudentAttendanceDao.clasMontAbsCnt(startDate.get(i), endDate.get(i), clasId, sqliteDatabase));
		}
		return absList;
	}

	public static int clasMontAbsCnt(String startDate, String endDate, int clasId, SQLiteDatabase sqliteDatabase){
		int cnt = 0;
		String sql = "SELECT count(*) as count FROM studentattendance where DateAttendance>='"+startDate+"' and DateAttendance<='"+endDate+"' and ClassId="+clasId+" and " +
				"TypeOfLeave!='NA'";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			cnt = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return cnt;
	}

	public static List<String> selClasMontAbs(String startDate, String endDate, int classId, SQLiteDatabase sqliteDatabase){
		List<String> studNameList = new ArrayList<String>();
		List<Integer> studIdList = new ArrayList<Integer>();
		String sql = "SELECT StudentId,count(StudentId) as count FROM studentattendance where DateAttendance<='"+endDate+"' and DateAttendance>='"+startDate+"' and"+
				" ClassId="+classId+" group by StudentId order by count(StudentId) desc";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		if(c.getCount()>0){
			int count = 0;
			while(!c.isAfterLast()){
				if(c.getInt(c.getColumnIndex("count")) >= count){
					count = c.getInt(c.getColumnIndex("count"));
					studIdList.add(c.getInt(c.getColumnIndex("StudentId")));
					c.moveToNext();
				}else{
					c.moveToLast();
					c.moveToNext();
				}
			}
		}else{
			studIdList.add(0);
		}
		c.close();

		for(Integer studId: studIdList){
			String sql1 = "select Name from Students where StudentId="+studId;
			Cursor c1 = sqliteDatabase.rawQuery(sql1, null);
			if(c1.getCount()>0){
				c1.moveToFirst();
				while(!c1.isAfterLast()){
					studNameList.add(c1.getString(c1.getColumnIndex("Name")));
					c1.moveToNext();
				}
			}else{
				studNameList.add("NA");
			}
			c1.close();
		}
		return studNameList;
	}

	public static String clasMontAbsentee(String startDate, String endDate, int classId, double noOfDays, SQLiteDatabase sqliteDatabase){
		List<Integer> studIdList = new ArrayList<Integer>();
		List<Integer> countList = new ArrayList<Integer>();
		List<Integer> perCountList = new ArrayList<Integer>();
		String nameCount = "";
		String s = "";
		boolean flag = false;
		boolean flg = false;
		String sql = "SELECT StudentId,(count(*)/"+noOfDays+")*100 as perCount,count(*) as count FROM studentattendance where DateAttendance<='"+endDate+"' and " +
				"DateAttendance>='"+startDate+"' and ClassId="+classId+" and TypeOfLeave!='NA' group by StudentId order by 2 desc";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		if(c.getCount()>0){
			c.moveToFirst();
			while(!c.isAfterLast()){
				studIdList.add(c.getInt(c.getColumnIndex("StudentId")));
				perCountList.add(c.getInt(c.getColumnIndex("perCount")));
				countList.add(c.getInt(c.getColumnIndex("count")));
				c.moveToNext();
			}
		}else{
			flag = true;
		}
		c.close();
		StringBuilder sb = new StringBuilder();
		if(flag){
			nameCount = "-";
		}else{
			for(int i=0; i<countList.size(); i++){
				if(perCountList.get(i)>20){
					flg = true;
					String sql1 = "select Name from Students where StudentId="+studIdList.get(i);
					Cursor c1 = sqliteDatabase.rawQuery(sql1, null);
					c1.moveToFirst();
					while(!c1.isAfterLast()){
						s = c1.getString(c1.getColumnIndex("Name"));
						c1.moveToNext();
					}
					c1.close();
					sb.append("...").append(s+"("+countList.get(i)+")");
				}
			}
			if(!flg){
				nameCount = "-";
			}else{
				if(sb.length()>33){
					nameCount = sb.substring(3, 33)+"...";
				}else{
					nameCount = sb.substring(3);
				}
			}
		}
		return nameCount;
	}

	public static List<String> secMontAbsentee(String startDate, String endDate, List<Integer> secIdList, double noOfDays, SQLiteDatabase sqliteDatabase){
		List<Integer> studIdList = new ArrayList<Integer>();
		List<Integer> countList = new ArrayList<Integer>();
		List<String> nameCountList = new ArrayList<String>();
		List<Integer> perCountList = new ArrayList<Integer>();
		String s = "";
		boolean flag = false;
		boolean flg = false;
		for(Integer secId: secIdList){
			flag = false;
			flg = false;
			studIdList.clear();
			perCountList.clear();
			countList.clear();
			String sql = "SELECT StudentId,(count(*)/"+noOfDays+")*100 as perCount,count(*) as count FROM studentattendance where DateAttendance<='"+endDate+"' and " +
					"DateAttendance>='"+startDate+"' and SectionId="+secId+" and TypeOfLeave!='NA' group by StudentId order by 2 desc";
			Cursor c = sqliteDatabase.rawQuery(sql, null);
			if(c.getCount()>0){
				c.moveToFirst();
				while(!c.isAfterLast()){
					studIdList.add(c.getInt(c.getColumnIndex("StudentId")));
					perCountList.add(c.getInt(c.getColumnIndex("perCount")));
					countList.add(c.getInt(c.getColumnIndex("count")));
					c.moveToNext();
				}
			}else{
				//	studIdList.add(0);
				flag = true;
			}
			c.close();
			StringBuilder sb = new StringBuilder();
			if(flag){
				nameCountList.add("-");
			}else{
				for(int i=0; i<countList.size(); i++){
					if(perCountList.get(i)>20){
						flg = true;
						String sql1 = "select Name from Students where StudentId="+studIdList.get(i);
						Cursor c1 = sqliteDatabase.rawQuery(sql1, null);
						c1.moveToFirst();
						while(!c1.isAfterLast()){
							s = c1.getString(c1.getColumnIndex("Name"));
							c1.moveToNext();
						}
						c1.close();
						sb.append("...").append(s+"("+countList.get(i)+")");						
					}
				}
				if(!flg){
					nameCountList.add("-");
				}else{
					if(sb.length()>33){
						nameCountList.add(sb.substring(3, 33)+"...");
					}else{
						nameCountList.add(sb.substring(3));
					}
				}
			}
		}
		return nameCountList;
	}

	public static int secMontAbsCnt(String startDate, String endDate, int secId, SQLiteDatabase sqliteDatabase){
		int cnt = 0;
		String sql = "SELECT count(*) as count FROM studentattendance where DateAttendance>='"+startDate+"' and DateAttendance<='"+endDate+"' and SectionId="+secId+" and " +
				"TypeOfLeave!='NA'";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			cnt = c.getInt(c.getColumnIndex("count"));
			c.moveToNext();
		}
		c.close();
		return cnt;
	}

	public static String selectFirstAtt(SQLiteDatabase sqliteDatabase){
		String date = "";
		String sql = "select DateAttendance from studentattendance limit 1";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			date = c.getString(c.getColumnIndex("DateAttendance"));
			c.moveToNext();
		}
		c.close();
		return date;
	}

	public static String selectLastAtt(SQLiteDatabase sqliteDatabase){
		String date = null;
		String sql = "select DateAttendance from studentattendance order by rowid desc limit 1";
		Cursor c = sqliteDatabase.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			date = c.getString(c.getColumnIndex("DateAttendance"));
			c.moveToNext();
		}
		c.close();
		return date;
	}

}
