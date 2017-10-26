package model.service;

import com.sun.xml.internal.ws.developer.Serialization;
import model.dao.ImageDao;
import model.entity.Image;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/25.
 */
@Service
public class ImageService {
    @Resource
    ImageDao imageDao;

    public Image getImage(Image image)
    {
        if(image==null)image=new Image();
        return  imageDao.get(image);
    }

    public List<Image> getImages(Image image)
    {
        if(image==null) image=new Image();
        return  imageDao.find(image);
    }

    public Integer saveImage(Image image) throws Exception
    {
        if(image == null){
            throw new Exception("图片信息不能为空...");
        }
        if(image.getId() == null)
            return imageDao.insert(image);
        else
            return imageDao.update(image);
    }
    public Integer deleteImage(Image image){
        if(image == null) image = new Image();
        return imageDao.delete(image);
    }
}
