package in.principal.dao;

import in.principal.sqlite.SlipTestt;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SlipTesttDao {

    public static List<SlipTestt> selectSlipTest(int sectionId, int subjectId, SQLiteDatabase sqliteDatabase) {
        Cursor c = sqliteDatabase.rawQuery("select * from sliptest where SectionId=" + sectionId + " and SubjectId=" + subjectId, null);
        List<SlipTestt> stList = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            SlipTestt st = new SlipTestt();
            st.setAverageMark(c.getDouble(c.getColumnIndex("AverageMark")));
            st.setClassId(c.getInt(c.getColumnIndex("ClassId")));
            st.setMarkEntered(c.getInt(c.getColumnIndex("MarkEntered")));
            st.setMaximumMark(c.getInt(c.getColumnIndex("MaximumMark")));
            st.setPortion(c.getString(c.getColumnIndex("Portion")));
            st.setExtraPortion(c.getString(c.getColumnIndex("ExtraPortion")));
            st.setPortionName(c.getString(c.getColumnIndex("PortionName")));
            st.setSchoolId(c.getInt(c.getColumnIndex("SchoolId")));
            st.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
            st.setSlipTestId(c.getLong(c.getColumnIndex("SlipTestId")));
            st.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
            st.setTestDate(c.getString(c.getColumnIndex("TestDate")));
            st.setSubmissionDate(c.getString(c.getColumnIndex("SubmissionDate")));
            stList.add(st);
            c.moveToNext();
        }
        c.close();
        return stList;
    }

    public static SlipTestt selectSlipTest(long slipTestId, SQLiteDatabase sqliteDatabase) {
        Cursor c = sqliteDatabase.rawQuery("select * from sliptest where SlipTestId=" + slipTestId, null);
        SlipTestt st = new SlipTestt();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            st.setAverageMark(c.getDouble(c.getColumnIndex("AverageMark")));
            st.setClassId(c.getInt(c.getColumnIndex("ClassId")));
            st.setMarkEntered(c.getInt(c.getColumnIndex("MarkEntered")));
            st.setMaximumMark(c.getInt(c.getColumnIndex("MaximumMark")));
            st.setPortion(c.getString(c.getColumnIndex("Portion")));
            st.setExtraPortion(c.getString(c.getColumnIndex("ExtraPortion")));
            st.setPortionName(c.getString(c.getColumnIndex("PortionName")));
            st.setSchoolId(c.getInt(c.getColumnIndex("SchoolId")));
            st.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
            st.setSlipTestId(c.getLong(c.getColumnIndex("SlipTestId")));
            st.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
            st.setTestDate(c.getString(c.getColumnIndex("TestDate")));
            st.setSubmissionDate(c.getString(c.getColumnIndex("SubmissionDate")));
            c.moveToNext();
        }
        c.close();
        return st;
    }

    public static List<Integer> avgSlipTest(int sectionId, int subjectId, SQLiteDatabase sqliteDatabase) {
        Cursor c = sqliteDatabase.rawQuery("select * from sliptest where SectionId=" + sectionId + " and SubjectId=" + subjectId, null);
        List<Integer> stList = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            stList.add(c.getInt(c.getColumnIndex("SlipTestId")));
            c.moveToNext();
        }
        c.close();
        return stList;
    }

    public static List<SlipTestt> selectSlipTest(SQLiteDatabase sqliteDatabase) {
        Cursor c = sqliteDatabase.rawQuery("SELECT * FROM sliptest ORDER BY SlipTestId ASC LIMIT 1", null);
        List<SlipTestt> stList = new ArrayList<>();
        c.moveToFirst();
        SlipTestt st = new SlipTestt();
        st.setMaximumMark(c.getInt(c.getColumnIndex("MaximumMark")));
        st.setPortion(c.getString(c.getColumnIndex("Portion")));
        st.setExtraPortion(c.getString(c.getColumnIndex("ExtraPortion")));
        st.setPortionName(c.getString(c.getColumnIndex("PortionName")));
        st.setSectionId(c.getInt(c.getColumnIndex("SectionId")));
        st.setSlipTestName(c.getString(c.getColumnIndex("SlipTestName")));
        st.setSubjectId(c.getInt(c.getColumnIndex("SubjectId")));
        st.setTestDate(c.getString(c.getColumnIndex("TestDate")));
        stList.add(st);
        c.close();
        return stList;
    }

    public static String selectSlipTestName(long slipTestId, SQLiteDatabase sqliteDatabase) {
        String s = null;
        Cursor c = sqliteDatabase.rawQuery("select * from sliptest where SlipTestId=" + slipTestId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            s = c.getString(c.getColumnIndex("PortionName"));
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public static double findSlipTestPercentage(int sectionId, int subjectId, SQLiteDatabase sqliteDatabase) {
        double mavg = 0;
        List<SlipTestt> slipTestList = SlipTesttDao.selectSlipTest(sectionId, subjectId, sqliteDatabase);
        int mlen = slipTestList.size();
        for (SlipTestt st : slipTestList) {
            double d = (st.getAverageMark() / (double) st.getMaximumMark());
            mavg += d;
        }
        if (mlen == 0) return 0;
        else return (mavg / Double.parseDouble(mlen + "") * 360);
    }

}
