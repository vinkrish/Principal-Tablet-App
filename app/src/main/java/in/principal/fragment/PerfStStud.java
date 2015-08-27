package in.principal.fragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.Capitalize;
import in.principal.adapter.PerfStAdapter;
import in.principal.dao.StudentsDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TeacherDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PerfStStud extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perf_st_stud, container, false);
        Context context = AppGlobal.getContext();
        SQLiteDatabase sqliteDatabase = AppGlobal.getSqliteDatabase();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        int schoolId = t.getSchoolId();
        int subjectId = t.getSubjectId();
        int sectionId = t.getSectionId();
        int studentId = t.getStudentId();
        String className = t.getClassName();
        String secName = t.getSectionName();
        int teacherId = t.getTeacherId();

        Button clas = (Button) view.findViewById(R.id.perfClass);
        clas.setText("Class " + className);
        Button sec = (Button) view.findViewById(R.id.perfSec);
        sec.setText("Section " + secName);
        Button stud = (Button) view.findViewById(R.id.perfStStud);
        stud.setText(Capitalize.capitalThis(StudentsDao.getStudentName(studentId, sqliteDatabase)));

        TextView subInfo = (TextView) view.findViewById(R.id.subinfo);
        subInfo.setText(SubjectsDao.getSubjectName(subjectId, sqliteDatabase) + "");
        TextView teacherInfo = (TextView) view.findViewById(R.id.teacherinfo);
        teacherInfo.setText(Capitalize.capitalThis(TeacherDao.getTeacherName(teacherId, sqliteDatabase) + ""));

        List<String> stnameList = new ArrayList<>();
        List<Integer> progressList = new ArrayList<>();
        List<String> markMaxList = new ArrayList<>();

        Cursor c = sqliteDatabase.rawQuery("select C.MaximumMark,B.Mark,C.SlipTestId,C.PortionName, (CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 as avg from students A, sliptestmark_" + schoolId + " B," +
                "sliptest C where B.SubjectId=C.SubjectId and B.SectionId=C.SectionId and B.SlipTestId=C.SlipTestId and C.SubjectId=" + subjectId + " and C.SectionId=" + sectionId + " and A.StudentId=" + studentId + " and " +
                "B.StudentId=" + studentId + " group by C.SlipTestId", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            stnameList.add(c.getString(c.getColumnIndex("PortionName")));
            markMaxList.add(c.getInt(c.getColumnIndex("Mark")) + "/" + c.getInt(c.getColumnIndex("MaximumMark")));
            progressList.add(c.getInt(c.getColumnIndex("avg")));
            c.moveToNext();
        }
        c.close();
        double studAvg = 0;
        for (Integer intt : progressList)
            studAvg += intt;

        int progres = (int) (studAvg / (double) (progressList.size()));
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.stAvgProgress);
        if (progres >= 75)
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
        else if (progres >= 50)
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
        else
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));

        pb.setProgress(progres);
        TextView pecent = (TextView) view.findViewById(R.id.percent);
        pecent.setText(progres + "%");

        ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
        ListView lv = (ListView) view.findViewById(R.id.list);
        for (int i = 0, j = stnameList.size(); i < j; i++)
            amrList.add(new AdapterOverloaded(i + 1 + "", stnameList.get(i), markMaxList.get(i), progressList.get(i)));

        PerfStAdapter amrAdapter = new PerfStAdapter(context, R.layout.p_stud_list, amrList);
        lv.setAdapter(amrAdapter);

        return view;

    }

}
