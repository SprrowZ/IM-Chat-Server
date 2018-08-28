package net.ryecatcher.web.italker.push.bean;

/**
 * describe:测试实体类
 *
 * @Author Zzg
 * @Create 2018-08-28 18:23
 */
public class User {
    String name;
    String sex;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
