package model.service;

import model.dao.PlantDistrictDao;
import model.entity.Images;
import model.entity.Plant;
import model.entity.PlantDistrict;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
@Service
public class PlantDistrictService {
    @Resource
    PlantDistrictDao plantDistrictDao;
    public PlantDistrict getPlantdistri(PlantDistrict plantDistrict){
        if(plantDistrict == null) plantDistrict = new PlantDistrict();
        return plantDistrictDao.get(plantDistrict);
    }

    public List<PlantDistrict> getPlantdistris(PlantDistrict plantDistrict){
        if(plantDistrict == null) plantDistrict = new PlantDistrict();
        return plantDistrictDao.find(plantDistrict);
    }

    public Integer savePlantdistri(PlantDistrict plantDistrict) throws Exception {
        if(plantDistrict == null){
            throw new Exception("用户信息不能为空...");
        }
        if(plantDistrict.getId() == null)
            return plantDistrictDao.insert(plantDistrict);
        else
            return plantDistrictDao.update(plantDistrict);
    }

    public Integer deletePlantdistri(PlantDistrict plantDistrict){
        if(plantDistrict == null) plantDistrict = new PlantDistrict();
        return plantDistrictDao.delete(plantDistrict);
    }

    //通过坐标获取植物信息
    public Plant getPlant(double lat, double log){
        return plantDistrictDao.getPlant(lat,log);
    }

    //通过坐标获取植物图片
    public List<Images> getImgs(double lat, double log){
        return plantDistrictDao.getImgs(lat,log);
    }
}
