package model.dao;

import model.entity.Coordinate;
import model.entity.FuzzyPlant;
import model.entity.Images;
import model.entity.Plant;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
@Repository
public interface PlantDao {
    Plant get(Plant plant);
    List<Plant> find(Plant plant, String plantName);
    Integer insert(Plant plant);
    Integer update(Plant plant);
    Integer delete(Plant plant);
    Plant getPlant(String plantName);//按植物名获取植物基本信息
    List<Coordinate> getPlantDistrict(String plantName);//按植物名获取植物分布
    List<Images> getImg(String plantName);//按植物名获取植物图片
    List<FuzzyPlant> getFuzzyPlants(String plantName); //按模糊的植物名查询植物列表
    //按校区名，区域名，植物名查询植物坐标
    List<Coordinate> getSectionPlantDis(String campusName,String sectionNname,String plantName);
}
