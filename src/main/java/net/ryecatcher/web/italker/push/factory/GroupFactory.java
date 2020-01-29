package net.ryecatcher.web.italker.push.factory;

import net.ryecatcher.web.italker.push.bean.api.message.MessageCreateModel;
import net.ryecatcher.web.italker.push.bean.db.Group;
import net.ryecatcher.web.italker.push.bean.db.GroupMember;
import net.ryecatcher.web.italker.push.bean.db.User;

import java.util.Set;

/**
 * Create by  -SQ-
 * at 2020/1/26 22:52
 *
 * @description:
 */
public class GroupFactory {
    public static Group findById(String groupId) {
        // TODO: 2020/1/26
        return null;
    }

    public static Set<GroupMember> getMembers(Group group) {
    return null;
    }

    public static Group findById(User sender, MessageCreateModel model) {
        // TODO: 2020/1/26 通过id找到群，并且判断发送人是否是群成员，不是的话，是没有权限发送的 
        return null;
    }
}
