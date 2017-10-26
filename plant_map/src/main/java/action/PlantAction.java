package action;

import model.entity.*;
import model.service.PlantService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
public class PlantAction {

    private String plantname;
    private String sectionname;
    private String campusname;
    private List<Coordinate> coordinates;
    private List<Images> images;
    private List<SendPlant> sendPlants;
    private Plant plant;
    private List<Plant> plants;
    private List<FuzzyPlant> fuzzyPlants;

    @Resource
    PlantService plantService;

    public String list(){
        plants = plantService.getPlants(plant,plantname);
        return "list";
    }
    public String add(){
        return "edit";
    }
    public String edit(){
        plant = plantService.getPlant(plant);
        return "edit";
    }
    public String save() throws Exception {
        plantService.savePlant(plant);
        return "save";
    }
    public String delete() {
        plantService.deletePlant(plant);
        return "save";
    }
    public String inquiry(){
        return "inquiry";
    }

    //按植物名获取植物基本信息
    //按植物名获取植物分布
    //按植物名获取植物图片
    public String getPlantByName(){

        List<SendPlant> sendPlantList = new ArrayList<SendPlant>();

        plant = plantService.getPlant(plantname);
        coordinates = plantService.getPlantDis(plantname);
        images = plantService.getPlantImg(plantname);

        //封装基本信息和坐标和图片地址
        SendPlant sendp = new SendPlant();
        sendp.setId(plant.getId());
        sendp.setName(plant.getName());
        sendp.setLatName(plant.getLatName());
        sendp.setAliaseName(plant.getAliaseName());
        sendp.setFamilyName(plant.getFamilyName());
        sendp.setGenusName(plant.getGenusName());
        sendp.setDistribution(plant.getDistribution());
        sendp.setExterion(plant.getExterion());
        sendp.setFlower(plant.getFlower());
        sendp.setBranch(plant.getBranch());
        sendp.setLeaf(plant.getLeaf());
        sendp.setFruit(plant.getFruit());
        sendp.setBranch(plant.getBranch());
        //封装坐标
        sendp.setCoordinateList(coordinates);
        //封装图片信息
        sendp.setPlantImage(images);
        sendPlantList.add(sendp);

        this.setSendPlants(sendPlantList);

        return "plantname";
    }

    //按植物名模糊查询植物列表
    public String getPlantsByFuzzyName(){
        //获取植物列表
        fuzzyPlants = plantService.getFuzzyPlants(plantname);
        return "FuzzySections";
    }

    //按校区名，区域名，植物名查询植物列表
    public String getPlantsByThreeName(){
        List<SendPlant> sendPlantList = new ArrayList<SendPlant>();

        plant = plantService.getPlant(plantname);
        coordinates = plantService.getSectionPlantDis(campusname,sectionname,plantname);
        images = plantService.getPlantImg(plantname);

        //封装基本信息和坐标
        SendPlant sendp = new SendPlant();
        sendp.setId(plant.getId());
        sendp.setName(plant.getName());
        sendp.setLatName(plant.getLatName());
        sendp.setAliaseName(plant.getAliaseName());
        sendp.setFamilyName(plant.getFamilyName());
        sendp.setGenusName(plant.getGenusName());
        sendp.setDistribution(plant.getDistribution());
        sendp.setExterion(plant.getExterion());
        sendp.setFlower(plant.getFlower());
        sendp.setBranch(plant.getBranch());
        sendp.setLeaf(plant.getLeaf());
        sendp.setFruit(plant.getFruit());
        sendp.setBranch(plant.getBranch());
        //封装坐标
        sendp.setCoordinateList(coordinates);
        //封装图片信息
        sendp.setPlantImage(images);
        sendPlantList.add(sendp);

        this.setSendPlants(sendPlantList);

        return "ThreePlantName";
    }

    //get、set方法
    public String getPlantname() {
        return plantname;
    }

    public void setPlantname(String plantname) {
        this.plantname = plantname;
    }

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public String getCampusname() {
        return campusname;
    }

    public void setCampusname(String campusname) {
        this.campusname = campusname;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public List<SendPlant> getSendPlants() {
        return sendPlants;
    }

    public void setSendPlants(List<SendPlant> sendPlants) {
        this.sendPlants = sendPlants;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public List<FuzzyPlant> getFuzzyPlants() {
        return fuzzyPlants;
    }

    public void setFuzzyPlants(List<FuzzyPlant> fuzzyPlants) {
        this.fuzzyPlants = fuzzyPlants;
    }
}
