package action;

import model.entity.*;
import model.service.PlantDistrictService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
public class PlantDistrictAction {
    private PlantDistrict plantDistrict;
    private List<PlantDistrict> plantDistricts;
    private List<SendPlant> sendPlants;
    private List<Images> images;
    private Plant plant;
    private Double lat;
    private Double log;
    private double EARTH_RADIUS = 6370996.81;  //地球半径

    @Resource
    PlantDistrictService plantDistrictService;
    public String list(){
        plantDistricts = plantDistrictService.getPlantdistris(plantDistrict);
        return "list";
    }
    public String add(){
        return "edit";
    }
    public String edit(){
        plantDistrict = plantDistrictService.getPlantdistri(plantDistrict);
        return "edit";
    }
    public String save() throws Exception {
        plantDistrictService.savePlantdistri(plantDistrict);
        return "save";
    }
    public String delete() {
        plantDistrictService.deletePlantdistri(plantDistrict);
        return "save";
    }

    //附近搜索
    public String getPlantsByDis(){
        List<SendPlant> tempPlantList = new ArrayList<SendPlant>();
        List<SendPlant> sendPlantList = new ArrayList<SendPlant>();
        plantDistricts = plantDistrictService.getPlantdistris(plantDistrict);

        double pk = Math.PI/180;
        double p1_lat = lat * pk;
        double p1_log = log * pk;

        for (PlantDistrict plantdistri:plantDistricts){
            SendPlant sendPlant = new SendPlant();
            List<Coordinate> coordinates = new ArrayList<Coordinate>();
            Coordinate coordinate = new Coordinate();
            double plant_lat = plantdistri.getLat();
            double plant_log = plantdistri.getLog();
            double p2_lat = plant_lat * pk;
            double p2_log = plant_log * pk;
            double t1 = Math.cos(p1_lat) * Math.cos(p1_log) * Math.cos(p2_lat) * Math.cos(p2_log);
            double t2 = Math.cos(p1_lat) * Math.sin(p1_log) * Math.cos(p2_lat) * Math.sin(p2_log);
            double t3 = Math.sin(p1_lat) * Math.sin(p2_lat);
            double distance = EARTH_RADIUS * Math.acos(t1+t2+t3);
            if (distance <= 100.0){
                plant = plantDistrictService.getPlant(plant_lat,plant_log);

                sendPlant.setId(plant.getId());
                sendPlant.setName(plant.getName());
                sendPlant.setLatName(plant.getLatName());
                sendPlant.setAliaseName(plant.getAliaseName());
                sendPlant.setFamilyName(plant.getFamilyName());
                sendPlant.setGenusName(plant.getGenusName());
                sendPlant.setDistribution(plant.getDistribution());
                sendPlant.setExterion(plant.getExterion());
                sendPlant.setFlower(plant.getFlower());
                sendPlant.setBranch(plant.getBranch());
                sendPlant.setLeaf(plant.getLeaf());
                sendPlant.setFruit(plant.getFruit());
                //添加坐标
                coordinate.setLat(plant_lat);
                coordinate.setLog(plant_log);
                coordinates.add(coordinate);
                sendPlant.setCoordinateList(coordinates);
                //添加图片
                images = plantDistrictService.getImgs(plant_lat,plant_log);
                sendPlant.setPlantImage(images);
                tempPlantList.add(sendPlant);
            }
        }
        while (!tempPlantList.isEmpty()) {
            SendPlant tempPlant = tempPlantList.get(0);
            String name = "";
            Iterator<SendPlant> iterator = tempPlantList.iterator();
            while (iterator.hasNext()) {
                SendPlant sendPlant = iterator.next();
                if (name.equals("")) {
                    name = tempPlant.getName();
                    iterator.remove();
                    continue;
                }
                if (!sendPlant.getName().equals(name)) {
                    continue;
                }
                tempPlant.getCoordinateList().add(sendPlant.getCoordinateList().get(0));
                iterator.remove();
            }
            sendPlantList.add(tempPlant);
        }
        this.setSendPlants(sendPlantList);

        return "plants";
    }



    //get、set方法
    public PlantDistrict getPlantDistrict() {
        return plantDistrict;
    }

    public void setPlantDistrict(PlantDistrict plantDistrict) {
        this.plantDistrict = plantDistrict;
    }

    public List<PlantDistrict> getPlantDistricts() {
        return plantDistricts;
    }

    public void setPlantDistricts(List<PlantDistrict> plantDistricts) {
        this.plantDistricts = plantDistricts;
    }

    public List<SendPlant> getSendPlants() {
        return sendPlants;
    }

    public void setSendPlants(List<SendPlant> sendPlants) {
        this.sendPlants = sendPlants;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLog() {
        return log;
    }

    public void setLog(Double log) {
        this.log = log;
    }

    public double getEARTH_RADIUS() {
        return EARTH_RADIUS;
    }

    public void setEARTH_RADIUS(double EARTH_RADIUS) {
        this.EARTH_RADIUS = EARTH_RADIUS;
    }
}
