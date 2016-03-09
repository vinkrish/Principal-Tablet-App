package in.principal.fragment;

import in.principal.activity.R;
import in.principal.activity.R.animator;
import in.principal.dao.ClasDao;
import in.principal.dao.SectionDao;
import in.principal.dao.StudentsDao;
import in.principal.sqlite.Clas;
import in.principal.sqlite.Section;
import in.principal.sqlite.Students;
import in.principal.util.AppGlobal;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by vinkrish.
 */
public class VoiceSms extends Fragment {
    private SQLiteDatabase sqliteDatabase;
    private Button allStudentsBtn, allTeachersBtn, classBtn, sectionBtn, studentBtn, submitBtn;
    private FrameLayout allStudentsFrame, allTeachersFrame;
    private LinearLayout selectionFrame;
    private EditText classSpinner, sectionSpinner, studentSpinner;
    private int classId, sectionId;
    private ArrayList<Clas> clasList;
    private ArrayList<Section> secList;
    private ArrayList<Integer> classIdList;
    private ArrayList<Integer> secIdList;
    private ArrayList<Long> studIdList;
    private ArrayList<String> classNameList;
    private ArrayList<String> secNameList;
    private ArrayList<String> studNameList;

    protected boolean[] classSelections;
    protected boolean[] sectionSelections;
    protected boolean[] studentSelections;

    private String ids;
    private Bundle b;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voice_sms, container, false);

        initializeList();

        sqliteDatabase = AppGlobal.getSqliteDatabase();

        allStudentsBtn = (Button) view.findViewById(R.id.allStudents);
        allTeachersBtn = (Button) view.findViewById(R.id.allTeachers);
        classBtn = (Button) view.findViewById(R.id.clas);
        sectionBtn = (Button) view.findViewById(R.id.sec);
        studentBtn = (Button) view.findViewById(R.id.stud);
        submitBtn = (Button) view.findViewById(R.id.submit);

        allStudentsFrame = (FrameLayout) view.findViewById(R.id.allStudentsFrame);
        allTeachersFrame = (FrameLayout) view.findViewById(R.id.allTeachersFrame);
        selectionFrame = (LinearLayout) view.findViewById(R.id.selectionFrame);

        classSpinner = (EditText) view.findViewById(R.id.classSpinner);
        ;
        sectionSpinner = (EditText) view.findViewById(R.id.secSpinner);
        studentSpinner = (EditText) view.findViewById(R.id.studSpinner);

        classSpinner.setOnTouchListener(classTouch);
        sectionSpinner.setOnTouchListener(sectionTouch);
        studentSpinner.setOnTouchListener(studentTouch);

        clasList = ClasDao.selectClas(sqliteDatabase);
        for (Clas c : clasList) {
            classIdList.add(c.getClassId());
            classNameList.add(c.getClassName());
        }
        classSelections = new boolean[clasList.size()];

        allStudentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                allStudentsFrame.setVisibility(View.VISIBLE);
                b.putInt("key", 0);
            }
        });

        allTeachersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                allTeachersFrame.setVisibility(View.VISIBLE);
                b.putInt("key", 1);
            }
        });

        classBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                selectionFrame.setVisibility(View.VISIBLE);
                sectionSpinner.setVisibility(View.INVISIBLE);
                studentSpinner.setVisibility(View.INVISIBLE);
                classSpinner.setText("");
                b.putInt("key", 2);
            }
        });

        sectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                selectionFrame.setVisibility(View.VISIBLE);
                sectionSpinner.setVisibility(View.VISIBLE);
                studentSpinner.setVisibility(View.INVISIBLE);
                classSpinner.setText("");
                sectionSpinner.setText("");
                for (int i = 0; i < classSelections.length; i++) {
                    classSelections[i] = false;
                }
                secList.clear();
                secIdList.clear();
                secNameList.clear();
                sectionSelections = new boolean[secIdList.size()];
                b.putInt("key", 3);
            }
        });

        studentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                selectionFrame.setVisibility(View.VISIBLE);
                sectionSpinner.setVisibility(View.VISIBLE);
                studentSpinner.setVisibility(View.VISIBLE);
                classSpinner.setText("");
                sectionSpinner.setText("");
                studentSpinner.setText("");
                for (int i = 0; i < classSelections.length; i++) {
                    classSelections[i] = false;
                }
                secList.clear();
                secIdList.clear();
                secNameList.clear();
                sectionSelections = new boolean[secIdList.size()];
                studIdList.clear();
                studNameList.clear();
                studentSelections = new boolean[studIdList.size()];
                b.putInt("key", 4);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.putString("ids", ids);
                Fragment fragment = new UploadVoiceSms();
                fragment.setArguments(b);
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(animator.fade_in, animator.fade_out)
                        .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            }
        });

		/*classSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
			public boolean onTouch(View v, MotionEvent event) {
				int inType = classSpinner.getInputType(); // backup the input type
				classSpinner.setInputType(InputType.TYPE_NULL); // disable soft input
				classSpinner.onTouchEvent(event); // call native handler
				classSpinner.setInputType(inType); // restore input type
				return false;
			}
		});*/

        classSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ClassDialog classDialog = new ClassDialog();
                classDialog.show(ft, "dialog");
            }
        });

        sectionSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                SectionDialog secFragment = new SectionDialog();
                secFragment.show(ft, "sectionDialog");
            }
        });

        studentSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                StudentDialog studFragment = new StudentDialog();
                studFragment.show(ft, "studentdialog");
            }
        });

        return view;
    }

    private void initializeList() {
        b = new Bundle();
        clasList = new ArrayList<>();
        secList = new ArrayList<>();
        classIdList = new ArrayList<>();
        secIdList = new ArrayList<>();
        studIdList = new ArrayList<>();
        classNameList = new ArrayList<>();
        secNameList = new ArrayList<>();
        studNameList = new ArrayList<>();
    }

    private OnTouchListener classTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int inType = classSpinner.getInputType(); // backup the input type
            classSpinner.setInputType(InputType.TYPE_NULL); // disable soft input
            classSpinner.onTouchEvent(event); // call native handler
            classSpinner.setInputType(inType); // restore input type
            return false;
        }
    };

    private OnTouchListener sectionTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int inType = sectionSpinner.getInputType(); // backup the input type
            sectionSpinner.setInputType(InputType.TYPE_NULL); // disable soft input
            sectionSpinner.onTouchEvent(event); // call native handler
            sectionSpinner.setInputType(inType); // restore input type
            return false;
        }
    };


    private OnTouchListener studentTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int inType = studentSpinner.getInputType(); // backup the input type
            studentSpinner.setInputType(InputType.TYPE_NULL); // disable soft input
            studentSpinner.onTouchEvent(event); // call native handler
            studentSpinner.setInputType(inType); // restore input type
            return false;
        }
    };

    private void deActivate() {
        allStudentsBtn.setActivated(false);
        allTeachersBtn.setActivated(false);
        classBtn.setActivated(false);
        sectionBtn.setActivated(false);
        studentBtn.setActivated(false);
        submitBtn.setEnabled(false);
        allStudentsFrame.setVisibility(View.GONE);
        allTeachersFrame.setVisibility(View.GONE);
        selectionFrame.setVisibility(View.GONE);
    }

    public class ClassDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder classBuilder = new AlertDialog.Builder(getActivity());
            classBuilder.setTitle("Classes")
                    .setMultiChoiceItems(classNameList.toArray(new CharSequence[classIdList.size()]), classSelections, new ClassSelectionClickHandler())
                    .setPositiveButton("OK", new ClassButtonClickHandler())
                    .create();
            Dialog d = classBuilder.create();
            return d;
        }
    }

    public class ClassSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            Log.d("i", classNameList.get(clicked) + " selected: " + selected);
        }
    }

    public class ClassButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    classSpinner.clearFocus();
                    selectedClass();
                    break;
            }
        }
    }

    protected void selectedClass() {
        submitBtn.setEnabled(false);
        boolean isSelected = false;
        StringBuilder sb2 = new StringBuilder();
        if (!sectionSpinner.isShown() && !studentSpinner.isShown()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < classIdList.size(); i++) {
                if (classSelections[i]) {
                    isSelected = true;
                    sb.append(classIdList.get(i) + ",");
                    sb2.append(classNameList.get(i) + ", ");
                }
            }
            if (isSelected) {
                classSpinner.setText(sb2.substring(0, sb2.length() - 2));
                ids = sb.substring(0, sb.length() - 1);
                submitBtn.setEnabled(true);
            } else {
                classSpinner.setText("");
            }
        } else if (sectionSpinner.isShown()) {
            for (int i = 0; i < classIdList.size(); i++) {
                if (classSelections[i]) {
                    isSelected = true;
                    classId = classIdList.get(i);
                    sb2.append(classNameList.get(i));
                    break;
                }
            }
            if (!isSelected) {
                classId = 0;
                classSpinner.setText("");
            } else {
                classSpinner.setText(sb2.toString());
            }
            sectionSpinner.setText("");
            secList.clear();
            secIdList.clear();
            secNameList.clear();
            secList = SectionDao.selectSection(classId, sqliteDatabase);
            for (Section s : secList) {
                secIdList.add(s.getSectionId());
                secNameList.add(s.getSectionName());
            }
            sectionSelections = new boolean[secIdList.size()];

            studIdList.clear();
            studNameList.clear();
            studentSelections = new boolean[studIdList.size()];
        }
    }

    public class SectionDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Sections")
                    .setMultiChoiceItems(secNameList.toArray(new CharSequence[secIdList.size()]), sectionSelections, new SectionSelectionClickHandler())
                    .setPositiveButton("OK", new SectionButtonClickHandler())
                    .create();
        }
    }

    public class SectionSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            Log.d("i", secNameList.get(clicked) + " selected: " + selected);
        }
    }

    public class SectionButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    sectionSpinner.clearFocus();
                    selectedSection();
                    break;
            }
        }
    }

    protected void selectedSection() {
        submitBtn.setEnabled(false);
        boolean isSelected = false;
        StringBuilder sb2 = new StringBuilder();
        if (!studentSpinner.isShown()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < secIdList.size(); i++) {
                if (sectionSelections[i]) {
                    isSelected = true;
                    sb.append(secIdList.get(i) + ",");
                    sb2.append(secNameList.get(i) + ", ");
                }
            }
            if (isSelected) {
                sectionSpinner.setText(sb2.substring(0, sb2.length() - 2));
                ids = sb.substring(0, sb.length() - 1);
                submitBtn.setEnabled(true);
            } else {
                sectionSpinner.setText("");
            }
        } else {
            for (int i = 0; i < secIdList.size(); i++) {
                if (sectionSelections[i]) {
                    isSelected = true;
                    sectionId = secIdList.get(i);
                    sb2.append(secNameList.get(i));
                    break;
                }
            }
            if (!isSelected) {
                sectionId = 0;
                sectionSpinner.setText("");
            } else {
                sectionSpinner.setText(sb2.toString());
            }
            studentSpinner.setText("");
            studIdList.clear();
            studNameList.clear();
            List<Students> studentList = StudentsDao.selectStudents(sectionId, sqliteDatabase);
            for (Students s : studentList) {
                studIdList.add(s.getStudentId());
                studNameList.add(s.getName());
            }
            studentSelections = new boolean[studIdList.size()];
        }
    }

    public class StudentDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Students")
                    .setMultiChoiceItems(studNameList.toArray(new CharSequence[studIdList.size()]), studentSelections, new StudentSelectionClickHandler())
                    .setPositiveButton("OK", new StudentButtonClickHandler())
                    .create();
        }
    }

    public class StudentSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            Log.d("i", studNameList.get(clicked) + " selected: " + selected);
        }
    }

    public class StudentButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    studentSpinner.clearFocus();
                    selectedStudent();
                    break;
            }
        }
    }

    protected void selectedStudent() {
        submitBtn.setEnabled(false);
        boolean isSelected = false;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < studIdList.size(); i++) {
            if (studentSelections[i]) {
                isSelected = true;
                sb.append(studIdList.get(i) + ",");
                sb2.append(studNameList.get(i) + ", ");
            }
        }
        if (isSelected) {
            studentSpinner.setText(sb2.substring(0, sb2.length() - 2));
            ids = sb.substring(0, sb.length() - 1);
            submitBtn.setEnabled(true);
        } else {
            studentSpinner.setText("");
        }
    }

}
