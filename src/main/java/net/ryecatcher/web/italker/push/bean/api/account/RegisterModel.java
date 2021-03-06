package net.ryecatcher.web.italker.push.bean.api.account;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * describe:用户注册实体类，入口在service下的AccountService里
 *
 * @Author
 * @Create
 */
public class RegisterModel {
    @Expose   //必须加此注解，否则Gson不进行解析，配置是在GsonProvider中的
    private String account;
    @Expose
    private String password;
    @Expose
    private String name;
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


    /**
     * 对输入的信息进行校验，不然用户输入的json中没有账号密码以及name就会报空指针
     * 同理LoginModel
     * @param registerModel
     * @return
     */
    public static boolean check(RegisterModel registerModel){
        return registerModel!=null
                &&!Strings.isNullOrEmpty(registerModel.account)
                &&!Strings.isNullOrEmpty(registerModel.password)
                &&!Strings.isNullOrEmpty(registerModel.name);
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
