import java.util.ArrayList;
import java.util.List;
//内存容器
public class Data {
    private Data() {
        throw new RuntimeException("工具类不能实例化");
    }

    //静态集合
    public static final List<Person> studentList = new ArrayList<>();
}
