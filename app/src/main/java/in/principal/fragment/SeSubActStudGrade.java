package in.principal.fragment;

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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.Capitalize;
import in.principal.adapter.GradeAdapter;
import in.principal.dao.ExamsDao;
import in.principal.dao.SubActivityDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TeacherDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

/**
 * Created by vinkrish.
 */
public class SeSubActStudGrade extends Fragment {
    private Context context;
    private int subActivityId;
    private SQLiteDatabase sqliteDatabase;
    private GradeAdapter gradeAdapter;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private List<Integer> rollNoList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<String> gradeList = new ArrayList<>();
    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.act_stud_grade, container, false);
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        lv = (ListView) view.findViewById(R.id.list);
        Temp t = TempDao.selectTemp(sqliteDatabase);
        int subjectId = t.getSubjectId();
        //	sectionId = t.getSectionId();
        String className = t.getClassName();
        String secName = t.getSectionName();
        int teacherId = t.getTeacherId();
        int examId = t.getExamId();
        //	activityId = t.getActivityId();
        subActivityId = t.getSubActivityId();

        String examName = ExamsDao.selectExamName(examId, sqliteDatabase);

        Button perfClas = (Button) view.findViewById(R.id.seClass);
        perfClas.setText("Class " + className);
        Button perfSe = (Button) view.findViewById(R.id.seSec);
        perfSe.setText("Section " + secName);
        Button SeBut = (Button) view.findViewById(R.id.se);
        SeBut.setText(examName);
        SeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.replace(new SeSubAct(), getFragmentManager());
            }
        });

        Button SeSubActBut = (Button) view.findViewById(R.id.seActSubAct);
        String subact = SubActivityDao.selectSubActivityName(subActivityId, sqliteDatabase);

        if (subact.length() > 20) SeSubActBut.setText(subact.substring(0, 21));
        else SeSubActBut.setText(subact);

        TextView subj = (TextView) view.findViewById(R.id.subinfo);
        subj.setText(SubjectsDao.getSubjectName(subjectId, sqliteDatabase));

        TextView teacher = (TextView) view.findViewById(R.id.teacherinfo);
        teacher.setText(Capitalize.capitalThis(TeacherDao.getTeacherName(teacherId, sqliteDatabase)));

        populateListView();

        return view;
    }

    private void populateListView() {
        Cursor c = sqliteDatabase.rawQuery("select A.StudentId, A.RollNoInClass, A.Name, B.Grade from students A, subactivitygrade B, subactivity C where " +
                "A.StudentId=B.StudentId and B.SubActivityId=" + subActivityId + " and B.SubActivityId=C.SubActivityId order by A.RollNoInClass", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            rollNoList.add(c.getInt(c.getColumnIndex("RollNoInClass")));
            nameList.add(c.getString(c.getColumnIndex("Name")));
            gradeList.add(c.getString(c.getColumnIndex("Grade")));
            c.moveToNext();
        }
        c.close();
        for (int i = 0, j = rollNoList.size(); i < j; i++) {
            amrList.add(new AdapterOverloaded(rollNoList.get(i) + "", nameList.get(i), gradeList.get(i)));
        }
        gradeAdapter = new GradeAdapter(context, R.layout.exam_grade_item, amrList);
        lv.setAdapter(gradeAdapter);
    }
}
