import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        // 强制控制台输出UTF-8编码，解决中文乱码
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        List<Person> list = StudentUtil.createList();
        Scanner sc = new Scanner(System.in);

        try {
            while (true) {
                System.out.println("\n===== 学生管理系统 =====");
                System.out.println("1. 新增学生");
                System.out.println("2. 查看全部");
                System.out.println("3. 空集合打印测试");
                System.out.println("4. 退出");
                System.out.print("请选择：");
                String choice = sc.nextLine();

                switch (choice) {
                    case "1":
                        StudentUtil.inputStudent(list);
                        break;
                    case "2":
                        System.out.println("\n--- 当前全部人员 ---");
                        StudentUtil.printAll(list);
                        break;
                    case "3":
                        System.out.println("\n--- 空集合打印测试 ---");
                        StudentUtil.printAll(new ArrayList<Person>());
                        break;
                    case "4":
                        System.out.println("已退出");
                        return;
                    default:
                        System.out.println("无效选项，请重新输入");
                }
            }
        } catch (Exception e) {
            System.out.println("系统异常：" + e.getMessage() + "，程序终止");
        }
    }
}
