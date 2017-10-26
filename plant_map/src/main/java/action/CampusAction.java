package action;

import model.entity.Campus;
import model.service.CampusService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 林中漫步 on 2017/10/25.
 */

public class CampusAction {
    private Campus campus;
    private List<Campus> campuses;
    @Resource
    CampusService campusService;
    public  String list(){
        campuses=campusService.getCampuses(campus);
        return "list";
    }

    public String sendCampusList()
    {
        campuses=campusService.sendCampusList(campus);
        return  "sendList";
    }
    public String add(){return  "add";}

    public String edit(){
        campus=campusService.getCampus(campus);
        return "edit";
    }

    public String save() throws Exception {
        campusService.saveCampus(campus);
        return  "save";
    }
    public String delete()
    {
        campusService.deleteCampus(campus);
        return  "save";
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    public List<Campus> getCampuses() {
        return campuses;
    }

    public void setCampuses(List<Campus> campuses) {
        this.campuses = campuses;
    }


}
