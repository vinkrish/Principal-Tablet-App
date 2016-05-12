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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.StudActAdapter;
import in.principal.dao.ActivitiDao;
import in.principal.dao.ActivityGradeDao;
import in.principal.dao.ActivityMarkDao;
import in.principal.dao.ExamsDao;
import in.principal.dao.GradesClassWiseDao;
import in.principal.dao.SubActivityDao;
import in.principal.dao.SubjectsDao;
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
public class SearchStudAct extends Fragment {
    private int sectionId, subjectId, classId;
    private long examId, studentId;
    private String studentName, className, secName, examName, subjectName;
    private SQLiteDatabase sqliteDatabase;
    private List<Long> actIdList = new ArrayList<>();
    private List<String> actNameList = new ArrayList<>();
    private List<Integer> avgList1 = new ArrayList<>();
    private List<Integer> avgList2 = new ArrayList<>();
    private List<Activiti> activitiList = new ArrayList<>();
    private List<AdapterOverloaded> amrList = new ArrayList<>();
    private List<String> scoreList = new ArrayList<>();
    private StudActAdapter adapter;
    private ProgressDialog pDialog;
    private TextView studTV, clasSecTV;
    private Button examBut, subBut;
    private List<GradesClassWise> gradesClassWiseList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_se_act, container, false);
        Context context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(this.getActivity());

        clearList();

        examBut = (Button) view.findViewById(R.id.examSubButton);
        subBut = (Button) view.findViewById(R.id.examSubActButton);
        studTV = (TextView) view.findViewById(R.id.studName);
        clasSecTV = (TextView) view.findViewById(R.id.studClasSec);
        ListView lv = (ListView) view.findViewById(R.id.list);

        adapter = new StudActAdapter(context, R.layout.search_act_list, amrList);
        lv.setAdapter(adapter);

        view.findViewById(R.id.profile).setOnClickListener(searchProfile);
        view.findViewById(R.id.slipSearch).setOnClickListener(searchSlipTest);
        view.findViewById(R.id.seSearch).setOnClickListener(searchExam);
        view.findViewById(R.id.examButton).setOnClickListener(searchExam);
        view.findViewById(R.id.examSubButton).setOnClickListener(searchExamSub);
        view.findViewById(R.id.attSearch).setOnClickListener(searchAttendance);

        Temp t = TempDao.selectTemp(sqliteDatabase);
        studentId = t.getStudentId();
        classId = t.getClassId();
        examId = t.getExamId();
        subjectId = t.getSubjectId();

        new CalledBackLoad().execute();

        lv.setOnItemClickListener(clickListItem);

        return view;
    }

    private void clearList() {
        actIdList.clear();
        activitiList.clear();
        avgList1.clear();
        avgList2.clear();
        actNameList.clear();
        amrList.clear();
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

    private View.OnClickListener searchExamSub = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudExamSub(), getFragmentManager());
        }
    };

    private View.OnClickListener searchAttendance = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudAtt(), getFragmentManager());
        }
    };

    private AdapterView.OnItemClickListener clickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TempDao.updateActivityId(actIdList.get(position), sqliteDatabase);
            int cache = SubActivityDao.isThereSubAct(actIdList.get(position), sqliteDatabase);
            if (cache == 1) {
                ReplaceFragment.replace(new SearchStudSubAct(), getFragmentManager());
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
            subjectName = SubjectsDao.getSubjectName(subjectId, sqliteDatabase);

            Cursor c = sqliteDatabase.rawQuery("select A.Name, A.ClassId, A.SectionId, B.ClassName, C.SectionName from students A, class B, section C where" +
                    " A.StudentId=" + studentId + " and A.ClassId=B.ClassId and A.SectionId=C.SectionId group by A.StudentId", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                studentName = c.getString(c.getColumnIndex("Name"));
                sectionId = c.getInt(c.getColumnIndex("SectionId"));
                className = c.getString(c.getColumnIndex("ClassName"));
                secName = c.getString(c.getColumnIndex("SectionName"));
                c.moveToNext();
            }
            c.close();

            gradesClassWiseList = GradesClassWiseDao.getGradeClassWise(classId, sqliteDatabase);
            Collections.sort(gradesClassWiseList, new GradeClassWiseSort());

            activitiList = ActivitiDao.selectActiviti(examId, subjectId, sectionId, sqliteDatabase);
            for (Activiti act : activitiList) {
                actNameList.add(act.getActivityName());
                actIdList.add(act.getActivityId());
                Cursor cursor = sqliteDatabase.rawQuery("select StudentId from activitymark where StudentId = " + studentId + " and ActivityId = " + act.getActivityId(), null);
                if (cursor.getCount() > 0) {
                    int avg = ActivityMarkDao.getStudActAvg(studentId, act.getActivityId(), sqliteDatabase);
                    avgList1.add(avg);
                    if (avg == 0) {
                        scoreList.add("-");
                    } else {
                        int score = ActivityMarkDao.getStudActMark(studentId, act.getActivityId(), sqliteDatabase);
                        float maxScore = ActivitiDao.getActivityMaxMark(act.getActivityId(), sqliteDatabase);
                        scoreList.add(score + "/" + maxScore);
                    }
                    avgList2.add(ActivityMarkDao.getSectionAvg(act.getActivityId(), sqliteDatabase));
                } else {
                    Cursor cursor1 = sqliteDatabase.rawQuery("select Grade from activitygrade where StudentId = " + studentId + " and ActivityId = " + act.getActivityId(), null);
                    if (cursor1.getCount() > 0) {
                        cursor1.moveToFirst();
                        while (!cursor1.isAfterLast()) {
                            scoreList.add(cursor1.getString(cursor1.getColumnIndex("Grade")));
                            avgList1.add(getMarkTo(cursor1.getString(cursor1.getColumnIndex("Grade"))));
                            cursor1.moveToNext();
                        }
                        avgList2.add(ActivityGradeDao.getSectionAvg(classId, act.getActivityId(), sqliteDatabase));
                    } else {
                        avgList1.add(0);
                        scoreList.add("-");
                        avgList2.add(0);
                    }
                    cursor1.close();
                }
                cursor.close();
            }

            for (int i = 0; i < actIdList.size(); i++) {
                try {
                    amrList.add(new AdapterOverloaded(actNameList.get(i), scoreList.get(i), avgList1.get(i), avgList2.get(i)));
                } catch (IndexOutOfBoundsException e) {
                    amrList.add(new AdapterOverloaded(actNameList.get(i), 0, 0));
                }
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            examBut.setText(examName);
            subBut.setText(subjectName);
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
