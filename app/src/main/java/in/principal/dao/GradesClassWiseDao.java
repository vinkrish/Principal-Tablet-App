package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.principal.sqlite.GradesClassWise;
import in.principal.util.GradeClassWiseSort;

public class GradesClassWiseDao {

    public static List<GradesClassWise> getGradeClassWise(int classId, SQLiteDatabase sqliteDatabase){
        List<GradesClassWise> gcwList = new ArrayList<>();
        Cursor c = sqliteDatabase.rawQuery("select * from gradesclasswise where ClassId="+classId, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            GradesClassWise gcw = new GradesClassWise();
            gcw.setClassId(c.getInt(c.getColumnIndex("ClassId")));
            gcw.setGrade(c.getString(c.getColumnIndex("Grade")));
            gcw.setMarkFrom(c.getInt(c.getColumnIndex("MarkFrom")));
            gcw.setMarkTo(c.getInt(c.getColumnIndex("MarkTo")));
            gcw.setGradePoint(c.getInt(c.getColumnIndex("GradePoint")));
            gcwList.add(gcw);
            c.moveToNext();
        }
        c.close();

        Collections.sort(gcwList, new GradeClassWiseSort());

        return gcwList;
    }

}
