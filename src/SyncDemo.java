/*444444444444444444444444444444
多线程并发同步处理：
 */
public class SyncDemo {

    // 共用的测试文件路径
    private static final String TEST_FILE = "sync_test.txt";

    // 线程数
    private static final int THREAD_COUNT = 5;

    // 每条线程写入次数
    private static final int WRITE_COUNT = 3;

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("========== 多线程并发同步处理 ==========\n");

        // ==================== 第一阶段：未加锁，观察数据错乱 ====================
        System.out.println(" 阶段一：未加锁 — 并发写入，观察数据错乱  ");

        // 清理文件
        IOUtil.writeTextFile(TEST_FILE, "");
        System.out.println("初始文件已清空\n");

        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i + 1;
            threads[i] = new Thread(() -> {
                for (int j = 1; j <= WRITE_COUNT; j++) {
                    // 未加锁：直接写文件
                    unsynchronizedWrite(TEST_FILE, threadId, j);
                }
            }, "Writer-" + threadId);
        }

        System.out.println("启动 " + THREAD_COUNT + " 条线程并发写入（未加锁）...\n");
        long startTime = System.currentTimeMillis();

        for (Thread t : threads) {
            t.start();
        }

        // 等待所有线程执行完毕
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n所有线程执行完毕，耗时：" + (endTime - startTime) + "ms");

        // 读取未加锁写入的结果
        String unsafeResult = IOUtil.readTextFile(TEST_FILE);
        System.out.println("\n--- 未加锁写入结果 ---");
        System.out.println("文件内容：\n" + (unsafeResult.isEmpty() ? "(空)" : unsafeResult));
        System.out.println("预期应有 " + (THREAD_COUNT * WRITE_COUNT) + " 条记录");

        // 统计实际记录数
        int actualCount = unsafeResult.isEmpty() ? 0 : unsafeResult.split("\n").length;
        int expectedCount = THREAD_COUNT * WRITE_COUNT;
        System.out.println("实际记录数：" + actualCount);
        System.out.println("缺失记录数：" + (expectedCount - actualCount));
        if (actualCount < expectedCount) {
            System.out.println(" 数据丢失！这就是并发不安全的后果！");
        }

        System.out.println("\n============================================\n");

        // ==================== 第二阶段：加锁，保证线程安全 ====================
        System.out.println("  阶段二：synchronized 加锁 — 线程安全    ");

        // 清理文件
        IOUtil.writeTextFile(TEST_FILE, "");
        System.out.println("文件已清空\n");

        Thread[] syncThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i + 1;
            syncThreads[i] = new Thread(() -> {
                for (int j = 1; j <= WRITE_COUNT; j++) {
                    // 加锁：同步写文件
                    synchronizedWrite(TEST_FILE, threadId, j);
                }
            }, "SyncWriter-" + threadId);
        }

        System.out.println("启动 " + THREAD_COUNT + " 条线程并发写入（synchronized 加锁）...\n");
        startTime = System.currentTimeMillis();

        for (Thread t : syncThreads) {
            t.start();
        }

        for (Thread t : syncThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        endTime = System.currentTimeMillis();
        System.out.println("\n所有线程执行完毕，耗时：" + (endTime - startTime) + "ms");

        // 读取加锁写入的结果
        String safeResult = IOUtil.readTextFile(TEST_FILE);
        System.out.println("\n--- synchronized 加锁写入结果 ---");
        System.out.println("文件内容：\n" + safeResult);

        int safeCount = safeResult.isEmpty() ? 0 : safeResult.split("\n").length;
        System.out.println("预期记录数：" + expectedCount + "，实际记录数：" + safeCount);
        System.out.println(safeCount == expectedCount ? "✓ 数据完整，线程安全！" : "✗ 仍有数据丢失");

    }

    /**
     * 未加锁的写文件方法 — 模拟并发不安全的操作
     */
    private static void unsynchronizedWrite(String filePath, int threadId, int seq) {
        // 模拟：先读取、再拼接、再写入

        // 1. 读取当前文件内容
        String current = IOUtil.readTextFile(filePath);

        // 2. 模拟处理耗时
        try {
            Thread.sleep(10); // 故意延迟，让多个线程更容易交错执行
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. 拼接新内容
        String newLine = "[线程" + threadId + "-第" + seq + "次] " + System.currentTimeMillis();
        String newContent;
        if (current.isEmpty()) {
            newContent = newLine;
        } else {
            newContent = current + "\n" + newLine;
        }

        // 4. 写入文件
        IOUtil.writeTextFile(filePath, newContent);
        System.out.println("  [线程" + threadId + "] 写入第" + seq + "次");
    }

    /**
     * synchronized 加锁的写文件方法 — 线程安全
     * synchronized 修饰 static 方法 = 类锁（SyncDemo.class），
     * 同一时刻只允许一个线程进入该方法。
     */
    private static synchronized void synchronizedWrite(String filePath, int threadId, int seq) {
        // 整个"读取-拼接-写入"操作被锁保护，保证原子性

        // 1. 读取当前文件内容
        String current = IOUtil.readTextFile(filePath);

        // 2. 模拟处理耗时
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. 拼接新内容
        String newLine = "[线程" + threadId + "-第" + seq + "次-synchronized] " + System.currentTimeMillis();
        String newContent;
        if (current.isEmpty()) {
            newContent = newLine;
        } else {
            newContent = current + "\n" + newLine;
        }

        // 4. 写入文件
        IOUtil.writeTextFile(filePath, newContent);
        System.out.println("  [线程" + threadId + "] 同步写入第" + seq + "次（加锁保护）");
    }
}
