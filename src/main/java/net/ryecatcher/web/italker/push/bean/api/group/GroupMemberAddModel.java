package net.ryecatcher.web.italker.push.bean.api.group;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * Create by  -SQ-
 * at 2020/1/29 21:48
 *
 * @description:
 */
public class GroupMemberAddModel {
    @Expose
    private Set<String> users=new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public static boolean  check(GroupMemberAddModel model){
        return  !(model.users==null
                ||model.users.size()==0
        );
    }

}
