package in.principal.fragment;

import in.principal.activity.R;
import in.principal.dao.CceAspectPrimaryDao;
import in.principal.dao.CceTopicPrimaryDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.Students;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by vinkrish.
 * Don't expect comments explaining every piece of code, class and function names are self explanatory.
 */
public class ViewCCSGrade extends Fragment {
    private SQLiteDatabase sqliteDatabase;
    private int term, topicId, aspectId, sectionId;
    private CoSchAdapter coSchAdapter;
    private ArrayList<CoSch> coSchList = new ArrayList<>();
    private SparseArray<String> map2 = new SparseArray<>();
    private List<Integer> intGradeList = new ArrayList<>();
    private List<String> remarkList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.co_sch_grade, container, false);
        Bundle b = getArguments();
        term = b.getInt("Term");
        topicId = b.getInt("TopicId");
        aspectId = b.getInt("AspectId");

        Context context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        //	schoolId = t.getSchoolId();
        //	classId = t.getClassId();
        sectionId = t.getSectionId();
        String className = t.getClassName();
        String secName = t.getSectionName();

        ListView lv = (ListView) view.findViewById(R.id.list);
        coSchAdapter = new CoSchAdapter(context, R.layout.co_sch_list, coSchList);
        lv.setAdapter(coSchAdapter);

        Button perfClas = (Button) view.findViewById(R.id.seClass);
        perfClas.setText("Class " + className);
        Button perfSe = (Button) view.findViewById(R.id.seSec);
        perfSe.setText("Section " + secName);

        TextView tv_topic = (TextView) view.findViewById(R.id.topic_tv);
        tv_topic.setText("Topic : " + CceTopicPrimaryDao.getTopicName(topicId, sqliteDatabase));
        TextView tv_aspect = (TextView) view.findViewById(R.id.aspect_tv);
        tv_aspect.setText("Aspect : " + CceAspectPrimaryDao.getAspectName(aspectId, sqliteDatabase));

        createListView();

        return view;
    }

    public void createListView() {
        map2.put(0, "");
        Cursor c = sqliteDatabase.rawQuery("select * from ccetopicgrade where TopicId=" + topicId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            map2.put(c.getInt(c.getColumnIndex("Value")), c.getString(c.getColumnIndex("Grade")));
            c.moveToNext();
        }
        c.close();

        List<Students> studentsArray = StudentsDao.selectStudents(sectionId, sqliteDatabase);

        Cursor c1 = sqliteDatabase.rawQuery("select Grade,Description from ccecoscholasticgrade where AspectId=" + aspectId + " and Term=" + term + " and StudentId in " +
                "(select StudentId from students where SectionId=" + sectionId + " order by RollNoInClass)", null);
        c1.moveToFirst();
        while (!c1.isAfterLast()) {
            intGradeList.add(c1.getInt(c1.getColumnIndex("Grade")));
            remarkList.add(c1.getString(c1.getColumnIndex("Description")));
            c1.moveToNext();
        }
        c1.close();

        if (studentsArray.size() == remarkList.size()) {
            int outLoop = 0;
            for (Students stud : studentsArray) {
                coSchList.add(new CoSch(stud.getRollNoInClass() + "", stud.getName(), remarkList.get(outLoop), map2.get(intGradeList.get(outLoop))));
                outLoop += 1;
            }
        } else {
            CommonDialogUtils.displayAlertWhiteDialog(ViewCCSGrade.this.getActivity(), "some student grades are missing, please notify to contact person");
        }

        coSchAdapter.notifyDataSetChanged();
    }

    public class CoSchAdapter extends ArrayAdapter<CoSch> {
        int resource;
        Context context;
        ArrayList<CoSch> data = new ArrayList<>();

        public CoSchAdapter(Context context, int resource, ArrayList<CoSch> listArray) {
            super(context, resource, listArray);
            this.context = context;
            this.resource = resource;
            this.data = listArray;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            RecordHolder holder;

            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(resource, parent, false);

                holder = new RecordHolder();
                holder.tv1 = (TextView) row.findViewById(R.id.roll);
                holder.tv2 = (TextView) row.findViewById(R.id.name);
                holder.tv3 = (TextView) row.findViewById(R.id.remark);
                holder.tv4 = (TextView) row.findViewById(R.id.grade);

                row.setTag(holder);
            } else {
                holder = (RecordHolder) row.getTag();
            }

            if (position % 2 == 0) {
                //	row.setBackgroundResource(R.drawable.list_selector1);
                row.setBackgroundColor(Color.rgb(255, 255, 255));
            } else {
                //	row.setBackgroundResource(R.drawable.list_selector2);
                row.setBackgroundColor(Color.rgb(237, 239, 242));
            }

            CoSch listItem = data.get(position);
            holder.tv1.setText(listItem.getRoll());
            holder.tv2.setText(listItem.getName());
            holder.tv3.setText(listItem.getRemark());
            holder.tv4.setText(listItem.getGrade());

            return row;
        }

        class RecordHolder {
            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;
        }

    }

    public class CoSch {
        private String roll;
        private String name;
        private String remark;
        private String grade;

        public CoSch(String roll, String name, String remark, String grade) {
            this.roll = roll;
            this.name = name;
            this.remark = remark;
            this.grade = grade;
        }

        public String getRoll() {
            return roll;
        }

        public void setRoll(String roll) {
            this.roll = roll;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }
    }

}
