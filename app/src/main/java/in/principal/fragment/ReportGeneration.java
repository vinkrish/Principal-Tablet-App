package in.principal.fragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.dao.ExamsDao;
import in.principal.dao.TempDao;
import in.principal.examfragment.SeExam;
import in.principal.examfragment.SeExamSub;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by vinkrish.
 */

@SuppressLint("InflateParams")
public class ReportGeneration extends Fragment {
    private Context context;
    private String className, secName, examName;
    private int classId, sectionId;
    private long examId;
    private SQLiteDatabase sqliteDatabase;
    private List<Integer> subIdList = new ArrayList<>();
    private List<String> subNameList = new ArrayList<>();
    //	private DecimalFormat df = new DecimalFormat("#.#");
    private Spinner spin1;
    private Spinner spin2;
    private List<Integer> examIdList = new ArrayList<>();
    private List<String> examNameList = new ArrayList<>();
    private int spinnerCount1;
    private int spinnerCount2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_gen, container, false);
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        classId = t.getClassId();
        sectionId = t.getSectionId();
        className = t.getClassName();
        secName = t.getSectionName();
        examId = t.getExamId();

        examName = ExamsDao.selectExamName(examId, sqliteDatabase);

        Button classButton = (Button) view.findViewById(R.id.seClass);
        classButton.setText("Class " + className);
        classButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.replace(new SeExam(), getFragmentManager());
            }
        });
        Button secButton = (Button) view.findViewById(R.id.seSec);
        secButton.setText("Section " + secName);
        secButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.replace(new SeExamSub(), getFragmentManager());
            }
        });
        Button examButton = (Button) view.findViewById(R.id.exam);
        examButton.setText(examName);
        spin1 = (Spinner) view.findViewById(R.id.spinner1);
        spin2 = (Spinner) view.findViewById(R.id.spinner2);

        Cursor c = sqliteDatabase.rawQuery("select A.SubjectId, A.TeacherId, B.SubjectName,C.Name " +
                "from subjectteacher A, subjects B, teacher C " +
                "where A.SectionId=" + sectionId + " and A.SubjectId=B.SubjectId and A.TeacherId=C.TeacherId", null);
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
        spinnerCount2 = 0;

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
        spin2.setAdapter(dropExam);
        spin2.setSelection(examPosition);
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


}
