package action;

import model.entity.*;
import model.service.SectionService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/26.
 */
public class SectionAction {
    private Section section;
    private List<Section> sections;
    private String sectionname;
    private String campusname;
    private List<Plant> plants;
    private List<SendPlant> sendPlants;
    private Coordinate sectionCoordinate;
    private List<SendSection> sendSections;
    @Resource
    SectionService sectionService;

    public String list(){
        sections = sectionService.getSections(section);
        return "list";
    }
    public String add(){
        return "edit";
    }
    public String edit(){
        section = sectionService.getSection(section);
        return "edit";
    }
    public String save() throws Exception {
        sectionService.saveSection(section);
        return "save";
    }
    public String delete() {
        sectionService.deleteSection(section);
        return "save";
    }
    //按区域名和校区名获取植物列表
    public String getPlantBySectionName(){
        sectionCoordinate = sectionService.getSectionDis(sectionname,campusname);
        //创建一个List
        List<SendPlant> sendPlantList = new ArrayList<SendPlant>();

        //获取当前区域内的植物列表
        plants = sectionService.getPlants(sectionname,campusname);

        for(Plant plant:plants){
            //获取当前植物的id
            int id = plant.getId();
            //获取当前植物当前区域内的分布
            List<Coordinate> coordinates = sectionService.getPlantDis(sectionname,id,campusname);
            //获取当前植物图片地址
            List<Images> images = sectionService.getImg(id);

            SendPlant sendPlant = new SendPlant();

            //封装植物基本信息和当前区域内的分布
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
            sendPlant.setCoordinateList(coordinates);

            //封装图片信息
            sendPlant.setPlantImage(images);
            //把封装好的记录添加到List中
            sendPlantList.add(sendPlant);
        }

        this.setSendPlants(sendPlantList);
        return "sectionname";
    }

    //按区域名模糊查询区域列表
    public String getSectionsByFuzzyName(){
        List<SendSection> sendSectionList = new ArrayList<SendSection>();
        sendSectionList = sectionService.getFuzzySections(sectionname);
        this.setSendSections(sendSectionList);
        return "FuzzySections";
    }


    //get、set方法

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<Section> getSections() {
        return sections;
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

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public List<SendPlant> getSendPlants() {
        return sendPlants;
    }

    public void setSendPlants(List<SendPlant> sendPlants) {
        this.sendPlants = sendPlants;
    }

    public Coordinate getSectionCoordinate() {
        return sectionCoordinate;
    }

    public void setSectionCoordinate(Coordinate sectionCoordinate) {
        this.sectionCoordinate = sectionCoordinate;
    }

    public List<SendSection> getSendSections() {
        return sendSections;
    }

    public void setSendSections(List<SendSection> sendSections) {
        this.sendSections = sendSections;
    }
}
