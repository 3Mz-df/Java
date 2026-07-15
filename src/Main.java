import java.util.*;

public class Main {

    private static String storageMode = "txt";
    private static final String TXT_FILE = "student.txt";
    private static final String DAT_FILE = "student.dat";
    private static FileWatcher fileWatcher = null;
    private static final long WATCH_INTERVAL = 3000;

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        loadDataFromFile();
        if (Data.studentList.isEmpty()) {
            initSampleData();
        }
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\n========== 学生管理系统 ==========");
                System.out.println("  1. 学生单条操作");
                System.out.println("  2. 批量数据操作");
                System.out.println("  3. 统计查询");
                System.out.println("  4. 存储管理");
                System.out.println("  5. 线程管理");
                System.out.println("  0. 退出系统");
                System.out.println("==================================");
                System.out.print("请选择：");
                String input = sc.nextLine();
                if (!StudentUtil.isNumber(input)) {
                    System.out.println("输入非法，请输入数字选项！");
                    continue;
                }
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1: StudentFF(sc); break;
                    case 2: DataSS(sc); break;
                    case 3: ChaXunTT(sc); break;
                    case 4: storageMenu(sc); break;
                    case 5: threadMenu(sc); break;
                    case 0:
                        stopFileWatcher();
                        saveDataToFile();
                        System.out.println("已退出");
                        return;
                    default:
                        System.out.println("无效选项，请重新输入0-5");
                }
            } catch (InputMismatchException e) {
                System.out.println("输入非法，请输入数字选项");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("系统异常：" + e.getMessage());
            }
        }
    }

    private static void loadDataFromFile() {
        if ("dat".equals(storageMode)) {
            List<Person> loaded = IOUtil.deserializeList(DAT_FILE);
            if (!loaded.isEmpty()) {
                Data.studentList.clear();
                Data.studentList.addAll(loaded);
                System.out.println("已从 " + DAT_FILE + " 加载 " + Data.studentList.size() + " 条数据");
            }
        } else {
            String content = IOUtil.readTextFile(TXT_FILE);
            if (!content.isEmpty()) {
                Data.studentList.clear();
                String[] lines = content.split("\n");
                int successCount = 0;
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    Student s = StudentUtil.strToStudent(line);
                    if (s != null) {
                        Data.studentList.add(s);
                        successCount++;
                    }
                }
                System.out.println("已从 " + TXT_FILE + " 加载 " + successCount + " 条数据");
            }
        }
    }

    private static synchronized void saveDataToFile() {
        if ("dat".equals(storageMode)) {
            IOUtil.serializeList(DAT_FILE, Data.studentList);
        } else {
            StringBuilder sb = new StringBuilder();
            for (Person p : Data.studentList) {
                if (p instanceof Student) {
                    sb.append(StudentUtil.studentToStr((Student) p)).append("\n");
                }
            }
            IOUtil.writeTextFile(TXT_FILE, sb.toString());
        }
    }

    private static void storageMenu(Scanner sc) {
        while (true) {
            try {
                System.out.println("\n--- 存储管理 ---");
                System.out.println("  当前模式：" + ("dat".equals(storageMode) ? "DAT序列化" : "TXT文本"));
                System.out.println("  1. 切换TXT  2. 切换DAT  3. 立即保存  4. 重新加载  0. 返回");
                System.out.print("请选择：");
                String input = sc.nextLine();
                if (!StudentUtil.isNumber(input)) { System.out.println("输入非法"); continue; }
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1: storageMode = "txt"; saveDataToFile(); System.out.println("已切换TXT"); break;
                    case 2: storageMode = "dat"; saveDataToFile(); System.out.println("已切换DAT"); break;
                    case 3: saveDataToFile(); System.out.println("保存完成"); break;
                    case 4: loadDataFromFile(); System.out.println("加载完成，共" + Data.studentList.size() + "条"); break;
                    case 0: return;
                    default: System.out.println("无效选项");
                }
            } catch (Exception e) { System.out.println("异常：" + e.getMessage()); }
        }
    }

    private static void initSampleData() {
        Data.studentList.add(new Student("韩立", 100, 1001, "修仙学"));
        Data.studentList.add(new Student("王婵", 90, 1002, "修仙学"));
        Data.studentList.add(new Student("陈博", 19, 250250, "物流管理"));
        Data.studentList.add(new Student("彭亦", 20, 520250, "软件工程"));
        Data.studentList.add(new Student("张三", 22, 1003, "软件工程"));
        Data.studentList.add(new Student("李四", 21, 1004, "计算机科学"));
        Data.studentList.add(new Student("王五", 23, 1005, "计算机科学"));
        Data.studentList.add(new Student("赵六", 18, 1006, "软件工程"));
    }

    private static void StudentFF(Scanner sc) {
        while (true) {
            try {
                System.out.println("\n--- 学生单条操作 ---");
                System.out.println("  1.新增 2.修改 3.删除 4.学号查询 5.姓名查询 0.返回");
                System.out.print("请选择：");
                String input = sc.nextLine();
                if (!StudentUtil.isNumber(input)) { System.out.println("输入非法"); continue; }
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1: addStudent(sc); break;
                    case 2: updateStudent(sc); break;
                    case 3: deleteStudent(sc); break;
                    case 4: queryById(sc); break;
                    case 5: queryByName(sc); break;
                    case 0: return;
                    default: System.out.println("无效选项");
                }
            } catch (Exception e) { System.out.println("异常：" + e.getMessage()); }
        }
    }

    private static void addStudent(Scanner sc) {
        System.out.println("\n-- 新增学生 --");
        StudentUtil.inputStudent(Data.studentList);
    }

    private static void updateStudent(Scanner sc) {
        System.out.println("\n-- 修改学生 --");
        System.out.print("学号：");
        String idStr = sc.nextLine();
        if (!StudentUtil.isNumber(idStr)) { System.out.println("学号必须为数字"); return; }
        int id = Integer.parseInt(idStr);
        Student target = null;
        for (Person p : Data.studentList) {
            if (p instanceof Student) {
                Student s = (Student) p;
                if (s.getId() == id) { target = s; break; }
            }
        }
        if (target == null) { System.out.println("未找到"); return; }
        System.out.println("当前信息："); target.showInfo();
        System.out.println("输入新信息（回车跳过）：");
        System.out.print("姓名（" + target.getName() + "）：");
        String name = sc.nextLine();
        if (!StudentUtil.isEmpty(name)) target.setName(name);
        System.out.print("年龄（" + target.getAge() + "）：");
        String ageStr = sc.nextLine();
        if (!StudentUtil.isEmpty(ageStr)) {
            if (StudentUtil.isNumber(ageStr)) {
                int age = Integer.parseInt(ageStr);
                if (age < 0 || age > 100) { System.out.println("非法年龄"); return; }
                target.setAge(age);
            } else { System.out.println("年龄须为数字"); return; }
        }
        System.out.print("专业（" + target.getMajor() + "）：");
        String major = sc.nextLine();
        if (!StudentUtil.isEmpty(major)) target.setMajor(major);
        System.out.println("修改成功");
    }

    private static void deleteStudent(Scanner sc) {
        System.out.println("\n-- 删除学生 --");
        System.out.print("学号：");
        String idStr = sc.nextLine();
        if (!StudentUtil.isNumber(idStr)) { System.out.println("学号须为数字"); return; }
        int id = Integer.parseInt(idStr);
        Student target = null;
        for (Person p : Data.studentList) {
            if (p instanceof Student) {
                Student s = (Student) p;
                if (s.getId() == id) { target = s; break; }
            }
        }
        if (target == null) { System.out.println("未找到"); return; }
        target.showInfo();
        System.out.print("确认删除？(yes/no)：");
        if ("yes".equalsIgnoreCase(sc.nextLine())) {
            Data.studentList.remove(target);
            System.out.println("已删除");
        } else { System.out.println("已取消"); }
    }

    private static void queryById(Scanner sc) {
        System.out.println("\n-- 学号查询 --");
        System.out.print("学号：");
        String idStr = sc.nextLine();
        if (!StudentUtil.isNumber(idStr)) { System.out.println("学号须为数字"); return; }
        int id = Integer.parseInt(idStr);
        for (Person p : Data.studentList) {
            if (p instanceof Student) {
                Student s = (Student) p;
                if (s.getId() == id) { s.showInfo(); return; }
            }
        }
        System.out.println("未找到");
    }

    private static void queryByName(Scanner sc) {
        System.out.println("\n-- 姓名查询 --");
        System.out.print("姓名：");
        String name = sc.nextLine();
        if (StudentUtil.isEmpty(name)) { System.out.println("姓名不能为空"); return; }
        boolean found = false;
        for (Person p : Data.studentList) {
            if (p instanceof Student) {
                Student s = (Student) p;
                if (s.getName().contains(name)) { s.showInfo(); found = true; }
            }
        }
        if (!found) System.out.println("未找到");
    }

    private static void DataSS(Scanner sc) {
        while (true) {
            try {
                System.out.println("\n--- 批量操作 ---");
                System.out.println("  1.批量录入 2.按专业删除 3.全部展示 0.返回");
                System.out.print("请选择：");
                String input = sc.nextLine();
                if (!StudentUtil.isNumber(input)) { System.out.println("输入非法"); continue; }
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1: batchAddStudents(sc); break;
                    case 2: batchDeleteByMajor(sc); break;
                    case 3: displayAllStudents(); break;
                    case 0: return;
                    default: System.out.println("无效选项");
                }
            } catch (Exception e) { System.out.println("异常：" + e.getMessage()); }
        }
    }

    private static void batchAddStudents(Scanner sc) {
        System.out.println("\n-- 批量录入 --");
        System.out.print("数量：");
        String countStr = sc.nextLine();
        if (!StudentUtil.isNumber(countStr)) { System.out.println("数量须为数字"); return; }
        int count = Integer.parseInt(countStr);
        if (count <= 0) { System.out.println("数量须>0"); return; }
        int ok = 0, fail = 0;
        for (int i = 1; i <= count; i++) {
            System.out.println("\n第" + i + "名：");
            System.out.print("姓名："); String name = sc.nextLine();
            if (StudentUtil.isEmpty(name)) { System.out.println("姓名空"); fail++; continue; }
            System.out.print("年龄："); String ageStr = sc.nextLine();
            if (StudentUtil.isEmpty(ageStr) || !StudentUtil.isNumber(ageStr)) { System.out.println("年龄非法"); fail++; continue; }
            int age = Integer.parseInt(ageStr);
            if (age < 0 || age > 100) { System.out.println("年龄非法"); fail++; continue; }
            System.out.print("学号："); String idStr = sc.nextLine();
            if (StudentUtil.isEmpty(idStr) || !StudentUtil.isNumber(idStr)) { System.out.println("学号非法"); fail++; continue; }
            int id = Integer.parseInt(idStr);
            if (StudentUtil.isChongFu(id, Data.studentList)) { System.out.println("学号重复"); fail++; continue; }
            System.out.print("专业："); String major = sc.nextLine();
            if (StudentUtil.isEmpty(major)) { System.out.println("专业空"); fail++; continue; }
            Data.studentList.add(new Student(name, age, id, major));
            System.out.println("录入成功"); ok++;
        }
        System.out.println("\n完成：成功" + ok + "人，失败" + fail + "人");
    }

    private static void batchDeleteByMajor(Scanner sc) {
        System.out.println("\n-- 按专业删除 --");
        System.out.print("专业：");
        String major = sc.nextLine();
        if (StudentUtil.isEmpty(major)) { System.out.println("专业不能为空"); return; }
        List<Student> toRemove = new ArrayList<>();
        for (Person p : Data.studentList) {
            if (p instanceof Student) {
                Student s = (Student) p;
                if (s.getMajor().equals(major)) toRemove.add(s);
            }
        }
        if (toRemove.isEmpty()) { System.out.println("未找到"); return; }
        System.out.println("将删除" + toRemove.size() + "名学生：");
        for (Student s : toRemove) s.showInfo();
        System.out.print("确认？(yes/no)：");
        if ("yes".equalsIgnoreCase(sc.nextLine())) {
            Data.studentList.removeAll(toRemove);
            System.out.println("已删除" + toRemove.size() + "名");
        } else { System.out.println("已取消"); }
    }

    private static void displayAllStudents() {
        System.out.println("\n-- 全部学生 --");
        StudentUtil.printAll(Data.studentList);
        System.out.println("共" + Data.studentList.size() + "名");
    }

    private static void ChaXunTT(Scanner sc) {
        while (true) {
            try {
                System.out.println("\n--- 统计查询 ---");
                System.out.println("  1.总人数 2.专业分组 3.年龄极值 0.返回");
                System.out.print("请选择：");
                String input = sc.nextLine();
                if (!StudentUtil.isNumber(input)) { System.out.println("输入非法"); continue; }
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1: AllStudents(); break;
                    case 2: groupByMajor(); break;
                    case 3: findMinMaxAge(); break;
                    case 0: return;
                    default: System.out.println("无效选项");
                }
            } catch (Exception e) { System.out.println("异常：" + e.getMessage()); }
        }
    }

    private static void AllStudents() {
        int count = 0;
        for (Person p : Data.studentList) { if (p instanceof Student) count++; }
        System.out.println("学生人数：" + count);
    }

    private static void groupByMajor() {
        if (Data.studentList.isEmpty()) { System.out.println("无数据"); return; }
        Map<String, Integer> map = new HashMap<>();
        for (Person p : Data.studentList) {
            if (p instanceof Student) {
                String m = ((Student) p).getMajor();
                map.put(m, map.getOrDefault(m, 0) + 1);
            }
        }
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            System.out.println(e.getKey() + "：" + e.getValue() + "人");
        }
    }

    private static void findMinMaxAge() {
        List<Student> students = new ArrayList<>();
        for (Person p : Data.studentList) { if (p instanceof Student) students.add((Student) p); }
        if (students.isEmpty()) { System.out.println("无数据"); return; }
        Student maxS = students.get(0), minS = students.get(0);
        for (Student s : students) {
            if (s.getAge() > maxS.getAge()) maxS = s;
            if (s.getAge() < minS.getAge()) minS = s;
        }
        System.out.print("最大："); maxS.showInfo();
        System.out.print("最小："); minS.showInfo();
    }

    // ========== 线程管理 ==========
    private static void threadMenu(Scanner sc) {
        while (true) {
            try {
                System.out.println("\n--- 线程管理 ---");
                System.out.println("  巡检状态：" + (fileWatcher != null && fileWatcher.isAlive() ? "运行中" : "已停止"));
                System.out.println("  1.启动巡检 2.停止巡检 3.手动巡检 4.同步保存测试 0.返回");
                System.out.print("请选择：");
                String input = sc.nextLine();
                if (!StudentUtil.isNumber(input)) { System.out.println("输入非法"); continue; }
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1: startFileWatcher(); break;
                    case 2: stopFileWatcher(); break;
                    case 3: manualCheck(); break;
                    case 4: syncSaveTest(); break;
                    case 0: return;
                    default: System.out.println("无效选项");
                }
            } catch (Exception e) { System.out.println("异常：" + e.getMessage()); }
        }
    }

    private static void startFileWatcher() {
        if (fileWatcher != null && fileWatcher.isAlive()) {
            System.out.println("巡检线程已在运行中");
            return;
        }
        saveDataToFile();
        fileWatcher = new FileWatcher(TXT_FILE, WATCH_INTERVAL);
        fileWatcher.start();
        System.out.println("后台巡检线程已启动（守护线程），监控：" + TXT_FILE);
    }

    private static void stopFileWatcher() {
        if (fileWatcher == null || !fileWatcher.isAlive()) {
            System.out.println("巡检线程未运行");
            return;
        }
        fileWatcher.interrupt();
        try { fileWatcher.join(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        System.out.println("巡检线程已停止");
        fileWatcher = null;
    }

    private static void manualCheck() {
        if (fileWatcher != null && fileWatcher.isAlive()) {
            fileWatcher.checkNow();
        } else {
            String content = IOUtil.readTextFile(TXT_FILE);
            if (content.isEmpty()) { System.out.println("文件为空"); return; }
            String[] lines = content.split("\n");
            List<Person> tmp = new ArrayList<>();
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Student s = StudentUtil.strToStudent(line);
                if (s != null) tmp.add(s);
            }
            System.out.println("读取到" + tmp.size() + "条数据（未刷新集合）");
        }
    }

    private static void syncSaveTest() {
        System.out.println("\n-- 同步保存测试 --");
        System.out.println("启动3条线程同时保存...");
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int tid = i + 1;
            threads[i] = new Thread(() -> {
                System.out.println("  [线程" + tid + "] 开始保存...");
                saveDataToFile();
                System.out.println("  [线程" + tid + "] 保存完成");
            }, "Save-" + tid);
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        System.out.println("所有线程保存完毕，synchronized保证线程安全");
    }
}
