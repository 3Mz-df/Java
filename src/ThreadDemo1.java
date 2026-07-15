/*1111111111111111111111111111
 线程基础编码练习：
 */
public class ThreadDemo1 {
    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("========== 线程基础编码练习 ==========\n");

        // 一 继承 Thread 类
        System.out.println("方式一 继承 Thread 类创建线程：");
        MyThread thread1 = new MyThread("Thread-继承");
        thread1.start(); // 启动线程

        //  二 实现 Runnable 接口
        System.out.println("方式二 实现 Runnable 接口创建线程：");
        MyRunnable myRunnable = new MyRunnable("Runnable-实现");
        Thread thread2 = new Thread(myRunnable);
        thread2.start(); // 启动线程

        //  三 匿名内部类 + Runnable
        System.out.println("方式三 匿名内部类 + Runnable：");
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    System.out.println("  [匿名Runnable/" + Thread.currentThread().getName() + "] 第" + i + "次输出");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }, "匿名线程");
        thread3.start();

        //四 Lambda 表达式
        System.out.println("方式四 Lambda 表达式：");
        Thread thread4 = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("  [Lambda/" + Thread.currentThread().getName() + "] 第" + i + "次输出");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "Lambda线程");
        thread4.start();

        // 等待所有子线程执行完毕
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// ==================== 方式一：继承 Thread 类 ====================
class MyThread extends Thread {
    private String prefix;

    public MyThread(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("  [" + prefix + "/" + Thread.currentThread().getName() + "] 第" + i + "次输出");
            try {
                Thread.sleep(200); // 模拟耗时操作
            } catch (InterruptedException e) {
                System.out.println("  [" + prefix + "] 线程被中断");
                break;
            }
        }
    }
}

// ==================== 方式二：实现 Runnable 接口 ====================
class MyRunnable implements Runnable {
    private String prefix;

    public MyRunnable(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("  [" + prefix + "/" + Thread.currentThread().getName() + "] 第" + i + "次输出");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("  [" + prefix + "] 线程被中断");
                break;
            }
        }
    }
}
