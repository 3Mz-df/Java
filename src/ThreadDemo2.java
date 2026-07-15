/*2222222222222222222222222
 线程方法实操练习：
 */
public class ThreadDemo2 {
    // 用于 interrupt 演示的线程引用
    private static volatile boolean running = true;

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("========== 线程方法实操练习 ==========\n");

        // ========== 1. sleep() 线程休眠 ==========
        demo_sleep();

        // ========== 2. join() 等待线程执行完毕 ==========
        demo_join();

        // ========== 3. yield() CPU 礼让 ==========
        demo_yield();

        // ========== 4. interrupt() 优雅终止 ==========
        demo_interrupt();

        System.out.println("\n========== 全部演示完毕 ==========");
    }

    // ==================== 1. sleep() 线程休眠演示 ====================
    private static void demo_sleep() {
        System.out.println("  1. sleep() 线程休眠演示                  ");

        Thread sleepThread = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("  [sleep线程] 第" + i + "次输出 - " + System.currentTimeMillis());
                try {
                    // sleep 使当前线程休眠指定毫秒数，不释放锁
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("  [sleep线程] 休眠中被中断！");
                    break;
                }
            }
        }, "SleepDemo");

        sleepThread.start();
        try {
            sleepThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("  → sleep(500) 使线程每次循环间隔500ms，共耗时约2.5秒\n");
    }

    // ==================== 2. join() 等待线程执行完毕演示 ====================
    private static void demo_join() {
        System.out.println("  2. join() 等待线程执行完毕演示          ");

        Thread workerA = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  [工人A] 正在干活...步骤" + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("  [工人A] 干活完毕！");
        }, "WorkerA");

        Thread workerB = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  [工人B] 正在干活...步骤" + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("  [工人B] 干活完毕！");
        }, "WorkerB");

        System.out.println("主线程：让A和B同时开始干活...");
        workerA.start();
        workerB.start();

        try {
            System.out.println("主线程：等待A干完...");
            workerA.join(); // 主线程阻塞，等待 workerA 执行完毕
            System.out.println("主线程：A已经干完了！");

            System.out.println("主线程：等待B干完...");
            workerB.join(); // 主线程阻塞，等待 workerB 执行完毕
            System.out.println("主线程：B也已经干完了！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("  → join() 保证主线程在所有子线程完成后才继续执行\n");
    }

    // ==================== 3. yield() CPU 礼让演示 ====================
    private static void demo_yield() {
        System.out.println("  3. yield() CPU 礼让演示                  ");

        // 不使用 yield 的线程
        Thread noYield = new Thread(() -> {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100_000_000; i++) {
                // 纯计算，不让出CPU
            }
            long end = System.currentTimeMillis();
            System.out.println("  [无私让] 耗时: " + (end - start) + "ms");
        }, "NoYield");

        // 使用 yield 的线程
        Thread withYield = new Thread(() -> {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100_000_000; i++) {
                // 每次循环都提示调度器：我可以让出CPU
                Thread.yield();
            }
            long end = System.currentTimeMillis();
            System.out.println("  [有礼让] 耗时: " + (end - start) + "ms");
        }, "WithYield");

        System.out.println("  启动两个线程，一个有yield()礼让，一个没有...");
        noYield.start();
        withYield.start();

        try {
            noYield.join();
            withYield.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("  → yield() 提示调度器当前线程愿意让出CPU给同优先级线程");
        System.out.println("  → 使用 yield() 的线程通常执行时间更长（频繁切换开销）\n");
    }

    // ==================== 4. interrupt() 终止演示 ====================
    private static void demo_interrupt() {
        System.out.println("  4. interrupt() 终止演示              ");

        // 方式A：通过 interrupt() + isInterrupted() 终止
        Thread countThread = new Thread(() -> {
            int count = 0;
            // 检查中断标志，退出循环
            while (!Thread.currentThread().isInterrupted()) {
                count++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("  [计数器线程] 收到中断信号，准备退出...");
                    // 捕获 InterruptedException 后中断标志被清除，
                    // 需要再次调用 interrupt() 或在 catch 中 break
                    Thread.currentThread().interrupt(); // 恢复中断标志
                    break;
                }
            }

            System.out.println("  [计数器线程] 已优雅退出，共计数 " + count + " 次");
        }, "CounterThread");


        countThread.start();

        System.out.println("  计数器线程已启动，将在2秒后发送中断信号...");
        try {
            Thread.sleep(2000); // 让计数线程运行2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("  主线程：发送 interrupt() 中断信号...");
        countThread.interrupt(); // 发送中断信号

        try {
            countThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
