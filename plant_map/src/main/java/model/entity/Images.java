package model.entity;

/**
 * Created by 林中漫步 on 2017/10/24.
 * 发送给前端的图片实体
 */
public class Images {
    private String type;
    private String urlAddress;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }
}
