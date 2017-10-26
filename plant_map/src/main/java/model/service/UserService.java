package model.service;

import com.sun.xml.internal.ws.developer.Serialization;
import model.dao.UserDao;
import model.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
@Service
public class UserService {
    @Resource
    UserDao userDao;
    public List<User> getUsers(User user){
        if(user == null) user = new User();
        return userDao.find(user);
    }
    public Integer addUser(User user) throws Exception{
        return userDao.insert(user);
    }
    public Integer updateUser(Integer id) throws Exception{
        return userDao.update(id);
    }
    public List<User> getRegistUser(User user){
        if(user == null){
            user = new User();
        }
        return userDao.getUsers(user);
    }
}
