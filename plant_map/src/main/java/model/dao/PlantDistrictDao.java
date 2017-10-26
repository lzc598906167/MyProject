package model.dao;

import model.entity.Images;
import model.entity.Plant;
import model.entity.PlantDistrict;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
@Repository
public interface PlantDistrictDao {
    PlantDistrict get(PlantDistrict plantDistrict);
    List<PlantDistrict> find(PlantDistrict plantDistrict);
    Integer insert(PlantDistrict plantDistrict);
    Integer update(PlantDistrict plantDistrict);
    Integer delete(PlantDistrict plantDistrict);
    Plant getPlant(double lat, double log);  //通过坐标获取植物信息
    List<Images> getImgs(double lat, double log);  //通过坐标获取植物图片
}
