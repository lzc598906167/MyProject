package model.dao;

import model.entity.Campus;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/25.
 */
@Repository
public interface CampusDao {
    Campus get(Campus campus);
    List<Campus> find(Campus campus);
    List<Campus> sendList(Campus campus);
    Integer insert(Campus campus);
    Integer update(Campus campus);
    Integer delete(Campus campus);
}
