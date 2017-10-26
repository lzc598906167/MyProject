package model.entity;

/**
 * Created by 林中漫步 on 2017/10/24.
 * 植物信息
 */
public class Plant {
    private Integer id;
    private String name;
    private String latName;//拉丁名
    private String aliaseName;
    private String familyName;
    private String genusName;
    private String distribution;
    private String exterion;
    private String flower;
    private String branch;
    private String leaf;
    private String fruit;

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

    public String getLatName() {
        return latName;
    }

    public void setLatName(String latName) {
        this.latName = latName;
    }

    public String getAliaseName() {
        return aliaseName;
    }

    public void setAliaseName(String aliaseName) {
        this.aliaseName = aliaseName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGenusName() {
        return genusName;
    }

    public void setGenusName(String genusName) {
        this.genusName = genusName;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getExterion() {
        return exterion;
    }

    public void setExterion(String exterion) {
        this.exterion = exterion;
    }

    public String getFlower() {
        return flower;
    }

    public void setFlower(String flower) {
        this.flower = flower;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getLeaf() {
        return leaf;
    }

    public void setLeaf(String leaf) {
        this.leaf = leaf;
    }

    public String getFruit() {
        return fruit;
    }

    public void setFruit(String fruit) {
        this.fruit = fruit;
    }
}
