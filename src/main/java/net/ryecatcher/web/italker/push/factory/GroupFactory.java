package net.ryecatcher.web.italker.push.factory;

import com.google.common.base.Strings;
import net.ryecatcher.web.italker.push.bean.api.group.GroupCreateModel;
import net.ryecatcher.web.italker.push.bean.api.message.MessageCreateModel;
import net.ryecatcher.web.italker.push.bean.db.Group;
import net.ryecatcher.web.italker.push.bean.db.GroupMember;
import net.ryecatcher.web.italker.push.bean.db.User;
import net.ryecatcher.web.italker.push.utils.Hib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Create by  -SQ-
 * at 2020/1/26 22:52
 *
 * @description:
 */
public class GroupFactory {
    /**
     * 通过群查找一个群
     * @param groupId
     * @return
     */
    public static Group findById(String groupId) {//直接get？
      return    Hib.query(session -> {
           return  session.get(Group.class,groupId);
        });

    }

    /**
     * 通过名字查找群，todo---感觉可以模糊查询个列表啊
     * @param name
     * @return
     */
    public static Group findByName(String name) {
        return  Hib.query(session -> {
          return   (Group)session.createQuery("from Group  where  lower(name) =:name")
                    .setParameter("name",name.toLowerCase())
                    .uniqueResult();
        });

    }

    public static Group findById(User sender, String groupId) {
        // : 2020/1/26 通过id找到群，并且判断发送人是否是群成员，不是的话，是没有权限发送的

            GroupMember member=getMember(sender.getId(),groupId);
            if (member!=null){
                return  member.getGroup();
            }
            return null;
    }

    /**
     * 在GroupMember表中查找一个群的所有成员
     * @param group
     * @return
     */
    public static Set<GroupMember> getMembers(Group group) {
       return  Hib.query(session -> {
          List<GroupMember> groupMembers= session.createQuery(" FROM GroupMember WHERE group=:group ")
                   .setParameter("group",group)
                   .list();
           return new HashSet<>(groupMembers);
       });
    }
   //获取一个人加入的所有群
    public static Set<GroupMember> getMembers(User user) {
        return  Hib.query(session -> {
            List<GroupMember> groupMembers= session.createQuery(" FROM GroupMember WHERE userId=:userId ")
                    .setParameter("userId",user.getId())
                    .list();
            return new HashSet<>(groupMembers);
        });
    }


    /**
     * 创建群聊：不仅要保存群，还要保存用户
     * @param creator
     * @param model
     * @param users
     * @return
     */
    public static Group create(User creator, GroupCreateModel model, List<User> users) {
        return Hib.query(session -> {
            Group group=new Group(creator,model);
            session.save(group);//存储到数据库中如此简洁高效

            GroupMember owner=new GroupMember(creator,group);
            //群主
            owner.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);
            session.save(owner);

            users.stream().forEach(user -> {
                GroupMember groupMember=new GroupMember(user,group);
                session.save(groupMember);
            });
             return group;
        });

    }

    /**
     * 获取一个群的成员，通过此来构造一个GroupCard
     * @param memberId
     * @param groupId
     * @return
     */
    public static GroupMember getMember(String memberId, String groupId) {
        return Hib.query(session -> (GroupMember)session.createQuery(
                "from GroupMember where userId=:userId and groupId =:groupId")
                    .setParameter("userId",memberId)
                    .setParameter("groupId",groupId)
                    .setMaxResults(1)
                    .uniqueResult()
         );

    }

    /**
     * 查找群，模糊匹配
     * @param name
     * @return
     */
    public static List<Group> search(String name){
        if (Strings.isNullOrEmpty(name))
            name="";
        final String searchName="%"+name+"%";
        return Hib.query(session -> {
         return     (List<Group>)session.createQuery("from Group  where lower(name) like :name")
                    .setParameter("name",searchName)
                    .setMaxResults(20)//至多查找二十个群
                    .list();
        });
    }


    /**
     * 给群添加新成员
     * @param group
     * @param insertUsers
     * @return
     */
    public static Set<GroupMember> addMembers(Group group,List<User> insertUsers){
        return Hib.query(session -> {
           Set<GroupMember> members=new HashSet<>();
           insertUsers.stream().forEach(user -> {
               GroupMember member=new GroupMember(user,group);
               session.save(member);
               //这里并没有从数据库中查询，所以关联的外键：userId，groupId可能拿不到
               members.add(member);
           });
           return members;
        });
    }
}
