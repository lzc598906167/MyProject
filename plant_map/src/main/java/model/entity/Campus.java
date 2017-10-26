package model.entity;

/**
 * Created by 林中漫步 on 2017/10/24.
 * 校区
 */
public class Campus {
    private Integer id;
    private String name;
    private Double lat;
    private Double log;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
