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
public class CauseOfDispute extends Model{

    @Id
    public long id;
    public String causeOfDisputeText;

    public CauseOfDispute(String causeOfDispute)
    {
        this.causeOfDisputeText = causeOfDispute;
    }

    public static Model.Finder<Integer, CauseOfDispute> find = new Model.Finder<>(CauseOfDispute.class);

    public static List<CauseOfDispute> getAllCausesOfDisputes()
    {
        List<CauseOfDispute> causeOfDisputes = Ebean.find(CauseOfDispute.class).findList();
        return causeOfDisputes;
    }
}
