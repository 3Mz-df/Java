import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;

    public Person(){

    }
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        if (age < 0 || age > 100) {
                System.out.println("非法年龄：" + age + "，年龄应在0~100之间");
                return;
        }
            this.age = age;
        }

    public void showInfo(){
        System.out.println("姓名" + name + "年龄" + age);
    }
}
