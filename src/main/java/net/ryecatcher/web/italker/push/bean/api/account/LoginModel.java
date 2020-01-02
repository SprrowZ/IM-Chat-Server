package net.ryecatcher.web.italker.push.bean.api.account;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

public class LoginModel {
    @Expose   //必须加此注解，否则Gson不进行解析，配置是在GsonProvider中的
    private String account;
    @Expose
    private String password;

    @Expose   //简化流程，注册的时候就绑定PushId，可为空，不做校验
    private String pushId;

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }


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

    /**
     * 对输入的信息进行校验，不然用户输入的json中没有账号密码就会报空指针
     * @param loginModel
     * @return
     */
    public static boolean check(LoginModel loginModel){
        return loginModel!=null
                &&!Strings.isNullOrEmpty(loginModel.account)
                &&!Strings.isNullOrEmpty(loginModel.password);
    }
}
