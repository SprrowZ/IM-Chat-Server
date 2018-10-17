package net.ryecatcher.web.italker.push.bean.api.account;

import com.google.gson.annotations.Expose;

/**
 * describe:用户注册实体类，入口在service下的AccountService里
 *
 * @Author Zzg
 * @Create 2018-10-16 19:55
 */
public class RegisterModel {
    @Expose
    private String account;
    @Expose
    private String password;
    @Expose
    private String name;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
