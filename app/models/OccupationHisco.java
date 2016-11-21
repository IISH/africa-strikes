package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by Igor on 11/15/2016.
 */
@Entity
public class OccupationHisco extends Model{

    @Id
    public long id;
    public String occupationHiscoText;

    public OccupationHisco()
    {}

    public static Model.Finder<Integer, OccupationHisco> find = new Model.Finder<>(OccupationHisco.class);

    public static List<OccupationHisco> getAllOccupations()
    {
        List<OccupationHisco> hiscoOccupations = Ebean.find(OccupationHisco.class).findList();
        return hiscoOccupations;
    }
}
