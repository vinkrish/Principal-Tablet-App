package in.principal.searchfragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.StSearchAdapter;
import in.principal.dao.TempDao;
import in.principal.fragment.StudentProfile;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.SqlDbHelper;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by vinkrish.
 * My lawyer told me not to reveal.
 */
public class SearchStudST extends Fragment {
    private Activity act;
    private Context context;
    private int schoolId, sectionId;
    private long studentId;
    private String studentName, className, secName;
    private SQLiteDatabase sqliteDatabase;
    private List<Integer> subIdList = new ArrayList<>();
    private List<String> subNameList = new ArrayList<>();
    private List<String> teacherNameList = new ArrayList<>();
    private List<Integer> progressList = new ArrayList<>();
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private StSearchAdapter amrAdapter;
    private List<Integer> conductedSTList = new ArrayList<>();
    private List<Integer> absentSTList = new ArrayList<>();
    private ProgressDialog pDialog;
    private TextView studTV, clasSecTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_st, container, false);

        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(act);

        clearList();

        ListView lv = (ListView) view.findViewById(R.id.list);
        studTV = (TextView) view.findViewById(R.id.studName);
        clasSecTV = (TextView) view.findViewById(R.id.studClasSec);

        amrAdapter = new StSearchAdapter(context, R.layout.st_search_list, amrList);
        lv.setAdapter(amrAdapter);

        view.findViewById(R.id.profile).setOnClickListener(searchProfile);
        view.findViewById(R.id.attSearch).setOnClickListener(searchAttendance);
        view.findViewById(R.id.seSearch).setOnClickListener(searchExam);

        Temp t = TempDao.selectTemp(sqliteDatabase);
        schoolId = t.getSchoolId();
        studentId = t.getStudentId();

        new CalledBackLoad().execute();

        lv.setOnItemClickListener(clickListItem);

        return view;
    }

    private void clearList() {
        conductedSTList.clear();
        absentSTList.clear();
        amrList.clear();
        subIdList.clear();
        subNameList.clear();
        teacherNameList.clear();
        progressList.clear();
    }

    private View.OnClickListener searchProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new StudentProfile(), getFragmentManager());
        }
    };

    private View.OnClickListener searchAttendance = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudAtt(), getFragmentManager());
        }
    };

    private View.OnClickListener searchExam = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudExam(), getFragmentManager());
        }
    };

    private OnItemClickListener clickListItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TempDao.updateSubjectId(subIdList.get(position), sqliteDatabase);
            ReplaceFragment.replace(new SearchStudSTSub(), getFragmentManager());
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
            sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
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

            Cursor c2 = sqliteDatabase.rawQuery("select E.SubjectName, C.SubjectId, A.Name, AVG((CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100) as avg from" +
                    " teacher A,sliptestmark_" + schoolId + " B, sliptest C, subjectteacher D, subjects E where E.SubjectId=C.SubjectId and B.SlipTestId=C.SlipTestId and B.StudentId=" + studentId +
                    " and B.SubjectId=C.SubjectId and D.SubjectId=C.SubjectId and A.TeacherId=D.TeacherId and D.SectionId=C.SectionId group by C.SubjectId", null);
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                subIdList.add(c2.getInt(c2.getColumnIndex("SubjectId")));
                subNameList.add(c2.getString(c2.getColumnIndex("SubjectName")));
                teacherNameList.add(c2.getString(c2.getColumnIndex("Name")));
                progressList.add(c2.getInt(c2.getColumnIndex("avg")));
                c2.moveToNext();
            }
            c2.close();

            for (Integer subId : subIdList) {
                Cursor c3 = sqliteDatabase.rawQuery("select count(*) as count from sliptest where SectionId=" + sectionId + " and SubjectId=" + subId, null);
                c3.moveToFirst();
                conductedSTList.add(c3.getInt(c3.getColumnIndex("count")));
                c3.close();
                Cursor c4 = sqliteDatabase.rawQuery("select count(*) as count from sliptestmark_" + schoolId + " where SectionId=" + sectionId + " and SubjectId=" + subId +
                        " and StudentId=" + studentId + " and Mark=-1", null);
                c4.moveToFirst();
                absentSTList.add(c4.getInt(c4.getColumnIndex("count")));
                c4.close();
            }


            for (int i = 0; i < subIdList.size(); i++) {
                amrList.add(new AdapterOverloaded(subNameList.get(i), teacherNameList.get(i), conductedSTList.get(i), absentSTList.get(i), progressList.get(i)));
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            studTV.setText(studentName);
            clasSecTV.setText(className + " - " + secName);
            amrAdapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

}
