package cn.edu.nwsuaf.liuzc.model.service;

import cn.edu.nwsuaf.liuzc.model.dao.RoomDao;
import cn.edu.nwsuaf.liuzc.model.entity.Room;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/22.
 */
@Service
public class RoomService {
    @Resource
    RoomDao roomDao;

    public Integer findSimple(){return  roomDao.findSimple();}

    public Integer findHard(){return  roomDao.findHard();}

    public Room get(int roomName){ return roomDao.get(roomName);}

    public Integer insert(int type,String owner) {
        List<Integer> roomNameList = roomDao.getRoomName();
        int roomName = 10000;
        for (Integer one : roomNameList)
        {
            if(one ==roomName)
            {
                roomName++;
            }else
            {
                break;
            }
        }
        roomDao.insert(roomName,type,0,owner);
        return  roomName;
    }

    public Integer delete(int roomName){ return  roomDao.delete(roomName);}

    public Integer getPeopleNumber(int roomName) {return  roomDao.getPeopleNumber(roomName);}
}
