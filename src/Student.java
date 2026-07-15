public class Student extends Person {

    private static final long serialVersionUID = 114514;

    private int id;
    private String major;

    private transient String tempNote;

    public Student() {

    }

    public Student(String name, int age, int id, String major) {
        super(name, age);
        this.id = id;
        this.major = major;
    }

    public Student(int id, String major) {
        this.id = id;
        this.major = major;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getMajor() {
        return major;
    }
    public void setMajor(String major) {
        this.major = major;
    }


    //版号
    public String getTempNote() {
        return tempNote;
    }
    public void setTempNote(String tempNote) {
        this.tempNote = tempNote;
    }

    @Override
    public void showInfo(){
        super.showInfo();
        System.out.println("学号" + id + "专业" + major);
    }
}
