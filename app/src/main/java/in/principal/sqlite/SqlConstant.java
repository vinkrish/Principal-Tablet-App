package in.principal.sqlite;

public interface SqlConstant {

    public static String DATABASE_NAME = "principal.db";
    public static int DATABASE_VERSION = 2;

    public static String CREATE_CLASS = "CREATE TABLE class(SchoolId INTEGER, ClassId INT DEFAULT 0,"
            + "ClassName TEXT, ClassType TEXT, DateTimeRecordInserted DATETIME, SubjectGroupIds TEXT)";

    public static String CREATE_SCHOOL = "CREATE TABLE school(SchoolId INTEGER PRIMARY KEY,"
            + "SchoolName TEXT, Website TEXT, ShortenedSchoolName TEXT, SenderID TEXT, ContactPersonName TEXT,"
            + "SchoolAdminUserName TEXT, SchoolAdminPassword TEXT, Address TEXT, address_short TEXT, Landline TEXT, Mobile TEXT, Mobile2 TEXT, Email TEXT, City TEXT, State TEXT,"
            + "District TEXT, Pincode TEXT, CreationDateTime TEXT, LastLoginTime TEXT, IPAddress TEXT, NumberofMobiles INTEGER, RouteId INTEGER, Locked INTEGER,"
            + "PrincipalTeacherId INTEGER, PathtoOpen TEXT, Syllabus TEXT, NumberofStudents INTEGER, Launched TEXT, ClassIDs TEXT, CCEClassIDs TEXT, DateTimeRecordInserted DATETIME)";

    public static String CREATE_SECTION = "CREATE TABLE section(SchoolId INTEGER, SectionId INT DEFAULT 0,"
            + "ClassId INTEGER, SectionName TEXT, ClassTeacherId INTEGER, DateTimeRecordInserted DATETIME)";

    public static String CREATE_STUDENT_ATTENDANCE = "CREATE TABLE studentattendance(SchoolId INTEGER,"
            + "ClassId INTEGER, SectionId INTEGER, StudentId INTEGER, DateAttendance DATETIME, TypeOfLeave TEXT, DateTimeRecordInserted DATETIME)";

    public static String ATTENDANCE_TRIGGER = "CREATE TRIGGER before_attendance BEFORE INSERT ON studentattendance " +
            "WHEN ((SELECT count() FROM studentattendance WHERE studentattendance.StudentId=NEW.StudentId and studentattendance.DateAttendance=NEW.DateAttendance) > 0) " +
            "BEGIN " +
            "DELETE FROM studentattendance WHERE studentattendance.StudentId=NEW.StudentId AND studentattendance.DateAttendance=NEW.DateAttendance; " +
            "END ";

    public static String CREATE_STUDENTS = "CREATE TABLE students(SOWSID TEXT, StudentId INT UNIQUE DEFAULT 0, SchoolId INTEGER,"
            + "ClassId INTEGER, SectionId INTEGER, SubjectIds TEXT, AdmissionNo TEXT, RollNoInClass INTEGER, Username TEXT, Password TEXT, Image TEXT, Name TEXT,"
            + "FatherName TEXT, MotherName TEXT, DateOfBirth TEXT, Gender TEXT, Email TEXT, Mobile1 TEXT, Mobile2 TEXT, Pincode TEXT, Address TEXT,"
            + "TransportationTypeId INTEGER, Community TEXT, Income INTEGER, IsLoggedIn INTEGER, Locked INTEGER, NoSms INTEGER, CreationDateTime TEXT, LastLoginTime TEXT,"
            + "LoginCount INTEGER, FeedbackSkip INTEGER, NotificationsRead TEXT, IPAddress TEXT)";

    public static String CREATE_SUBJECTS = "CREATE TABLE subjects(SubjectId INTEGER, SchoolId INTEGER, SubjectName TEXT, has_partition INTEGER," +
            " TheorySubjectId INTEGER, PracticalSubjectId INTEGER, DateTimeRecordInserted DATETIME)";

    public static String CREATE_SUB_GROUPS = "CREATE TABLE subjects_groups(id INTEGER, SchoolId INTEGER, SubjectGroup TEXT, subject_ids TEXT, DateTimeRecordInserted DATETIME)";

    public static String CREATE_SUBJECT_TEACHER = "CREATE TABLE subjectteacher(id INTEGER, ClassId INTEGER, SubjectId INTEGER, SchoolId INTEGER, " +
            " TeacherId INTEGER, SectionId INTEGER, DateTimeRecordInserted DATETIME, PRIMARY KEY(SectionId, SubjectId, TeacherId))";

    public static String CREATE_TEACHER = "CREATE TABLE teacher(SOWTID TEXT, TeacherId INT DEFAULT 0, Image TEXT,"
            + "Username TEXT, Password TEXT,SchoolId INTEGER, Name TEXT, DOB TEXT, Mobile TEXT, Qualification TEXT, Address TEXT, DateOfJoining TEXT, Gender TEXT, Email TEXT, Pincode INTEGER,"
            + "TransportationTypeId INTEGER, Community TEXT, CreationDateTime TEXT, LastLoginTime TEXT, IPAddress TEXT, Locked INTEGER, TabUser TEXT, TabPass TEXT)";

    public static String CREATE_EXAMS = "CREATE TABLE exams(SchoolId INTEGER, ClassId INTEGER, ExamId INT DEFAULT 0, SubjectIDs TEXT, SubjectGroupIds TEXT, ExamName TEXT, OrderId INTEGER, Percentage TEXT,"
            + "TimeTable TEXT, Portions TEXT, FileName TEXT, GradeSystem INTEGER, Term INTEGER, MarkUploaded INTEGER, DateTimeRecordInserted DATETIME)";

    public static String CREATE_SUBJECT_EXAMS = "CREATE TABLE subjectexams(SchoolId INTEGER, ClassId INTEGER, ExamId INTEGER, SubjectId INTEGER, TimeTable TEXT,"
            + "Session TEXT, MaximumMark INTEGER, FailMark INTEGER, UniqueKey TEXT, DateTimeRecordInserted DATETIME)";

    public static String CREATE_MARKS = "CREATE TABLE marks(SchoolId INTEGER, ExamId INTEGER, Is_Present INTEGER, SubjectId INTEGER, StudentId INTEGER, Mark TEXT, Grade TEXT, DateTimeRecordInserted DATETIME," +
            "PRIMARY KEY(ExamId, SubjectId, StudentId))";

    public static String MARKS_TRIGGER = "CREATE TRIGGER before_marks BEFORE INSERT ON marks " +
            "WHEN ((SELECT count() FROM marks WHERE marks.ExamId=NEW.ExamId AND marks.SubjectId=NEW.SubjectId AND marks.StudentId=NEW.StudentId) > 0) " +
            "BEGIN " +
            "DELETE FROM marks WHERE marks.ExamId=NEW.ExamId AND marks.SubjectId=NEW.SubjectId AND marks.StudentId=NEW.StudentId; " +
            "END";

    public static String INSERT_MARK_TRIGGER = "CREATE TRIGGER insert_mark BEFORE INSERT ON marks " +
            "FOR EACH ROW " +
            "BEGIN " +
            "INSERT INTO avgtrack(ExamId,SubjectId,Type) values(NEW.ExamId,NEW.SubjectId,0); " +
            "END";

    public static String UPDATE_MARK_TRIGGER = "CREATE TRIGGER update_mark BEFORE UPDATE ON marks " +
            "FOR EACH ROW " +
            "BEGIN " +
            "INSERT INTO avgtrack(ExamId,SubjectId,Type) values(NEW.ExamId,NEW.SubjectId,1); " +
            "END";

    public static String CREATE_TEMP = "CREATE TABLE temp(id INTEGER PRIMARY KEY, DeviceId TEXT, SchoolId INTEGER, ClassId INTEGER, SectionId INTEGER, ClassName TEXT, SectionName TEXT,"
            + "TeacherId INTEGER, StudentId INTEGER, SubjectId INTEGER, ExamId INTEGER, ExamId2 INTEGER, ActivityId INTEGER, SubActivityId INTEGER, SlipTestId INTEGER, SelectedDate TEXT, YesterDate TEXT, OtherDate TEXT, SyncTime TEXT, IsSync INTEGER)";

    public static String CREATE_ACTIVITY = "CREATE TABLE activity(ActivityId INT DEFAULT 0, SchoolId INTEGER, ClassId INTEGER, SectionId INTEGER, ExamId INTEGER, SubjectId INTEGER, RubrixId INTEGER, ActivityName TEXT, "
            + "MaximumMark INTEGER, Weightage REAL, SubActivity INTEGER, Calculation INTEGER, DateTimeRecordInserted DATETIME, ActivityAvg REAL DEFAULT 0, CompleteEntry INTEGER DEFAULT 0, UniqueKey TEXT)";

    public static String CREATE_ACTIVITY_MARK = "CREATE TABLE activitymark(SchoolId INTEGER, ExamId INTEGER, SubjectId INTEGER, StudentId INTEGER, ActivityId INTEGER, Mark TEXT, DateTimeRecordInserted DATETIME," +
            "PRIMARY KEY(ActivityId, StudentId))";

    public static String ACTIVITYMARK_TRIGGER = "CREATE TRIGGER before_actmark BEFORE INSERT ON activitymark " +
            "WHEN ((SELECT count() FROM activitymark WHERE activitymark.ActivityId=NEW.ActivityId AND activitymark.StudentId=NEW.StudentId) > 0) " +
            "BEGIN " +
            "DELETE FROM activitymark WHERE activitymark.ActivityId=NEW.ActivityId AND activitymark.StudentId=NEW.StudentId; " +
            "END";

    public static String CREATE_ACTIVITY_GRADE = "CREATE TABLE activitygrade(SchoolId INTEGER, ExamId INTEGER, SubjectId INTEGER," +
            "StudentId INTEGER, ActivityId INTEGER, Grade TEXT, DateTimeRecordInserted DATETIME, PRIMARY KEY(ActivityId, StudentId))";

    public static String ACTIVITYGRADE_TRIGGER = "CREATE TRIGGER before_actgrade BEFORE INSERT ON activitygrade " +
            "WHEN ((SELECT count() FROM activitygrade WHERE activitygrade.ActivityId=NEW.ActivityId AND activitygrade.StudentId=NEW.StudentId) > 0) " +
            "BEGIN " +
            "DELETE FROM activitygrade WHERE activitygrade.ActivityId=NEW.ActivityId AND activitygrade.StudentId=NEW.StudentId; " +
            "END";

    public static String IN_ACTMARK_TRIGGER = "CREATE TRIGGER insert_act_mark BEFORE INSERT ON activitymark " +
            "FOR EACH ROW " +
            "BEGIN " +
            "INSERT INTO avgtrack(ExamId,ActivityId,SubjectId,Type) values(NEW.ExamId,NEW.ActivityId,NEW.SubjectId,0); " +
            "END";

    public static String UP_ACTMARK_TRIGGER = "CREATE TRIGGER update_act_mark BEFORE UPDATE ON activitymark " +
            "FOR EACH ROW " +
            "BEGIN " +
            "INSERT INTO avgtrack(ExamId,ActivityId,SubjectId,Type) values(NEW.ExamId,NEW.ActivityId,NEW.SubjectId,1); " +
            "END";

    public static String CREATE_SUB_ACTIVITY = "CREATE TABLE subactivity(SubActivityId INT DEFAULT 0, SchoolId INTEGER, ClassId INTEGER, SectionId INTEGER, ExamId INTEGER, SubjectId INTEGER,"
            + "ActivityId INTEGER, SubActivityName TEXT, MaximumMark INTEGER, Weightage INTEGER, Calculation INTEGER, SubActivityAvg REAL DEFAULT 0, CompleteEntry INTEGER DEFAULT 0," +
            " UniqueKey TEXT, DateTimeRecordInserted DATETIME)";

    public static String CREATE_SUB_ACTIVITY_MARK = "CREATE TABLE subactivitymark(SchoolId INTEGER, ExamId INTEGER, SubjectId INTEGER, StudentId INTEGER, ActivityId INTEGER, SubActivityId INTEGER," +
            "Mark TEXT, Description TEXT, DateTimeRecordInserted DATETIME, PRIMARY KEY(SubActivityId, StudentId))";

    public static String SUBACTMARK_TRIGGER = "CREATE TRIGGER before_subactmark BEFORE INSERT ON subactivitymark " +
            "WHEN ((SELECT count() FROM subactivitymark WHERE subactivitymark.SubActivityId=NEW.SubActivityId AND subactivitymark.StudentId=NEW.StudentId) > 0) " +
            "BEGIN " +
            "DELETE FROM subactivitymark WHERE subactivitymark.SubActivityId=NEW.SubActivityId AND subactivitymark.StudentId=NEW.StudentId; " +
            "END";

    public static String CREATE_SUB_ACTIVITY_GRADE = "CREATE TABLE subactivitygrade(SchoolId INTEGER, ExamId INTEGER, SubjectId INTEGER," +
            "StudentId INTEGER, ActivityId INTEGER, SubActivityId INTEGER, Grade TEXT, Description TEXT, DateTimeRecordInserted DATETIME, PRIMARY KEY(SubActivityId, StudentId))";

    public static String SUBACTGRADE_TRIGGER = "CREATE TRIGGER before_subactgrade BEFORE INSERT ON subactivitygrade " +
            "WHEN ((SELECT count() FROM subactivitygrade WHERE subactivitygrade.SubActivityId=NEW.SubActivityId AND subactivitygrade.StudentId=NEW.StudentId) > 0) " +
            "BEGIN " +
            "DELETE FROM subactivitygrade WHERE subactivitygrade.SubActivityId=NEW.SubActivityId AND subactivitygrade.StudentId=NEW.StudentId; " +
            "END";

    public static String IN_SUBACTMARK_TRIGGER = "CREATE TRIGGER insert_subact_mark BEFORE INSERT ON subactivitymark " +
            "FOR EACH ROW " +
            "BEGIN " +
            "INSERT INTO avgtrack(ExamId,ActivityId,SubActivityId,SubjectId,Type) values(NEW.ExamId,NEW.ActivityId,NEW.SubActivityId,NEW.SubjectId,0); " +
            "END";

    public static String UP_SUBACTMARK_TRIGGER = "CREATE TRIGGER update_subact_mark BEFORE UPDATE ON subactivitymark " +
            "FOR EACH ROW " +
            "BEGIN " +
            "INSERT INTO avgtrack(ExamId,ActivityId,SubActivityId,SubjectId,Type) values(NEW.ExamId,NEW.ActivityId,NEW.SubActivityId,NEW.SubjectId,1); " +
            "END";

    public static String CREATE_PORTION = "CREATE TABLE portion(PortionId INT UNIQUE DEFAULT 0, SchoolId INTEGER, ClassId INTEGER, SubjectId INTEGER, NewSubjectId INTEGER, Portion TEXT, DateTimeRecordInserted DATETIME)";

    public static String CREATE_SLIPTEST = "CREATE TABLE sliptest(SlipTestId INTEGER PRIMARY KEY, SchoolId INTEGER, ClassId INTEGER, SectionId INTEGER, SlipTestName TEXT, IsActivity INTEGER, Grade INTEGER, Count INTEGER," +
            " SubjectId INTEGER,NewSubjectId INTEGER, Portion INTEGER, ExtraPortion TEXT, PortionName TEXT, MaximumMark INTEGER, AverageMark REAL, TestDate TEXT, SubmissionDate TEXT, MarkEntered INTEGER,EmployeeId INTEGER," +
            " DateTimeRecordInserted DATETIME, Weightage REAL)";

    public static String SLIPTEST_TRIGGER = "CREATE TRIGGER before_sliptest BEFORE INSERT ON sliptest " +
            "WHEN ((SELECT count() FROM sliptest WHERE sliptest.SlipTestId=NEW.SlipTestId)>0) " +
            "BEGIN " +
            "DELETE FROM sliptest WHERE sliptest.SlipTestId=NEW.SlipTestId; " +
            "END";

    public static String CREATE_HOMEWORK = "CREATE TABLE homeworkmessage(HomeworkId INTEGER, SchoolId INTEGER, ClassId INTEGER, SectionId INTEGER, TeacherId INTEGER, MessageFrom TEXT, MessageVia TEXT, SubjectIDs TEXT," +
            "Homework TEXT, IsNew INTEGER DEFAULT 0, HomeworkDate TEXT, DateTimeRecordInserted DATETIME)";

    public static String HOMEWORK_TRIGGER = "CREATE TRIGGER before_homework BEFORE INSERT ON homeworkmessage " +
            "WHEN ((SELECT count() FROM homeworkmessage WHERE homeworkmessage.SectionId=NEW.SectionId and homeworkmessage.HomeworkDate=NEW.HomeworkDate)>0) " +
            "BEGIN " +
            "DELETE FROM homeworkmessage WHERE homeworkmessage.SectionId=NEW.SectionId and homeworkmessage.HomeworkDate=NEW.HomeworkDate; " +
            "END";

    public static String CREATE_EXMAVG = "CREATE TABLE exmavg(ClassId INTEGER, SectionId INTEGER, SubjectId INTEGER, ExamId INTEGER, ExamAvg REAL DEFAULT 0, CompleteEntry INTEGER DEFAULT 0," +
            "PRIMARY KEY(SectionId, SubjectId, ExamId))";

    public static String CREATE_STAVG = "CREATE TABLE stavg(ClassId INTEGER, SectionId INTEGER, SubjectId INTEGER, SlipTestAvg REAL DEFAULT 0, PRIMARY KEY(SectionId, SubjectId))";

    public static String CREATE_DATE_TRACKER = "CREATE TABLE datetracker(id INTEGER PRIMARY KEY, FirstDate TEXT, LastDate TEXT, NoOfDays INTEGER, SelectedMonth INTEGER)";

    public static String CREATE_ACK = "CREATE TABLE ack(AckId INTEGER, TimeStamp INTEGER)";

    public static String CREATE_COMP = "CREATE TABLE comp(SecId INTEGER, IsCompare INTEGER DEFAULT 0)";

    public static String CREATE_GCW = "CREATE TABLE gradesclasswise(GradeId INTEGER, SchoolId INTEGER, ClassId INTEGER, Grade TEXT, MarkFrom INTEGER, MarkTo INTEGER, GradePoint INTEGER, DateTimeRecordInserted DATETIME)";

    public static String CREATE_CCECOSCHOLASTIC = "CREATE TABLE ccecoscholastic(CoScholasticId INTEGER, SchoolId INTEGER, Name TEXT, ClassIDs TEXT, DateTimeRecordInserted DATETIME)";

    public static String CREATE_CCESECTIONHEADING = "CREATE TABLE ccesectionheading(SectionHeadingId INTEGER, SchoolId INTEGER, CoScholasticId INTEGER, SectionName TEXT, DateTimeRecordInserted DATETIME)";

    public static String CREATE_CCETOPICPRIMARY = "CREATE TABLE ccetopicprimary(TopicId INTEGER, SchoolId INTEGER, CoScholasticId INTEGER, SectionHeadingId INTEGER, TopicName TEXT," +
            "Evaluation INTEGER, DateTimeRecordInserted DATETIME)";

    public static String CREATE_CCEASPECTPRIMARY = "CREATE TABLE cceaspectprimary(AspectId INTEGER, SchoolId INTEGER, CoScholasticId INTEGER, SectionHeadingId INTEGER, TopicId INTEGER," +
            "AspectName TEXT, DateTimeRecordInserted DATETIME)";

    public static String CREATE_CCETOPICGRADE = "CREATE TABLE ccetopicgrade(ccetopicgradeid INTEGER, SchoolId INTEGER, TopicId INTEGER, Grade TEXT, Value INTEGER, CoScholasticId INTEGER, SectionHeadingId INTEGER)";

    public static String CREATE_CCECOSCHOLASTICGRADE = "CREATE TABLE ccecoscholasticgrade(Id INTEGER, SchoolId INTEGER, ClassId INTEGER, SectionId INTEGER, StudentId INTEGER, Type INTEGER, Term INTEGER," +
            "TopicId INTEGER, AspectId INTEGER, Grade INTEGER, Description TEXT, DateTimeRecordInserted DATETIME)";

    public static String CCEGRADE_TRIGGER = "CREATE TRIGGER before_ccegrade BEFORE INSERT ON ccecoscholasticgrade " +
            "WHEN ((SELECT count() FROM ccecoscholasticgrade WHERE ccecoscholasticgrade.Term=New.Term AND ccecoscholasticgrade.AspectId=NEW.AspectId AND ccecoscholasticgrade.StudentId=NEW.StudentId) > 0) " +
            "BEGIN " +
            "DELETE FROM ccecoscholasticgrade WHERE ccecoscholasticgrade.Term=New.Term AND ccecoscholasticgrade.AspectId=NEW.AspectId AND ccecoscholasticgrade.StudentId=NEW.StudentId; " +
            "END";

    public static String CREATE_DOWNLOADED_FILE = "CREATE TABLE downloadedfile(id INTEGER PRIMARY KEY, filename TEXT UNIQUE, downloaded INTEGER DEFAULT 0, processed INTEGER DEFAULT 0, isack INT DEFAULT 0)";

    public static String CREATE_AVGTRACK = "CREATE TABLE avgtrack(ExamId INTEGER DEFAULT 0, ActivityId INTEGER DEFAULT 0, SubActivityId INTEGER DEFAULT 0," +
            " SubjectId INTEGER DEFAULT 0, SectionId INTEGER DEFAULT 0, Type INTEGER)";

    public static String CREATE_LOCKED = "CREATE TABLE locked(FileName TEXT UNIQUE, LineNumber INTEGER, StackTrace TEXT, IsSent INTEGER DEFAULT 0)";

    public static String CREATE_CCE_STUDENT_PROFILE = "CREATE TABLE ccestudentprofile(CceId INTEGER, SchoolId TEXT, ClassId TEXT, SectionId TEXT," +
            " StudentId TEXT, StudentName TEXT, Section TEXT, House TEXT, AdmissionNo TEXT, DateofBirth TEXT, Address TEXT, PhoneNo TEXT, RegistrationNo TEXT, MotherName TEXT," +
            " FatherName TEXT, Height TEXT, Weight TEXT, BloodGroup TEXT, VisionL TEXT, VisionR TEXT, Ailment TEXT, OralHygiene TEXT, TotalDays1 REAL, DaysAttended1 REAL," +
            " TotalDays2 REAL, DaysAttended2 REAL, TotalDays3 REAL, DaysAttended3 REAL, DateTimeRecordInserted DATETIME, Gender TEXT, HealthStatus TEXT, Term INTEGER DEFAULT 1," +
            " FromDate TEXT, ToDate TEXT)";

    public static String CSP_TRIGGER = "CREATE TRIGGER before_csp BEFORE INSERT ON ccestudentprofile " +
            "WHEN ((SELECT count() FROM ccestudentprofile WHERE ccestudentprofile.SectionId=NEW.SectionId AND ccestudentprofile.Term=NEW.Term AND ccestudentprofile.StudentId=NEW.StudentId)>0) " +
            "BEGIN " +
            "DELETE FROM ccestudentprofile WHERE ccestudentprofile.SectionId=NEW.SectionId AND ccestudentprofile.Term=NEW.Term AND ccestudentprofile.StudentId=NEW.StudentId; " +
            "END";

    public static String VOICE_CALL = "CREATE TABLE voicecall(id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, CustomName TEXT DEFAULT Voice_message, Date TEXT, MessageTo TEXT, Ids TEXT)";

    public static String TEXT_SMS = "CREATE TABLE textsms(Message TEXT, Date TEXT, MessageTo TEXT, Ids TEXT)";
}
