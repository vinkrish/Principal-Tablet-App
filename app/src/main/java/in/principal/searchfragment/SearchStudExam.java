package in.principal.searchfragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.StudExamAdapter;
import in.principal.dao.TempDao;
import in.principal.fragment.StudentProfile;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

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
public class SearchStudExam extends Fragment {
    private int sectionId, classId;
    private long studentId;
    private String studentName, className, secName;
    private SQLiteDatabase sqliteDatabase;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private StudExamAdapter adapter;
    private List<Integer> examIdList = new ArrayList<>();
    private List<String> examNameList = new ArrayList<>();
    private ProgressDialog pDialog;
    private TextView studTV, clasSecTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_se_exam, container, false);

        Context context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(this.getActivity());

        clearList();

        studTV = (TextView) view.findViewById(R.id.studName);
        clasSecTV = (TextView) view.findViewById(R.id.studClasSec);
        ListView lv = (ListView) view.findViewById(R.id.list);

        adapter = new StudExamAdapter(context, R.layout.search_exam_list, amrList);
        lv.setAdapter(adapter);

        view.findViewById(R.id.profile).setOnClickListener(searchProfile);
        view.findViewById(R.id.slipSearch).setOnClickListener(searchSlipTest);
        view.findViewById(R.id.attSearch).setOnClickListener(searchAttendance);

        Temp t = TempDao.selectTemp(sqliteDatabase);
        studentId = t.getStudentId();

        new CalledBackLoad().execute();

        lv.setOnItemClickListener(clickListItem);

        return view;
    }

    private void clearList() {
        amrList.clear();
        examIdList.clear();
        examNameList.clear();
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

    private View.OnClickListener searchAttendance = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudAtt(), getFragmentManager());
        }
    };

    private OnItemClickListener clickListItem = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TempDao.updateExamId(examIdList.get(position), sqliteDatabase);
            ReplaceFragment.replace(new SearchStudExamSub(), getFragmentManager());
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
                classId = c.getInt(c.getColumnIndex("ClassId"));
                sectionId = c.getInt(c.getColumnIndex("SectionId"));
                className = c.getString(c.getColumnIndex("ClassName"));
                secName = c.getString(c.getColumnIndex("SectionName"));
                c.moveToNext();
            }
            c.close();

            Temp t = new Temp();
            t.setClassId(classId);
            t.setClassName(className);
            TempDao.updateClass(t, sqliteDatabase);

            Cursor c2 = sqliteDatabase.rawQuery("select ExamId,ExamName from exams where ClassId=" + classId, null);
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                examIdList.add(c2.getInt(c2.getColumnIndex("ExamId")));
                examNameList.add(c2.getString(c2.getColumnIndex("ExamName")));
                c2.moveToNext();
            }
            c2.close();

            List<Integer> subIdList = new ArrayList<>();
            Cursor cc = sqliteDatabase.rawQuery("select A.SubjectId from subjectteacher A, subjects B, teacher C where A.SectionId=" + sectionId + " and" +
                    " A.SubjectId=B.SubjectId and A.TeacherId=C.TeacherId", null);
            cc.moveToFirst();
            while (!cc.isAfterLast()) {
                subIdList.add(cc.getInt(cc.getColumnIndex("SubjectId")));
                cc.moveToNext();
            }
            cc.close();

            for (int i = 0; i < examIdList.size(); i++) {
                try {
                    amrList.add(new AdapterOverloaded(examNameList.get(i), ""));
                } catch (IndexOutOfBoundsException e) {
                    amrList.add(new AdapterOverloaded(examNameList.get(i), ""));
                }

            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            studTV.setText(studentName);
            clasSecTV.setText(className + " - " + secName);
            adapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

}
