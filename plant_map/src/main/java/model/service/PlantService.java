package model.service;

import model.dao.PlantDao;
import model.entity.Coordinate;
import model.entity.FuzzyPlant;
import model.entity.Images;
import model.entity.Plant;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
@Service
public class PlantService {
    @Resource
    PlantDao plantDao;
    public Plant getPlant(Plant plant){
        if(plant == null) plant = new Plant();
        return plantDao.get(plant);
    }

    public List<Plant> getPlants(Plant plant, String plantName){
        if(plant == null) plant = new Plant();
        if (plantName == null) plantName = "";
        return plantDao.find(plant,plantName);
    }

    public Integer savePlant(Plant plant) throws Exception {
        if(plant == null){
            throw new Exception("用户信息不能为空...");
        }
        if(plant.getId() == null)
            return plantDao.insert(plant);
        else
            return plantDao.update(plant);
    }

    public Integer deletePlant(Plant plant){
        if(plant == null) plant = new Plant();
        return plantDao.delete(plant);
    }

    //按植物名获取植物基本信息
    public Plant getPlant(String plantName){
        return plantDao.getPlant(plantName);
    }

    //按植物名获取植物分布
    public List<Coordinate> getPlantDis(String plantName){
        return plantDao.getPlantDistrict(plantName);
    }

    //按植物名获取植物图片
    public List<Images> getPlantImg(String plantName){
        return plantDao.getImg(plantName);
    }

    //按模糊的植物名查询植物列表
    public List<FuzzyPlant> getFuzzyPlants(String plantName){
        return plantDao.getFuzzyPlants(plantName);
    }

    //按校区名，区域名，植物名查询植物坐标
    public List<Coordinate> getSectionPlantDis(String campusName,String sectionName,String plantName){
        return plantDao.getSectionPlantDis(campusName, sectionName, plantName);
    }
}
