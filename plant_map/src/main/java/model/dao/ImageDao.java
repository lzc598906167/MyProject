package model.dao;

import model.entity.Image;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/25.
 */
@Repository
public interface ImageDao {
    Image get(Image image);
    List<Image> find(Image image);
    Integer insert(Image image);
    Integer update(Image image);
    Integer delete(Image image);
}
