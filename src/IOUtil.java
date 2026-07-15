import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class IOUtil {
    private IOUtil() {
        throw new RuntimeException("工具类不能实例化");
    }

    //文本文件读写（转换流 + UTF-8）

    //1.
    public static String readTextFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在：" + filePath + "，将使用初始数据");
        } catch (IOException e) {
            System.out.println("读取文件失败：" + e.getMessage());
        }
        return sb.toString().trim();
    }

    //2.
    public static String readTextFileFinally(String filePath) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在：" + filePath + "，将使用初始数据");
        } catch (IOException e) {
            System.out.println("读取文件失败：" + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString().trim();
    }

    //1.0
    public static void writeTextFile(String filePath, String content) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))) {
            bw.write(content);
            bw.flush();
            System.out.println("文本文件已保存：" + filePath);
        } catch (IOException e) {
            System.out.println("写入文件失败：" + e.getMessage());
        }
    }

    //2.0
    public static void writeTextFileFinally(String filePath, String content) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
            bw.write(content);
            bw.flush();
            System.out.println("文本文件已保存：" + filePath);
        } catch (IOException e) {
            System.out.println("写入文件失败：" + e.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //二、对象序列化 / 反序列化（压缩打包 解压打开）

    /**
     * 序列化集合到文件 - try-with-resources 自动释放
     */
    public static void serializeList(String filePath, List<Person> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(list);
            oos.flush();
            System.out.println("序列化成功：" + filePath + "（共 " + list.size() + " 条数据）");
        } catch (IOException e) {
            System.out.println("序列化失败：" + e.getMessage());
        }
    }

    /**
     * 序列化集合到文件 - try-catch-finally 手动关闭
     */
    public static void serializeListFinally(String filePath, List<Person> list) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filePath));
            oos.writeObject(list);
            oos.flush();
            System.out.println("序列化成功：" + filePath + "（共 " + list.size() + " 条数据）");
        } catch (IOException e) {
            System.out.println("序列化失败：" + e.getMessage());
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 反序列化还原集合 - try-with-resources 自动释放
     */
    @SuppressWarnings("unchecked")
    public static List<Person> deserializeList(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                System.out.println("反序列化成功：" + filePath);
                return (List<Person>) obj;
            }
        } catch (FileNotFoundException e) {
            System.out.println("序列化文件不存在：" + filePath + "，将使用初始数据");
        } catch (IOException e) {
            System.out.println("反序列化失败：" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("类未找到，版本不兼容：" + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * 反序列化还原集合 - try-catch-finally 手动关闭
     */
    @SuppressWarnings("unchecked")
    public static List<Person> deserializeListFinally(String filePath) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(filePath));
            Object obj = ois.readObject();
            if (obj instanceof List) {
                System.out.println("反序列化成功：" + filePath);
                return (List<Person>) obj;
            }
        } catch (FileNotFoundException e) {
            System.out.println("序列化文件不存在：" + filePath + "，将使用初始数据");
        } catch (IOException e) {
            System.out.println("反序列化失败：" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("类未找到：" + e.getMessage());
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<>();
    }
}
