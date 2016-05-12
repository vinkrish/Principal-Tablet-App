package in.principal.sliptestfragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.Capitalize;
import in.principal.adapter.StSecSub;
import in.principal.dao.SectionDao;
import in.principal.dao.SlipTesttDao;
import in.principal.dao.StAvgDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TeacherDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.SlipTestt;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by vinkrish.
 * Don't expect comments explaining every piece of code, class and function names are self explanatory.
 */
public class PerfSecSub extends Fragment {
    private SQLiteDatabase sqliteDatabase;
    private int subjectId, sectionId;
    private List<Long> stIdList = new ArrayList<>();
    private List<String> portionIdList = new ArrayList<>();
    private List<String> portionNameList = new ArrayList<>();
    private List<String> dateList = new ArrayList<>();
    private List<Double> avgMarkList = new ArrayList<>();
    private List<Integer> maxMarkList = new ArrayList<>();
    private List<Integer> progressList = new ArrayList<>();
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perf_sec_sub, container, false);
        Context context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        subjectId = t.getSubjectId();
        sectionId = t.getSectionId();
        String className = t.getClassName();
        int teacherId = t.getTeacherId();

        String secName = SectionDao.getSecName(sectionId, sqliteDatabase);

        Button perfClas = (Button) view.findViewById(R.id.perfClass);
        perfClas.setText("Class " + className);
        Button perfSe = (Button) view.findViewById(R.id.perfSec);
        perfSe.setText("Section " + secName);
        TextView subj = (TextView) view.findViewById(R.id.subinfo);
        subj.setText(SubjectsDao.getSubjectName(subjectId, sqliteDatabase));

        TextView teacher = (TextView) view.findViewById(R.id.teacherinfo);
        teacher.setText(Capitalize.capitalThis(TeacherDao.getTeacherName(teacherId, sqliteDatabase)));

        int progres = StAvgDao.selectStAvg(sectionId, subjectId, sqliteDatabase);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.subAvgProgress);
        if (progres >= 75)
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
        else if (progres >= 50)
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
        else
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));

        pb.setProgress(progres);
        TextView pecent = (TextView) view.findViewById(R.id.percent);
        pecent.setText(progres + "%");

        initialize();

        ListView lv = (ListView) view.findViewById(R.id.list);
        amrList.clear();
        for (int i = 0; i < portionIdList.size(); i++)
            amrList.add(new AdapterOverloaded(i + 1 + "", portionNameList.get(i), dateList.get(i), progressList.get(i)));

        StSecSub stsecsubAdapter = new StSecSub(context, R.layout.st_list, amrList);
        lv.setAdapter(stsecsubAdapter);

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                TempDao.updateSlipId(stIdList.get(pos), sqliteDatabase);
                ReplaceFragment.replace(new PerfST(), getFragmentManager());
            }
        });

        return view;
    }

    private void initialize() {
        stIdList.clear();
        dateList.clear();
        portionIdList.clear();
        portionNameList.clear();
        avgMarkList.clear();
        maxMarkList.clear();
        progressList.clear();

        List<SlipTestt> slipTestList = SlipTesttDao.selectSlipTest(sectionId, subjectId, sqliteDatabase);
        for (SlipTestt st : slipTestList) {
            stIdList.add(st.getSlipTestId());
            dateList.add(st.getTestDate());
            portionIdList.add(st.getPortion());
            portionNameList.add(st.getPortionName());
            avgMarkList.add((Double) st.getAverageMark());
            maxMarkList.add(st.getMaximumMark());
        }
        for (int i = 0; i < avgMarkList.size(); i++) {
            double d = (avgMarkList.get(i) / Double.parseDouble(maxMarkList.get(i) + "")) * 100;
            progressList.add((int) d);
        }

    }

}
