package in.principal.searchfragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.StudExamSubAdapter;
import in.principal.dao.ActivitiDao;
import in.principal.dao.ExamsDao;
import in.principal.dao.GradesClassWiseDao;
import in.principal.dao.MarksDao;
import in.principal.dao.TempDao;
import in.principal.fragment.StudentProfile;
import in.principal.sqlite.Activiti;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.GradesClassWise;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.GradeClassWiseSort;
import in.principal.util.ReplaceFragment;

/**
 * Created by vinkrish.
 * My lawyer told me not to reveal.
 */
public class SearchStudExamSub extends Fragment {
    private int sectionId, classId;
    private long studentId;
    private long examId;
    private String studentName, className, secName, examName;
    private SQLiteDatabase sqliteDatabase;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private StudExamSubAdapter adapter;
    private ProgressDialog pDialog;
    private TextView studTV, clasSecTV;
    private List<Integer> subIdList = new ArrayList<>();
    private List<String> scoreList = new ArrayList<>();
    private Button examBut;
    private List<GradesClassWise> gradesClassWiseList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_se_exam_sub, container, false);
        Context context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(this.getActivity());

        clearList();

        examBut = (Button) view.findViewById(R.id.examSubButton);
        ListView lv = (ListView) view.findViewById(R.id.list);
        studTV = (TextView) view.findViewById(R.id.studName);
        clasSecTV = (TextView) view.findViewById(R.id.studClasSec);

        adapter = new StudExamSubAdapter(context, R.layout.search_exam_sub_list, amrList);
        lv.setAdapter(adapter);

        view.findViewById(R.id.profile).setOnClickListener(searchProfile);
        view.findViewById(R.id.slipSearch).setOnClickListener(searchSlipTest);
        view.findViewById(R.id.seSearch).setOnClickListener(searchExam);
        view.findViewById(R.id.examButton).setOnClickListener(searchExam);
        view.findViewById(R.id.attSearch).setOnClickListener(searchAttendance);

        Temp t = TempDao.selectTemp(sqliteDatabase);
        studentId = t.getStudentId();
        classId = t.getClassId();
        examId = t.getExamId();
        sectionId = t.getSectionId();

        new CalledBackLoad().execute();

        lv.setOnItemClickListener(clickListItem);

        return view;
    }

    private void clearList() {
        amrList.clear();
        subIdList.clear();
        scoreList.clear();
    }

    private View.OnClickListener searchProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new StudentProfile(), getFragmentManager());
        }
    };

    private View.OnClickListener searchSlipTest = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudST(), getFragmentManager());
        }
    };

    private View.OnClickListener searchExam = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudExam(), getFragmentManager());
        }
    };

    private View.OnClickListener searchAttendance = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudAtt(), getFragmentManager());
        }
    };

    private OnItemClickListener clickListItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TempDao.updateSubjectId(subIdList.get(position), sqliteDatabase);
            int cache = ActivitiDao.isThereActivity(sectionId, subIdList.get(position), examId, sqliteDatabase);
            if (cache == 1) {
                ReplaceFragment.replace(new SearchStudAct(), getFragmentManager());
            }
        }
    };

    class CalledBackLoad extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Preparing data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            examName = ExamsDao.selectExamName(examId, sqliteDatabase);

            Cursor c = sqliteDatabase.rawQuery("select A.Name, A.ClassId, A.SectionId, B.ClassName, C.SectionName from students A, class B, section C where" +
                    " A.StudentId=" + studentId + " and A.ClassId=B.ClassId and A.SectionId=C.SectionId group by A.StudentId", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                studentName = c.getString(c.getColumnIndex("Name"));
                //	classId = c.getInt(c.getColumnIndex("ClassId"));
                sectionId = c.getInt(c.getColumnIndex("SectionId"));
                className = c.getString(c.getColumnIndex("ClassName"));
                secName = c.getString(c.getColumnIndex("SectionName"));
                c.moveToNext();
            }
            c.close();

            gradesClassWiseList = GradesClassWiseDao.getGradeClassWise(classId, sqliteDatabase);
            Collections.sort(gradesClassWiseList, new GradeClassWiseSort());

            List<String> subNameList = new ArrayList<>();
            List<String> teacherNameList = new ArrayList<>();
            List<Integer> progressList1 = new ArrayList<>();
            List<Integer> progressList2 = new ArrayList<>();

            Cursor c2 = sqliteDatabase.rawQuery("select A.SubjectId, A.TeacherId, B.SubjectName,C.Name from subjectteacher A, subjects B, teacher C where A.SectionId=" + sectionId + " and" +
                    " A.SubjectId=B.SubjectId and A.TeacherId=C.TeacherId", null);
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                subIdList.add(c2.getInt(c2.getColumnIndex("SubjectId")));
                subNameList.add(c2.getString(c2.getColumnIndex("SubjectName")));
                teacherNameList.add(c2.getString(c2.getColumnIndex("Name")));
                c2.moveToNext();
            }
            c2.close();

            for (Integer sub : subIdList) {
                int avg;
                int sectionAvg = MarksDao.getSectionAvg(examId, sub, sectionId, sqliteDatabase);
                Cursor cursor = sqliteDatabase.rawQuery("select Mark from marks where ExamId=" + examId + " and SubjectId=" + sub + " and StudentId=" + studentId, null);
                cursor.moveToFirst();
                if (cursor.getCount() > 0 && sectionAvg != 0) {
                    avg = MarksDao.getStudExamAvg(studentId, sub, examId, sqliteDatabase);
                    int score = MarksDao.getStudExamMark(studentId, sub, examId, sqliteDatabase);
                    int maxScore = MarksDao.getExamMaxMark(sub, examId, sqliteDatabase);
                    scoreList.add(score + "/" + maxScore);
                    progressList1.add(avg);
                    progressList2.add(sectionAvg);
                } else {
                    Cursor cursor1 = sqliteDatabase.rawQuery("select Grade from marks where ExamId=" + examId + " and SubjectId=" + sub + " and StudentId=" + studentId, null);
                    cursor1.moveToFirst();
                    try {
                        if (cursor1.getCount() > 0 && !cursor1.getString(cursor1.getColumnIndex("Grade")).equals("")) {
                            while (!cursor1.isAfterLast()) {
                                scoreList.add(cursor1.getString(cursor1.getColumnIndex("Grade")));
                                progressList1.add(getMarkTo(cursor1.getString(cursor1.getColumnIndex("Grade"))));
                                cursor1.moveToNext();
                            }
                        } else{
                            progressList1.add(0);
                            scoreList.add("-");
                            progressList2.add(0);
                        }
                        progressList2.add(MarksDao.getSectionAvg(classId, sectionId, sub, examId, sqliteDatabase));
                    } catch (NullPointerException e) {
                        progressList1.add(0);
                        scoreList.add("-");
                        progressList2.add(MarksDao.getSectionAvg(classId, sectionId, sub, examId, sqliteDatabase));
                        e.printStackTrace();
                    }
                    cursor1.close();
                }
                cursor.close();
            }

            for (int i = 0; i < subIdList.size(); i++) {
                amrList.add(new AdapterOverloaded(subNameList.get(i), teacherNameList.get(i), scoreList.get(i), progressList1.get(i), progressList2.get(i)));
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            examBut.setText(examName);
            studTV.setText(studentName);
            clasSecTV.setText(className + " - " + secName);
            adapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

    private int getMarkTo(String grade) {
        int markTo = 0;
        for (GradesClassWise gcw : gradesClassWiseList) {
            if (grade.equals(gcw.getGrade())) {
                markTo = gcw.getMarkTo();
                break;
            }
        }
        return markTo;
    }

}
