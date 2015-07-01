package in.principal.fragment;

import in.principal.activity.R;
import in.principal.adapter.Capitalize;
import in.principal.adapter.PerfStAdapter;
import in.principal.dao.ActivitiDao;
import in.principal.dao.ExamsDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TeacherDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.DashObject;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SeActStud extends Fragment {
	private Context context;
	private int good, averag, improve, activityId, maximumMark;
	private SQLiteDatabase sqliteDatabase;
	private TextView scoreTv;
	private ListView lv;
	private List<Integer> rollNoList = new ArrayList<>();
	private List<String> nameList = new ArrayList<>();
	private List<Integer> progressList = new ArrayList<>();
	private List<String> markMaxList = new ArrayList<>();
	private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
	private PerfStAdapter amrAdapter;
	private boolean ascDescFlag;
	private ImageView rollFlag, nameFlag, scoreFlag, avgFlag;
	private ArrayList<DashObject> dashArrayGrid1 = new ArrayList<>();
	private CircleAdapter cA1;
	GridView gridView1;
	private ArrayList<DashObject> dashArrayGrid2 = new ArrayList<>();
	private CircleAdapter2 cA2;
	GridView gridView2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.act_sub_stud, container, false);
		context = AppGlobal.getContext();
		sqliteDatabase = AppGlobal.getSqliteDatabase();

		gridView1 = (GridView) view.findViewById(R.id.gridView1);
		cA1 = new CircleAdapter(context, R.layout.gam_grid, dashArrayGrid1);
		gridView2 = (GridView) view.findViewById(R.id.gridView2);
		cA2 = new CircleAdapter2(context, R.layout.gam_grid, dashArrayGrid2);
		
		clearList();

		TextView rollNoTv = (TextView)view.findViewById(R.id.rollNoSort);
		TextView nameTv = (TextView)view.findViewById(R.id.nameSort);
		scoreTv = (TextView)view.findViewById(R.id.scoreSort);
		TextView avgTv = (TextView)view.findViewById(R.id.avgSort);
		lv = (ListView)view.findViewById(R.id.list);
		
		rollFlag = (ImageView)view.findViewById(R.id.rollFlag);
		nameFlag = (ImageView)view.findViewById(R.id.nameFlag);
		scoreFlag = (ImageView)view.findViewById(R.id.scoreFlag);
		avgFlag = (ImageView)view.findViewById(R.id.avgFlag);
		
		final ObjectAnimator rotateRoll = ObjectAnimator.ofFloat(rollFlag, View.ROTATION, 360);
		rotateRoll.setRepeatCount(1);
		rotateRoll.setRepeatMode(ValueAnimator.REVERSE);
		final ObjectAnimator rotateName = ObjectAnimator.ofFloat(nameFlag, View.ROTATION, 360);
		rotateName.setRepeatCount(1);
		rotateName.setRepeatMode(ValueAnimator.REVERSE);
		final ObjectAnimator rotateScore = ObjectAnimator.ofFloat(scoreFlag, View.ROTATION, 360);
		rotateScore.setRepeatCount(1);
		rotateScore.setRepeatMode(ValueAnimator.REVERSE);
		final ObjectAnimator rotateAvg = ObjectAnimator.ofFloat(avgFlag, View.ROTATION, 360);
		rotateAvg.setRepeatCount(1);
		rotateAvg.setRepeatMode(ValueAnimator.REVERSE);

		Temp t = TempDao.selectTemp(sqliteDatabase);
		int subjectId = t.getSubjectId();
	//	sectionId = t.getSectionId();
		String className = t.getClassName();
		String secName = t.getSectionName();
		int teacherId= t.getTeacherId();
		int examId = t.getExamId();
		activityId = t.getActivityId();

		String examName = ExamsDao.selectExamName(examId, sqliteDatabase);
		
		Button perfClas = (Button)view.findViewById(R.id.seClass);
		perfClas.setText("Class "+className);
		Button perfSe = (Button)view.findViewById(R.id.seSec);
		perfSe.setText("Section "+secName);
		Button SeBut = (Button)view.findViewById(R.id.se);
		SeBut.setText(examName);
		SeBut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReplaceFragment.replace(new SeSubAct(), getFragmentManager());
			}
		});
		
		Button SeActBut = (Button)view.findViewById(R.id.seActSubAct);
		String activiti = ActivitiDao.selectActivityName(activityId, sqliteDatabase);
		if(activiti.length()>20){
			SeActBut.setText(activiti.substring(0, 21));
		}else{
			SeActBut.setText(activiti);
		}
		
		TextView subj = (TextView)view.findViewById(R.id.subinfo);
		subj.setText(SubjectsDao.getSubjectName(subjectId, sqliteDatabase));

		TextView teacher = (TextView)view.findViewById(R.id.teacherinfo);
		teacher.setText(Capitalize.capitalThis(TeacherDao.getTeacherName(teacherId, sqliteDatabase)));
		
		int progres = 0;
		sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
		Cursor c = sqliteDatabase.rawQuery("select ActivityAvg from activity where ActivityId="+activityId, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			progres = (int)(((double)c.getInt(c.getColumnIndex("ActivityAvg"))/(double)360)*100);
			c.moveToNext();
		}
		c.close();

		ProgressBar pb = (ProgressBar)view.findViewById(R.id.subAvgProgress);
		if(progres>=75){
			pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
		}else if(progres>=50){
			pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
		}else{
			pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
		}
		pb.setProgress(progres);
		TextView pecent = (TextView)view.findViewById(R.id.percent);
		pecent.setText(progres+"%");

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				
			}
		});
		
		populateListView();
		
		scoreTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearList();
				rollFlag.setVisibility(View.GONE);
				nameFlag.setVisibility(View.GONE);
				scoreFlag.setVisibility(View.VISIBLE);
				avgFlag.setVisibility(View.GONE);
				rotateScore.start();
				sortAvg();
			}
		});
		avgTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearList();
				rollFlag.setVisibility(View.GONE);
				nameFlag.setVisibility(View.GONE);
				scoreFlag.setVisibility(View.GONE);
				avgFlag.setVisibility(View.VISIBLE);
				rotateAvg.start();
				sortAvg();
			}
		});
		nameTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearList();
				rollFlag.setVisibility(View.GONE);
				nameFlag.setVisibility(View.VISIBLE);
				scoreFlag.setVisibility(View.GONE);
				avgFlag.setVisibility(View.GONE);
				rotateName.start();
				sortName();
			}
		});
		rollNoTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearList();
				rollFlag.setVisibility(View.VISIBLE);
				nameFlag.setVisibility(View.GONE);
				scoreFlag.setVisibility(View.GONE);
				avgFlag.setVisibility(View.GONE);
				rotateRoll.start();
				sortRollNo();
			}
		});
		return view;
	}
	
	public void clearList(){
		dashArrayGrid1.clear();
		dashArrayGrid2.clear();
		amrList.clear();
		rollNoList.clear();
		nameList.clear();
		progressList.clear();
		markMaxList.clear();
		good = 0;
		averag = 0;
		improve = 0;
	}
	
	private void populateListView(){
		sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
		Cursor c = sqliteDatabase.rawQuery("select A.StudentId, A.RollNoInClass, A.Name, B.Mark, C.MaximumMark, case B.Mark when '-1' then 0 " +
				"else (CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, activitymark B, activity C where " +
				"A.StudentId=B.StudentId and B.ActivityId="+activityId+" and B.ActivityId=C.ActivityId order by A.RollNoInClass", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			maximumMark = c.getInt(c.getColumnIndex("MaximumMark"));
			rollNoList.add(c.getInt(c.getColumnIndex("RollNoInClass")));
			nameList.add(c.getString(c.getColumnIndex("Name")));
			markMaxList.add(c.getString(c.getColumnIndex("Mark"))+"");
			progressList.add(c.getInt(c.getColumnIndex("avg")));
			c.moveToNext();
		}
		c.close();
		scoreTv.setText("Score ("+maximumMark+")");

		for(Integer i : progressList){
			if(i>=75){
				good += 1;
			}else if(i>=50){
				averag += 1;
			}else{
				improve += 1;
			}
		}
		
		dashArrayGrid1.add(new DashObject(0, "", ""));
		dashArrayGrid1.add(new DashObject(0, "", ""));
		dashArrayGrid1.add(new DashObject(0, "", ""));
		gridView1.setAdapter(cA1);
		dashArrayGrid2.add(new DashObject(0, good+"", "Good"));
		dashArrayGrid2.add(new DashObject(0, averag+"", "Average"));
		dashArrayGrid2.add(new DashObject(0, improve+"", "Must Improve"));
		gridView2.setAdapter(cA2);

		for(int i=0,j=rollNoList.size(); i<j; i++){
			amrList.add(new AdapterOverloaded(rollNoList.get(i)+"",nameList.get(i),markMaxList.get(i),progressList.get(i)));
		}
		amrAdapter = new PerfStAdapter(context, R.layout.ps_list, amrList);
		lv.setAdapter(amrAdapter);
	}
	
	private void sortRollNo(){
		sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
		Cursor c1 = sqliteDatabase.rawQuery("select A.StudentId, A.RollNoInClass, A.Name, B.Mark, C.MaximumMark, case B.Mark when '-1' then 0 " +
				"else (CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, activitymark B, activity C where " +
				"A.StudentId=B.StudentId and B.ActivityId="+activityId+" and B.ActivityId=C.ActivityId order by A.RollNoInClass", null);
		c1.moveToFirst();
		while(!c1.isAfterLast()){
			rollNoList.add(c1.getInt(c1.getColumnIndex("RollNoInClass")));
			nameList.add(c1.getString(c1.getColumnIndex("Name")));
			markMaxList.add(c1.getString(c1.getColumnIndex("Mark"))+"");
			progressList.add(c1.getInt(c1.getColumnIndex("avg")));
			c1.moveToNext();
		}
		c1.close();
		for(int i=0,j=rollNoList.size(); i<j; i++){
			amrList.add(new AdapterOverloaded(rollNoList.get(i)+"",nameList.get(i),markMaxList.get(i),progressList.get(i)));
		}
		amrAdapter.notifyDataSetChanged();
	}
	
	private void sortName(){
		sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
		Cursor c1 = sqliteDatabase.rawQuery("select A.StudentId, A.RollNoInClass, A.Name, B.Mark, C.MaximumMark, case B.Mark when '-1' then 0 " +
				"else (CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, activitymark B, activity C where " +
				"A.StudentId=B.StudentId and B.ActivityId="+activityId+" and B.ActivityId=C.ActivityId order by A.Name", null);
		c1.moveToFirst();
		while(!c1.isAfterLast()){
			rollNoList.add(c1.getInt(c1.getColumnIndex("RollNoInClass")));
			nameList.add(c1.getString(c1.getColumnIndex("Name")));
			markMaxList.add(c1.getString(c1.getColumnIndex("Mark"))+"");
			progressList.add(c1.getInt(c1.getColumnIndex("avg")));
			c1.moveToNext();
		}
		c1.close();
		for(int i=0,j=rollNoList.size(); i<j; i++){
			amrList.add(new AdapterOverloaded(rollNoList.get(i)+"",nameList.get(i),markMaxList.get(i),progressList.get(i)));
		}
		amrAdapter.notifyDataSetChanged();
	}
	
	private void sortAvg(){
		sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
		Cursor c;
		if(ascDescFlag){
			ascDescFlag = false;
			c = sqliteDatabase.rawQuery("select A.StudentId, A.RollNoInClass, A.Name, B.Mark, C.MaximumMark, case B.Mark when '-1' then 0 " +
				"else (CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, activitymark B, activity C where " +
				"A.StudentId=B.StudentId and B.ActivityId="+activityId+" and B.ActivityId=C.ActivityId order by avg DESC", null);
		}else{
			ascDescFlag = true;
			c = sqliteDatabase.rawQuery("select A.StudentId, A.RollNoInClass, A.Name, B.Mark, C.MaximumMark, case B.Mark when '-1' then 0 " +
				"else (CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, activitymark B, activity C where " +
				"A.StudentId=B.StudentId and B.ActivityId="+activityId+" and B.ActivityId=C.ActivityId order by avg", null);
		}
		
		c.moveToFirst();
		while(!c.isAfterLast()){
			rollNoList.add(c.getInt(c.getColumnIndex("RollNoInClass")));
			nameList.add(c.getString(c.getColumnIndex("Name")));
			markMaxList.add(c.getString(c.getColumnIndex("Mark"))+"");
			progressList.add(c.getInt(c.getColumnIndex("avg")));
			c.moveToNext();
		}
		c.close();
		for(int i=0,j=rollNoList.size(); i<j; i++){
			amrList.add(new AdapterOverloaded(rollNoList.get(i)+"",nameList.get(i),markMaxList.get(i),progressList.get(i)));
		}
		amrAdapter.notifyDataSetChanged();
	}
	
	public class CircleAdapter extends ArrayAdapter<DashObject> {
		Context context;
		int layoutResourceId;
		ArrayList<DashObject>	data = new ArrayList<>();
		protected ListView mListView;

		public CircleAdapter(Context context, int layoutResourceId,ArrayList<DashObject> gridArray) {
			super(context, layoutResourceId, gridArray);
			this.context = context;
			this.layoutResourceId = layoutResourceId;
			this.data = gridArray;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RecordHolder holder ;

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new RecordHolder();
				holder.outOf = (TextView) row.findViewById(R.id.text1);
				holder.str = (TextView) row.findViewById(R.id.text2);
				row.setTag(holder);

			} else {
				holder = (RecordHolder) row.getTag();
			}
			
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			FrameLayout fl = (FrameLayout)row.findViewById(R.id.fl);

			SampleView sV = new SampleView(context, position);
			fl.addView(sV,layoutParams);
			DashObject gridItem = data.get(position);
			holder.outOf.setText(gridItem.getOutOf());
			holder.str.setText(gridItem.getStr());
			return row;
		}

		public class RecordHolder {
			TextView outOf;
			TextView str;
		}

		private class SampleView extends View {
			Paint circlePaint;
			int posi;
			public SampleView(Context context, int pos) {
				super(context);
				setFocusable(true);
				posi = pos;
				init();
			}

			public void init(){
				circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				circlePaint.setStyle(Paint.Style.FILL);
			}

			@Override
			protected void onDraw(Canvas canvas) {
				if(posi==0){
					circlePaint.setColor(getResources().getColor(R.color.green));
					canvas.drawCircle(75, 60, 50, circlePaint);
				}else if(posi==1){
					circlePaint.setColor(getResources().getColor(R.color.orange));
					canvas.drawCircle(75, 60, 50, circlePaint);
				}else if(posi==2){
					circlePaint.setColor(getResources().getColor(R.color.red));
					canvas.drawCircle(75, 60, 50, circlePaint);
				}
			}

		}

	}
	
	public class CircleAdapter2 extends ArrayAdapter<DashObject> {
		private Context context;
        private int layoutResourceId;
        private ArrayList<DashObject> data = new ArrayList<>();
        private LayoutInflater inflater = null;

		public CircleAdapter2(Context context, int layoutResourceId,ArrayList<DashObject> gridArray) {
			super(context, layoutResourceId, gridArray);
			this.context = context;
			this.layoutResourceId = layoutResourceId;
			this.data = gridArray;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RecordHolder holder;

			if (row == null) {
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new RecordHolder();
				holder.outOf = (TextView) row.findViewById(R.id.text1);
				holder.str = (TextView) row.findViewById(R.id.text2);
				row.setTag(holder);

			} else {
				holder = (RecordHolder) row.getTag();
			}
			
			DashObject gridItem = data.get(position);
			holder.outOf.setText(gridItem.getOutOf());
			holder.str.setText(gridItem.getStr());
			return row;
		}

		public class RecordHolder {
			TextView outOf;
			TextView str;
		}

	}

}
