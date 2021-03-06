package in.principal.examfragment;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.principal.activity.R;
import in.principal.adapter.AssAdapter;
import in.principal.adapter.Capitalize;
import in.principal.dao.ActivitiDao;
import in.principal.dao.ExamsDao;
import in.principal.dao.SectionDao;
import in.principal.dao.SubActivityDao;
import in.principal.dao.SubActivityMarkDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TeacherDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Circle;
import in.principal.sqlite.Section;
import in.principal.sqlite.SubActivity;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

/**
 * Created by vinkrish.
 * Looks like this need to be optimized, good luck with that.
 */
public class SeActSubAct extends Fragment {
    private Context context;
    private SQLiteDatabase sqliteDatabase;
    private String secName;
    private int sectionId, subjectId, progres;
    private long activityId;
    private List<Section> secList = new ArrayList<>();
    private static List<Integer> secIdList = new ArrayList<>();
    private List<Long> subActIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private List<String> subActNameList = new ArrayList<>();
    private List<Integer> avgList = new ArrayList<>();
    private List<SubActivity> subActivitiList = new ArrayList<>();
    private List<AdapterOverloaded> amrList = new ArrayList<>();
    private AssAdapter amrAdapter;
    private CircleAdapter cA;
    private ArrayList<Circle> circleArrayGrid = new ArrayList<>();
    final Map<Object, Object> mi1 = new HashMap<>();
    final Map<Object, Object> mi2 = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.se_subact, container, false);
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        subjectId = t.getSubjectId();
        int classId = t.getClassId();
        sectionId = t.getSectionId();
        String className = t.getClassName();
        secName = t.getSectionName();
        int teacherId = t.getTeacherId();
        long examId = t.getExamId();
        activityId = t.getActivityId();

        String examName = ExamsDao.selectExamName(examId, sqliteDatabase);

        clearList();

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        cA = new CircleAdapter(context, R.layout.section_grid, circleArrayGrid);
        gridView.setAdapter(cA);

        ListView lv = (ListView) view.findViewById(R.id.list);
        amrAdapter = new AssAdapter(context, R.layout.se_act_list, amrList);
        lv.setAdapter(amrAdapter);

        secList = SectionDao.selectSection(classId, sqliteDatabase);
        for (Section s : secList) {
            secIdList.add(s.getSectionId());
            secNameList.add(s.getSectionName());
        }

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

        Button SeActBut = (Button) view.findViewById(R.id.seAct);
        String activiti = ActivitiDao.selectActivityName(activityId, sqliteDatabase);
        if (activiti.length() > 20)
            SeActBut.setText(activiti.substring(0, 21));
        else
            SeActBut.setText(activiti);

        TextView subj = (TextView) view.findViewById(R.id.subinfo);
        subj.setText(SubjectsDao.getSubjectName(subjectId, sqliteDatabase));
        TextView teacher = (TextView) view.findViewById(R.id.teacherinfo);
        teacher.setText(Capitalize.capitalThis(TeacherDao.getTeacherName(teacherId, sqliteDatabase)));

        updateListView();
        updateView();

        ProgressBar pb = (ProgressBar) view.findViewById(R.id.subAvgProgress);
        if (progres >= 75) {
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
        } else if (progres >= 50) {
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
        } else {
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
        }
        pb.setProgress(progres);
        TextView pecent = (TextView) view.findViewById(R.id.percent);
        pecent.setText(progres + "%");

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                TempDao.updateSubActivityId(subActIdList.get(pos), sqliteDatabase);
                Boolean b1 = (Boolean) mi1.get(subActIdList.get(pos));
                Boolean b2 = (Boolean) mi2.get(subActIdList.get(pos));
                if (b1 != null && b1)
                    ReplaceFragment.replace(new SeSubActStud(), getFragmentManager());
                else if (b2 != null && b2)
                    ReplaceFragment.replace(new SeSubActStudGrade(), getFragmentManager());
                else Toast.makeText(context, "Data not entered", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void clearList() {
        amrList.clear();
        subActivitiList.clear();
        subActIdList.clear();
        subActNameList.clear();
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        avgList.clear();
        circleArrayGrid.clear();
    }

	/*private void isItComparable(){
        int[] actCount = new int[secIdList.size()];
		int loop=0;
		for(Integer id: secIdList){
			Cursor c = sqliteDatabase.rawQuery("SELECT count(*) as count from subactivity where ActivityId="+activityId, null);
			c.moveToFirst();
			if(c.getCount()>0){
				actCount[loop] = c.getInt(c.getColumnIndex("count"));
			}else{
				actCount[loop] = 0;
			}
			c.close();		
			loop++;
		}
	}*/

    private void updateView() {
        Circle c = new Circle((int) (progres * 3.6), secName, true);
        circleArrayGrid.add(c);
        cA.notifyDataSetChanged();
    }

    private void updateListView() {
        subActivitiList = SubActivityDao.selectSubActivity(activityId, sqliteDatabase);
        int averag = 0;
        for (SubActivity at : subActivitiList) {
            subActNameList.add(at.getSubActivityName());
            subActIdList.add(at.getSubActivityId());
            //int i = (int) (((double) at.getSubActivityAvg() / (double) 360) * 100);
            avgList.add((int) at.getSubActivityAvg());
            averag += (int) at.getSubActivityAvg();
            int markEntry = SubActivityMarkDao.isThereSubActMark(at.getSubActivityId(), subjectId, sqliteDatabase);
            if (markEntry == 1) mi1.put(at.getSubActivityId(), true);
            int gradeEntry = SubActivityMarkDao.isThereSubActGrade(at.getSubActivityId(), subjectId, sqliteDatabase);
            if (gradeEntry == 1) mi2.put(at.getSubActivityId(), true);
        }

        progres = averag / subActivitiList.size();

        for (int i = 0; i < subActIdList.size(); i++) {
            try {
                amrList.add(new AdapterOverloaded(subActNameList.get(i), avgList.get(i)));
            } catch (IndexOutOfBoundsException e) {
                amrList.add(new AdapterOverloaded(subActNameList.get(i), 0, 0));
            }
        }

        amrAdapter.notifyDataSetChanged();
    }

    public class CircleAdapter extends ArrayAdapter<Circle> {
        private Context context;
        private int layoutResourceId;
        private ArrayList<Circle> data = new ArrayList<>();
        private LayoutInflater inflater = null;

        public CircleAdapter(Context context, int layoutResourceId, ArrayList<Circle> gridArray) {
            super(context, layoutResourceId, gridArray);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.data = gridArray;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            RecordHolder holder;

            if (row == null) {
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new RecordHolder();
                holder.secTxtBlack = (TextView) row.findViewById(R.id.sectionBlack);
                holder.ll = (LinearLayout) row.findViewById(R.id.sec_grid);
                row.setTag(holder);
            } else holder = (RecordHolder) row.getTag();

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            FrameLayout fl = (FrameLayout) row.findViewById(R.id.fl);

            Circle gridItem = data.get(position);
            SampleView sV = new SampleView(context, gridItem.getProgressInt(), gridItem.isSelected());
            holder.secTxtBlack.setText(gridItem.getSec());
            if (gridItem.isSelected()) {
                holder.ll.setActivated(true);
            }
            fl.addView(sV, layoutParams);

            sV.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sectionId = secIdList.get(position);
                    Temp t = new Temp();
                    t.setSectionId(sectionId);
                    t.setSectionName(secNameList.get(position));
                    TempDao.updateSection(t, sqliteDatabase);
                    ReplaceFragment.replace(new SeSubAct(), getFragmentManager());
                }
            });
            return row;
        }

        public class RecordHolder {
            TextView secTxtBlack;
            LinearLayout ll;
        }

        private class SampleView extends View {
            Paint p, defaultPaint, circlePaint;
            RectF rectF;
            int localInt;
            boolean selected;

            public SampleView(Context context, int i, boolean b) {
                super(context);
                setFocusable(true);
                localInt = i;
                selected = b;
                init();
            }

            public void init() {
                circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                circlePaint.setStyle(Paint.Style.FILL);
                circlePaint.setColor(Color.BLACK);
                p = new Paint();
                defaultPaint = new Paint();
                defaultPaint.setAntiAlias(true);
                defaultPaint.setStyle(Paint.Style.STROKE);
                defaultPaint.setStrokeWidth(6);
                Resources res = getResources();
                int defalt = res.getColor(R.color.defalt);
                defaultPaint.setColor(defalt);
                rectF = new RectF(10, 15, 80, 85);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                p.setAntiAlias(true);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(6);
                if (localInt >= 270) {
                    p.setColor(getResources().getColor(R.color.green));
                } else if (localInt >= 180) {
                    p.setColor(getResources().getColor(R.color.orange));
                } else if (localInt > 0) {
                    p.setColor(getResources().getColor(R.color.red));
                }
                canvas.drawArc(rectF, 0, 360, false, defaultPaint);
                canvas.drawArc(rectF, 270, Float.parseFloat(localInt + ""), false, p);
            }
        }

    }
}
