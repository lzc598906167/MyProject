package model.service;

import model.dao.CampusDao;
import model.entity.Campus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/25.
 */
@Service
public class CampusService  {
    @Resource
    CampusDao campusDao;

    public Campus getCampus(Campus campus) {
        if(campus==null)campus=new Campus();
        return campusDao.get(campus);
    }

    public List<Campus> getCampuses(Campus campus) {
        if(campus==null)campus=new Campus();
        return campusDao.find(campus);
    }

    public List<Campus> sendCampusList(Campus campus) {
        if(campus==null)campus=new Campus();
        return campusDao.sendList(campus);
    }

    public Integer saveCampus(Campus campus) throws Exception {
        if(campus==null)
        {
            throw  new Exception("用户的信息不能为空...");
        }
        if(campus.getId()==null)
            return  campusDao.insert(campus);
      else
        return campusDao.update(campus);
    }


    public Integer deleteCampus(Campus campus) {
        if(campus==null) campus=new Campus();
        return campusDao.delete(campus);
    }
}
