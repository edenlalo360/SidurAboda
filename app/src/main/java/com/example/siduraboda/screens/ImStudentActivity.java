public class ImStudentActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String studentId = "USER_ID_FROM_AUTH"; // כאן תביא את ה-UID של המחובר
    private TextView tvName, statusProcess, statusTest, statusPassed;
    private RecyclerView rvFuture, rvPast;
    private ArrayList<Lesson> futureList = new ArrayList<>();
    private ArrayList<Lesson> pastList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_student);

        // אתחול רכיבים
        tvName = findViewById(R.id.tv_student_name_display);
        statusProcess = findViewById(R.id.status_process);
        statusTest = findViewById(R.id.status_test);
        statusPassed = findViewById(R.id.status_passed);
        rvFuture = findViewById(R.id.rv_future_lessons);
        rvPast = findViewById(R.id.rv_past_lessons);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadStudentData();
        loadLessons();
    }

    private void loadStudentData() {
        mDatabase.child("Students").child(studentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                if (student != null) {
                    tvName.setText("שלום, " + student.getName());
                    updateStatusUI(student.getStatus());
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateStatusUI(String status) {
        // איפוס עיצובים
        statusProcess.setBackgroundResource(R.drawable.bg_status_inactive);
        statusTest.setBackgroundResource(R.drawable.bg_status_inactive);
        statusPassed.setBackgroundResource(R.drawable.bg_status_inactive);

        if ("in_process".equals(status)) statusProcess.setBackgroundResource(R.drawable.bg_status_active);
        else if ("in_test".equals(status)) statusTest.setBackgroundResource(R.drawable.bg_status_active);
        else if ("passed".equals(status)) statusPassed.setBackgroundResource(R.drawable.bg_status_active);
    }

    private void loadLessons() {
        mDatabase.child("Lessons").orderByChild("studentId").equalTo(studentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        futureList.clear();
                        pastList.clear();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String todayStr = sdf.format(new Date());

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Lesson lesson = postSnapshot.getValue(Lesson.class);
                            try {
                                Date lessonDate = sdf.parse(lesson.getDate());
                                Date today = sdf.parse(todayStr);

                                if (lessonDate != null && lessonDate.before(today)) {
                                    pastList.add(lesson);
                                } else {
                                    futureList.add(lesson);
                                }
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                        // כאן תגדיר את ה-Adapters ל-RecyclerView (צריך ליצור Adapter פשוט)
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}