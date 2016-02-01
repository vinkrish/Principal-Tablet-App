package in.principal.fragment;

import in.principal.activity.R;
import in.principal.dao.ExamsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by vinkrish.
 */

@SuppressLint("InflateParams")
public class RepGenComp extends Fragment {
    private Context context;
    private String className, secName, examName, examName2;
    private int classId, sectionId;
    private long examId, examId2;
    private SQLiteDatabase sqliteDatabase;
    private List<Integer> subIdList = new ArrayList<>();
    private List<String> subNameList = new ArrayList<>();

    //	private DecimalFormat df = new DecimalFormat("#.#");
    private Spinner spin1;
    private Spinner spin2;
    private List<Integer> examIdList = new ArrayList<Integer>();
    private List<String> examNameList = new ArrayList<String>();
    private int spinnerCount1, spinnerCount2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_gen_diff, container, false);
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        classId = t.getClassId();
        sectionId = t.getSectionId();
        className = t.getClassName();
        secName = t.getSectionName();
        examId = t.getExamId();
        examId2 = t.getExamId2();

        examName = ExamsDao.selectExamName(examId, sqliteDatabase);
        examName2 = ExamsDao.selectExamName(examId2, sqliteDatabase);

        Button classButton = (Button) view.findViewById(R.id.seClass);
        classButton.setText("Class " + className);
        Button secButton = (Button) view.findViewById(R.id.seSec);
        secButton.setText("Section " + secName);

        spin1 = (Spinner) view.findViewById(R.id.spinner1);
        spin2 = (Spinner) view.findViewById(R.id.spinner2);

        Cursor c = sqliteDatabase.rawQuery("select A.SubjectId, A.TeacherId, B.SubjectName,C.Name from subjectteacher A, subjects B, teacher C where A.SectionId=" + sectionId + " and" +
                " A.SubjectId=B.SubjectId and A.TeacherId=C.TeacherId", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            subIdList.add(c.getInt(c.getColumnIndex("SubjectId")));
            subNameList.add(c.getString(c.getColumnIndex("SubjectName")));
            c.moveToNext();
        }
        c.close();

        Cursor cur = sqliteDatabase.rawQuery("select ExamId,ExamName from exams where ClassId=" + classId, null);
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            examIdList.add(cur.getInt(cur.getColumnIndex("ExamId")));
            examNameList.add(cur.getString(cur.getColumnIndex("ExamName")));
            cur.moveToNext();
        }
        cur.close();

        int examPosition = getPosition();
        spinnerCount1 = 0;

        ArrayAdapter<String> dropExam = new ArrayAdapter<>(context, R.layout.spinner_report, examNameList);
        dropExam.setDropDownViewResource(R.layout.spinner_rep_exp);
        spin1.setAdapter(dropExam);
        spin1.setSelection(examPosition);
        spin1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spinnerCount1++;
                if (spinnerCount1 > 1) {
                    TempDao.updateExamId(examIdList.get(arg2), sqliteDatabase);
                    ReplaceFragment.replace(new ReportGeneration(), getFragmentManager());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        int examPosition2 = getPosition2();
        spinnerCount2 = 0;
        spin2.setAdapter(dropExam);
        spin2.setSelection(examPosition2);
        spin2.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                spinnerCount2++;
                if (spinnerCount2 > 1) {
                    TempDao.updateExamId2(examIdList.get(arg2), sqliteDatabase);
                    ReplaceFragment.replace(new RepGenComp(), getFragmentManager());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        return view;
    }

    private int getPosition() {
        int i = 0;
        for (String s : examNameList) {
            if (examName.equals(s)) {
                break;
            }
            i++;
        }
        return i;
    }

    private int getPosition2() {
        int i = 0;
        for (String s : examNameList) {
            if (examName2.equals(s)) {
                break;
            }
            i++;
        }
        return i;
    }

}
