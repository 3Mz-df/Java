import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentUtil {
    private StudentUtil() {
        throw new RuntimeException("工具类不能实例化");
    }

    //字符串空白检验
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    //年龄检验
    public static boolean isNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

 /*   学号查重
    public static boolean isChongFu(int id, Student[] students) {
        for (Student student : students) {
            if (student != null && student.getId() == id) {
                return true;
            }
        }
        return false;
    }*/

    //学号查重
    public static boolean isChongFu(int id, List<Person> list) {
        for (Person p : list) {
            if (p instanceof Student) {
                Student s = (Student) p;
                if (s.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    //多态批量打印
    public static void printAll(List<Person> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("集合为空，无数据可打印");
            return;
        }
        for (Person person : list) {
            if (person != null) {
                person.showInfo();
            }
        }
    }

    //字符串转学生对象 格式: 姓名,年龄,学号,专业
    // 文本文件行转Student对象（英文逗号分割），格式: 姓名,年龄,学号,专业
    public static Student strToStudent(String line) {
        if (isEmpty(line)) {
            return null;
        }
        String[] parts = line.split(",");
        if (parts.length != 4) {
            System.out.println("格式错误，需为：姓名,年龄,学号,专业，当前行：" + line);
            return null;
        }
        String name = parts[0].trim();
        String ageStr = parts[1].trim();
        String idStr = parts[2].trim();
        String major = parts[3].trim();

        if (isEmpty(name)) {
            System.out.println("姓名为空，跳过行：" + line);
            return null;
        }
        if (!isNumber(ageStr)) {
            System.out.println("年龄非法，跳过行：" + line);
            return null;
        }
        if (!isNumber(idStr)) {
            System.out.println("学号非法，跳过行：" + line);
            return null;
        }

        return new Student(name, Integer.parseInt(ageStr), Integer.parseInt(idStr), major);
    }

    // Student对象转文本行（英文逗号分割）
    public static String studentToStr(Student s) {
        return s.getName() + "," + s.getAge() + "," + s.getId() + "," + s.getMajor();
    }

    public static Student parseStudent(String str) {
        if (isEmpty(str)) {
            return null;
        }
        String[] parts = str.split("，");
        if (parts.length != 4) {
            System.out.println("格式错误，需为：姓名，年龄，学号，专业");
            return null;
        }
        String name = parts[0].trim();
        String ageStr = parts[1].trim();
        String idStr = parts[2].trim();
        String major = parts[3].trim();

        if (isEmpty(name)) {
            System.out.println("姓名不能为空");
            return null;
        }
        if (!isNumber(ageStr)) {
            System.out.println("年龄必须为数字");
            return null;
        }
        if (!isNumber(idStr)) {
            System.out.println("学号必须为数字");
            return null;
        }

        int age = Integer.parseInt(ageStr);
        int id = Integer.parseInt(idStr);

        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        student.setId(id);
        student.setMajor(major);
        return student;
    }

    public static List<Person> createList() {
        List<Person> list = new ArrayList<>();
        list.add(new Person("韩立", 100));
        list.add(new Person("王婵", 90));
        list.add(new Student("陈博", 19, 250250, "物流管理"));
        list.add(new Student("彭亦", 20, 520250, "软件工程"));
        return list;
    }

    //控制台录入学生信息并校验
    public static Student inputStudent(List<Person> list) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("请输入姓名：");
            String name = sc.nextLine();
            if (isEmpty(name)) {
                System.out.println("姓名不能为空，录入终止");
                return null;
            }

            System.out.print("请输入年龄：");
            String ageStr = sc.nextLine();
            if (isEmpty(ageStr)) {
                System.out.println("年龄不能为空，录入终止");
                return null;
            }
            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                System.out.println("年龄格式异常，请输入有效数字，录入终止");
                return null;
            }
            if (age < 0 || age > 100) {
                System.out.println("非法年龄：" + age + "，年龄应在0~100之间，录入终止");
                return null;
            }

            System.out.print("请输入学号：");
            String idStr = sc.nextLine();
            if (isEmpty(idStr)) {
                System.out.println("学号不能为空，录入终止");
                return null;
            }
            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                System.out.println("学号格式异常，请输入有效数字，录入终止");
                return null;
            }
            if (isChongFu(id, list)) {
                System.out.println("学号" + id + "已存在，录入终止");
                return null;
            }

            System.out.print("请输入专业：");
            String major = sc.nextLine();
            if (isEmpty(major)) {
                System.out.println("专业不能为空，录入终止");
                return null;
            }

            Student stu = new Student(name, age, id, major);
            list.add(stu);
            System.out.println("学生" + name + "录入成功！");
            return stu;
        } catch (Exception e) {
            System.out.println("系统异常：" + e.getMessage() + "，录入终止");
            return null;
        }
    }
}
