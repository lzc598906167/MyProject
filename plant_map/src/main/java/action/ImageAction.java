package action;

import model.entity.Image;
import model.service.ImageService;
import org.apache.struts2.ServletActionContext;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/25.
 */
public class ImageAction {
    private Image image;
    private List<Image> images;
    //文件上传
    private File upload;
    private String uploadContextType;
    private String uploadFileName;

    @Resource
    ImageService imageService;


    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Image> getImages() {
        return images;
    }

    public String getSavePath() throws Exception{
        return ServletActionContext.getServletContext().getRealPath("/");
    }

    public File getUpload() {
        return upload;
    }

    public void setUpload(File upload) {
        this.upload = upload;
    }

    public String getUploadContextType() {
        return uploadContextType;
    }

    public void setUploadContextType(String uploadContextType) {
        this.uploadContextType = uploadContextType;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String list(){
        images = imageService.getImages(image);
        return "list";
    }

    public String save()throws Exception{
        if( (upload != null) && (upload.exists()) ){
            String dateStr0 = new Date().toString().trim();
            String dateStr1 = dateStr0.replace(" ", "");
            String dateStr2 = dateStr1.replace(":", "");

            FileOutputStream fos = new FileOutputStream(getSavePath() + "\\UploadImages\\" + dateStr2 + "_" + uploadFileName);
            FileInputStream fis = new FileInputStream(upload);

            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            fis.close();

            //原来的图片删除
            String url = image.getUrlAddress();
            if((!url.equals("")) && (url != null) ){
                String[] strList = url.split("/");
                File file = new File(getSavePath()+"\\UploadImages\\" +strList[2]);
                if(file.exists()){
                    file.delete();
                }
            }
            image.setUrlAddress("/UploadImages" + "/" + dateStr2 + "_" + uploadFileName);
        }
        imageService.saveImage(image);
        return "save";
    }
    public String edit(){
        image = imageService.getImage(image);
        return "edit";
    }

    public String delete()throws Exception {
        image = imageService.getImage(image);
        String url = image.getUrlAddress();
        String[] strList = url.split("/");
        File file = new File(getSavePath()+"\\UploadImages\\" +strList[2]);
        file.delete();
        imageService.deleteImage(image);
        return "save";
    }

}
