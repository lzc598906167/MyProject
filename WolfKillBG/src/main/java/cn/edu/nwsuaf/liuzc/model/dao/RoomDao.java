package cn.edu.nwsuaf.liuzc.model.dao;

import cn.edu.nwsuaf.liuzc.model.entity.Room;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/22.
 */
@Repository
public interface RoomDao {
    Integer findSimple();//查询空闲的简单房间
    Integer findHard();//查询空闲的高级房间
    Room get(int roomName);//搜索指定的房间号
    Integer insert(Integer roomName,Integer type,Integer peopleNumber,String owner);//创建房间
    Integer delete(int roomName);//销毁指定房间
    List<Integer> getRoomName(); //获取所有房间号
    Integer getPeopleNumber(int roomName);//获取指定房间人数

}
