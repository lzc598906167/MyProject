package model.entity;

/**
 * Created by 林中漫步 on 2017/10/24.
 * 植物的分布区域
 */
public class PlantDistrict {
    private Integer id;
    private Integer plantId;
    private Integer sectionId;
    private Double lat;
    private Double log;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlantId() {
        return plantId;
    }

    public void setPlantId(Integer plantId) {
        this.plantId = plantId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
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
}
