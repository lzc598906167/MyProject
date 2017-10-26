package action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import model.entity.User;
import model.service.UserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
public class UserAction {
    private User user;
    private List<User> users;

    @Resource
    UserService userService;

    public String login(){
        users = userService.getUsers(user);
        ActionContext.getContext().getSession().put("username",user.getName());
        for(User u : users){
            if((u.getName().equals(user.getName())) && (u.getPasswd().equals(user.getPasswd())) ){
                if("administrator".equals(u.getType())){
                    ActionContext.getContext().getSession().put("name", "administrator");
                    return Action.SUCCESS;
                }
                else if("manager".equals(u.getType())){
                    ActionContext.getContext().getSession().put("name", "manager");
                    return Action.SUCCESS;
                }
                else{
                    ActionContext.getContext().getSession().put("error", "用户没有登录权限，等待管理员同意注册请求！");
                    return Action.INPUT;
                }
            }
        }
        ActionContext.getContext().getSession().put("error", "登录出错，请查看输入信息！");
        return Action.INPUT;
    }

    public String quit(){
        ActionContext.getContext().getSession().clear();
        return "quit";
    }

    //返回注册界面
    public String regist(){
        return "edit";
    }


    //添加管理员，注册时用到
    public String add() throws Exception{
        userService.addUser(user);
        return "adduser";
    }

    //修改用户信息，超级管理员验证普通管理员时用到
    public String update() throws Exception {
        userService.updateUser(user.getId());
        return "updateuser";
    }

    //获取提交注册申请的普通管理员信息
    public String getRegistUsers() throws Exception{
        users = userService.getRegistUser(user);
        return "registUsers";
    }


    //get、set方法
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
