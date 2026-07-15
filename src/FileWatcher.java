import java.util.List;
/*33333333333333333
  后台文件巡检守护线程：
 */
public class FileWatcher extends Thread {

    // 巡检间隔（毫秒）
    private final long interval;
    // 被监控的文件路径
    private final String filePath;
    // 上一次读取的文件内容快照（用于对比变化）
    private String lastSnapshot = "";

    /**
     * 构造方法
     * @param filePath 要监控的文件路径
     * @param interval 巡检间隔（毫秒）
     */
    public FileWatcher(String filePath, long interval) {
        this.filePath = filePath;
        this.interval = interval;
        // 关键：设置为守护线程 —— 当所有非守护线程结束时，守护线程自动终止
        this.setDaemon(true);
        this.setName("FileWatcher-Daemon");
    }

    @Override
    public void run() {
        System.out.println("[巡检线程] 后台文件巡检已启动，监控文件：" + filePath);
        System.out.println("[巡检线程] 巡检间隔：" + interval + "ms，线程类型：守护线程=" + isDaemon());

        // 先读取一次初始快照
        lastSnapshot = IOUtil.readTextFile(filePath);
        System.out.println("[巡检线程] 已记录初始快照（" + lastSnapshot.length() + " 字符）");

        // 持续巡检循环
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 休眠指定间隔
                Thread.sleep(interval);

                // 读取当前文件内容
                String currentContent = IOUtil.readTextFile(filePath);

                // 与上一次快照对比，检测变化
                if (!currentContent.equals(lastSnapshot)) {
                    System.out.println("\n[巡检线程] ⚠ 检测到文件变化！正在自动刷新全局集合...");

                    // 解析新内容并刷新全局集合
                    refreshStudentList(currentContent);

                    // 更新快照
                    lastSnapshot = currentContent;
                    System.out.println("[巡检线程] ✓ 全局集合已刷新完成，当前共 " + Data.studentList.size() + " 条数据");
                }

            } catch (InterruptedException e) {
                // 收到中断信号，退出
                System.out.println("[巡检线程] 收到中断信号，巡检线程正在退出...");
                Thread.currentThread().interrupt(); // 恢复中断标志
                break;
            } catch (Exception e) {
                System.out.println("[巡检线程] 巡检过程发生异常：" + e.getMessage());
            }
        }

        System.out.println("[巡检线程] 后台文件巡检已停止");
    }

    /**
     * 解析文件内容并刷新 Data.studentList 全局集合
     */
    private void refreshStudentList(String content) {
        synchronized (Data.studentList) {
            // 清空现有集合
            Data.studentList.clear();

            if (content == null || content.trim().isEmpty()) {
                System.out.println("[巡检线程] 文件内容为空，集合已清空");
                return;
            }

            // 按行解析
            String[] lines = content.split("\n");
            int count = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                Student s = StudentUtil.strToStudent(line);
                if (s != null) {
                    Data.studentList.add(s);
                    count++;
                }
            }
            System.out.println("[巡检线程] 从文件解析出 " + count + " 条学生数据");
        }
    }

    /**
     * 手动触发一次立即巡检
     */
    public void checkNow() {
        String currentContent = IOUtil.readTextFile(filePath);
        if (!currentContent.equals(lastSnapshot)) {
            System.out.println("[巡检线程] 手动巡检发现变化，正在刷新...");
            refreshStudentList(currentContent);
            lastSnapshot = currentContent;
            System.out.println("[巡检线程] 手动刷新完成，当前共 " + Data.studentList.size() + " 条数据");
        } else {
            System.out.println("[巡检线程] 手动巡检：文件未发生变化");
        }
    }
}
