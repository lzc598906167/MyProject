package model.entity;

/**
 * Created by 林中漫步 on 2017/10/24.
 * 模糊搜索的实体类
 */
public class FuzzyPlant {
    private String plantName;
    private String sectionName;
    private String campusName;

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }
}
