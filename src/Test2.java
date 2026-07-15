import java.util.ArrayList;
import java.util.List;

/**
 * 对比测试：
 * TXT文本存储 vs DAT序列化存储
 * 验证记事本打开txt可阅读、dat二进制乱码
 */
public class Test2 {
    public static void main(String[] args) {
        // 强制控制台输出UTF-8编码
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("========== 存储格式对比测试 ==========\n");

        // 准备测试数据
        List<Person> testList = new ArrayList<>();
        testList.add(new Student("韩立", 100, 1001, "修仙学"));
        testList.add(new Student("陈博", 19, 250250, "物流管理"));
        testList.add(new Student("彭亦", 20, 520250, "软件工程"));

        // ========== 1. TXT 文本存储 ==========
        System.out.println("【1】TXT 文本存储（使用转换流 + UTF-8 编码）");

        // try-with-resources 方式写入
        StringBuilder sb = new StringBuilder();
        for (Person p : testList) {
            if (p instanceof Student) {
                sb.append(StudentUtil.studentToStr((Student) p)).append("\n");
            }
        }
        IOUtil.writeTextFile("student.txt", sb.toString());

        // try-catch-finally 方式读取
        String content = IOUtil.readTextFileFinally("student.txt");
        System.out.println("读取到的文本内容：");
        System.out.println("------------------------------");
        System.out.println(content);
        System.out.println("------------------------------");
        System.out.println("✓ 用记事本打开 student.txt 可正常阅读中文\n");

        // ========== 2. DAT 序列化存储 ==========
        System.out.println("【2】DAT 序列化存储");

        // try-catch-finally 方式序列化
        System.out.println("--- 使用 try-catch-finally 手动关闭流 ---");
        IOUtil.serializeListFinally("student.dat", testList);

        // try-with-resources 方式反序列化
        System.out.println("--- 使用 try-with-resources 自动关闭流 ---");
        List<Person> loaded = IOUtil.deserializeList("student.dat");

        System.out.println("反序列化还原的数据：");
        StudentUtil.printAll(loaded);
        System.out.println("✗ 用记事本打开 student.dat 显示二进制乱码\n");

        // ========== 3. 格式对比总结 ==========
        System.out.println("========== 对比总结 ==========");
        System.out.println("TXT文件： 文本格式，记事本可直接查看和编辑，内容可读性强");
        System.out.println("DAT文件： 二进制格式，记事本打开乱码，但存储安全不易被篡改");
        System.out.println("==============================");
    }
}
