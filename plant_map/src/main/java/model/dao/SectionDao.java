package model.dao;

import model.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
@Repository
public interface SectionDao {
    Section get(Section section);
    List<Section> find(Section section);
    Integer insert(Section section);
    Integer update(Section section);
    Integer delete(Section section);
    List<Plant> getPlants(String sectionName, String campusName);//通过区域名获取植物信息
    //通过区域名、校区名和植物id获得植物分布
    List<Coordinate> getPlantDis(String sectionName, int plantId, String campusName);
    List<Images> getImg(int plantId);//按植物名获取植物图片
    Coordinate getSectionDis(String sectionName,String campusName);//按区域名和校区名获取区域坐标
    List<SendSection> getFuzzySections(String sectionName);//按模糊的区域名获取区域列表
}

