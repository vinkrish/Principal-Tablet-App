package in.principal.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.Alert;
import in.principal.adapter.AsecAdapter;
import in.principal.dao.SectionDao;
import in.principal.dao.StudentAttendanceDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Section;
import in.principal.sqlite.Students;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AttendanceSection extends Fragment {
	private static Context context;
	private static Activity act;
	private static SQLiteDatabase sqliteDatabase;
	private AlertDialog alertDialog;
	private static String dateSelected;
	private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
	private List<Section> secList = new ArrayList<>();
	private List<Integer> secIdList = new ArrayList<>();
	private List<String> secNameList = new ArrayList<>();
	private int index;
	private String[] items;
	private List<Integer> absentList = new ArrayList<>();
	private List<Integer> studentIdList = new ArrayList<>();
	private List<Integer> studIDList = new ArrayList<>();
	private List<String> studentNameList = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.attendance_section, container, false);
		act = AppGlobal.getActivity();
		context = AppGlobal.getContext();
		sqliteDatabase = AppGlobal.getSqliteDatabase();

		clearList();

		RelativeLayout ll = (RelativeLayout)view.findViewById(R.id.datePicker);

		ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});

		Temp t = TempDao.selectTemp(sqliteDatabase);
		int classId = t.getClassId();
		String className = t.getClassName();
		int sectionId = t.getSectionId();
		String sectionName = t.getSectionName();
		dateSelected = t.getSelectedDate();

		TextView date = (TextView) view.findViewById(R.id.dat);
		date.setText(dateSelected);

		secList = SectionDao.selectSection(classId, sqliteDatabase);
		for(Section s: secList){
			secIdList.add(s.getSectionId());
			secNameList.add(s.getSectionName());
		}

		items = secNameList.toArray(new String[secNameList.size()]);
		LinearLayout selecClass = (LinearLayout)view.findViewById(R.id.selectSection);
		selecClass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setTitle("Select Section");
				builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						index = which;
						alertDialog.dismiss();
						Temp t = new Temp();
						t.setSectionId(secIdList.get(index));
						t.setSectionName(secNameList.get(index));
						TempDao.updateSection(t, sqliteDatabase);
						ReplaceFragment.replace(new AttendanceSection(), getFragmentManager());
					}
				});
				alertDialog = builder.create();
				alertDialog.show();
			}
		});

		Button classBC = (Button)view.findViewById(R.id.classButton);
		classBC.setText("Class "+className);
		Button secBC = (Button)view.findViewById(R.id.sectionButton);
		secBC.setText("Section "+sectionName);

		TextView clasSecNam = (TextView)view.findViewById(R.id.clasecName);
		clasSecNam.setText(className+" - "+sectionName);

		int sectionProgress = findSectionAttendance(context, sectionId, dateSelected);
		ProgressBar pb = (ProgressBar)view.findViewById(R.id.secAvgProgress);
		if(sectionProgress>=75){
			pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
		}else if(sectionProgress>=50){
			pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
		}else{
			pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
		}
		pb.setProgress(sectionProgress);

		TextView sectionPercent = (TextView)view.findViewById(R.id.sectionpercent);
		sectionPercent.setText(sectionProgress+"%");

		absentList = StudentAttendanceDao.selectStudentAbsent(dateSelected, sectionId, sqliteDatabase);
		List<Students> studentList = StudentsDao.selectAbsentStudents(absentList, sqliteDatabase);
		for(Students s: studentList){
			studIDList.add(s.getStudentId());
			studentIdList.add(s.getRollNoInClass());
			studentNameList.add(s.getName());
		}

		ListView lv = (ListView)view.findViewById(R.id.list);
		populateList();
		AsecAdapter asecAdapter = new AsecAdapter(context, R.layout.asec_list, amrList);
		lv.setAdapter(asecAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				TempDao.updateStudentId(studIDList.get(pos), sqliteDatabase);
				ReplaceFragment.replace(new AttendanceStudent(), getFragmentManager());
			}
		});

		return view;
	}

	private int findSectionAttendance(Context context, int sectionId, String date){
		int sectionProgress = 0;
		double absentCount = 0;
		double totalStrength = StudentsDao.secTotalStrength(sectionId, sqliteDatabase);
		absentCount = StudentAttendanceDao.secAbsentCount(sectionId, date, sqliteDatabase);
		if(absentCount==0){
			absentCount = StudentAttendanceDao.secDailyMarked(date, sectionId, sqliteDatabase);
			if(absentCount==-1){
				absentCount = totalStrength;
			}
		}
		double progress = ((totalStrength-absentCount)/totalStrength)*100;
		sectionProgress = (int)progress;
		return sectionProgress;
	}

	private void clearList(){
		amrList.clear();
		secList.clear();
		secIdList.clear();
		secNameList.clear();
		studentIdList.clear();
		studentNameList.clear();
		absentList.clear();
		studIDList.clear();
	}

	private void populateList(){
		amrList.clear();
		for(int i=0; i<absentList.size(); i++){
			amrList.add(new AdapterOverloaded(studentIdList.get(i)+"",studentNameList.get(i)));
		}
	}

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {        
			String csvSplitBy = "-";
			String[] data = dateSelected.split(csvSplitBy);
			int year = Integer.parseInt(data[0]);
			int month = Integer.parseInt(data[1])-1;
			int day = Integer.parseInt(data[2]);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			if(view.isShown()){
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(year,month,day);
				Alert alert = new Alert(act);
				if(GregorianCalendar.getInstance().get(Calendar.YEAR)<cal.get(Calendar.YEAR)){
					alert.showAlert("Selected future date !");
				}else if(GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)<cal.get(Calendar.DAY_OF_MONTH) && 
						GregorianCalendar.getInstance().get(Calendar.MONTH)<=cal.get(Calendar.MONTH) && GregorianCalendar.getInstance().get(Calendar.YEAR)==cal.get(Calendar.YEAR)){
					alert.showAlert("Selected future date !");
				}else if(Calendar.SUNDAY==cal.get(Calendar.DAY_OF_WEEK)){
					alert.showAlert("Sundays are not working days.");	
				}else{
					TempDao.setThreeAbsDays(year, month, day, sqliteDatabase);
					ReplaceFragment.replace(new AttendanceSection(), getFragmentManager());
				}
			}
		}
	}
}
