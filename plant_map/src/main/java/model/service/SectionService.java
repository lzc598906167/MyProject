package model.service;

import model.dao.SectionDao;
import model.entity.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
@Service
public class SectionService {
    @Resource
    SectionDao sectionDao;
    public Section getSection(Section section){
        if(section == null) section = new Section();
        return sectionDao.get(section);
    }

    public List<Section> getSections(Section section){
        if(section == null) section = new Section();
        return sectionDao.find(section);
    }

    public Integer saveSection(Section section) throws Exception {
        if(section == null){
            throw new Exception("用户信息不能为空...");
        }
        if(section.getId() == null)
            return sectionDao.insert(section);
        else
            return sectionDao.update(section);
    }

    public Integer deleteSection(Section section){
        if(section == null) section = new Section();
        return sectionDao.delete(section);
    }

    //通过区域名获取植物信息
    public List<Plant> getPlants(String sectionname, String campusname){
        return sectionDao.getPlants(sectionname, campusname);
    }

    //通过区域名和植物id获得植物分布
    public List<Coordinate> getPlantDis(String sectionname, int plantid, String campusname){
        return sectionDao.getPlantDis(sectionname,plantid,campusname);
    }

    //按植物名获取植物图片
    public List<Images> getImg(int plantid){
        return sectionDao.getImg(plantid);
    }

    //按区域名获取区域坐标
    public Coordinate getSectionDis(String sectionname,String campusname){
        return sectionDao.getSectionDis(sectionname,campusname);
    }

    //按模糊的区域名获取区域列表
    public List<SendSection> getFuzzySections(String sectionname){
        return sectionDao.getFuzzySections(sectionname);
    }

}
