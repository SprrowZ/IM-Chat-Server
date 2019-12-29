package net.ryecatcher.web.italker.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.ryecatcher.web.italker.push.provider.GsonProvider;
import net.ryecatcher.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

/**
 * 描述:
 * servlet映射用web.xml
 *
 * @Author Zzg
 * @Create 2018-08-25 22:33
 */
public class Application  extends ResourceConfig {//继承类
    public Application(){
        //packages("net.ryecatcher.web.italker.push.service");
        packages(AccountService.class.getPackage().getName());//注册service包，两种方式，底下这个一看就更灵活
        //注册Json解析器,默认的Jackson解析器在处理boolean变量时有问题
         register(JacksonJsonProvider.class);
        //替换解析器为Gson------有点问题，暂时不用这个
//        register(GsonProvider.class);
        //注册log
        register(Logger.class);
    }

}
