package in.principal.fragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.SearchStAdapter;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SearchStudSTSub extends Fragment {
    private Activity act;
    private Context context;
    private int schoolId, studentId, subjectId, progres;
    private String studentName, className, secName;
    private SQLiteDatabase sqliteDatabase;
    private SearchStAdapter searchAdapter;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();
    private List<String> stNameList = new ArrayList<>();
    private List<Integer> maxMarkList = new ArrayList<>();
    private List<Integer> markList = new ArrayList<>();
    private ProgressDialog pDialog;
    private TextView studTV, clasSecTV, pecent;
    private ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_st_sub, container, false);

        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(act);

        clearList();

        ListView lv = (ListView) view.findViewById(R.id.list);
        studTV = (TextView) view.findViewById(R.id.studName);
        clasSecTV = (TextView) view.findViewById(R.id.studClasSec);
        pb = (ProgressBar) view.findViewById(R.id.subAvgProgress);
        pecent = (TextView) view.findViewById(R.id.percent);

        view.findViewById(R.id.slipSearch).setOnClickListener(searchSlipTest);
        view.findViewById(R.id.seSearch).setOnClickListener(searchExam);
        view.findViewById(R.id.attSearch).setOnClickListener(searchAttendance);

        Temp t = TempDao.selectTemp(sqliteDatabase);
        schoolId = t.getSchoolId();
        studentId = t.getStudentId();
        subjectId = t.getSubjectId();

        String subjectName = SubjectsDao.getSubjectName(subjectId, sqliteDatabase);
        Button subTV = (Button) view.findViewById(R.id.stSub);
        subTV.setText(subjectName);

        searchAdapter = new SearchStAdapter(context, R.layout.search_st_list, amrList);
        lv.setAdapter(searchAdapter);

        new CalledBackLoad().execute();

        return view;
    }

    private void clearList() {
        amrList.clear();
        dateList.clear();
        stNameList.clear();
        maxMarkList.clear();
        markList.clear();
    }

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
            Cursor c = sqliteDatabase.rawQuery("select A.Name, A.ClassId, A.SectionId, B.ClassName, C.SectionName from students A, class B, section C where" +
                    " A.StudentId=" + studentId + " and A.ClassId=B.ClassId and A.SectionId=C.SectionId group by A.StudentId", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                studentName = c.getString(c.getColumnIndex("Name"));
                className = c.getString(c.getColumnIndex("ClassName"));
                secName = c.getString(c.getColumnIndex("SectionName"));
                c.moveToNext();
            }
            c.close();

            Cursor c2 = sqliteDatabase.rawQuery("select  C.SlipTestId, C.TestDate, C.PortionName, C.MaximumMark, B.Mark from students A, sliptestmark_" + schoolId + " B," +
                    " sliptest C where B.SubjectId=C.SubjectId and B.SectionId=C.SectionId and B.SlipTestId=C.SlipTestId and C.SubjectId=" + subjectId + " and" +
                    " A.StudentId=" + studentId + " and B.StudentId=" + studentId + " group by C.SlipTestId", null);
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                stNameList.add(c2.getString(c2.getColumnIndex("PortionName")));
                dateList.add(c2.getString(c2.getColumnIndex("TestDate")));
                maxMarkList.add(c2.getInt(c2.getColumnIndex("MaximumMark")));
                markList.add(c2.getInt(c2.getColumnIndex("Mark")));
                c2.moveToNext();
            }
            c2.close();

            for (int i = 0; i < stNameList.size(); i++) {
                amrList.add(new AdapterOverloaded(stNameList.get(i), dateList.get(i), i + 1, maxMarkList.get(i), markList.get(i)));
            }

            Cursor c3 = sqliteDatabase.rawQuery("select C.SubjectId, AVG((CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100) as avg from sliptestmark_" + schoolId + " B," +
                    " sliptest C where B.SlipTestId=C.SlipTestId and B.StudentId=" + studentId + " and B.SubjectId=C.SubjectId and C.SubjectId=" + subjectId + " group by C.SubjectId", null);
            c3.moveToFirst();
            while (!c3.isAfterLast()) {
                progres = c3.getInt(c3.getColumnIndex("avg"));
                c3.moveToNext();
            }
            c3.close();
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            studTV.setText(studentName);
            clasSecTV.setText(className + " - " + secName);
            if (progres >= 75) {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
            } else if (progres >= 50) {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
            } else {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
            }
            pb.setProgress(progres);
            pecent.setText(progres + "%");
            searchAdapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

}