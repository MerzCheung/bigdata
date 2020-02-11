package ming.zhang.boot_hbase.service;

import lombok.Data;

/**
 * @author merz
 * @Description:
 */
public class Per {
    private String name;

    private String age;

    @Override
    public String toString() {
        return "Per{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
