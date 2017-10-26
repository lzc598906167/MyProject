package model.dao;

import model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
@Repository
public interface UserDao {
    List<User> find(User user);
    Integer insert(User user);
    Integer update(Integer id);
    List<User> getUsers(User user);//获取提交注册申请的普通管理员信息
}
