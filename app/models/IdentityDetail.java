package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by sIgor on 11/15/2016.
 */
@Entity
public class IdentityDetail extends Model{

    @Id
    public Long id;
    public String identityDetailText;

    public IdentityDetail(String identityDetail)
    {
        this.identityDetailText = identityDetail;
    }

    public static Model.Finder<Integer, IdentityDetail> find = new Model.Finder<>(IdentityDetail.class);

    public static List<IdentityDetail> getAllIdentityDetails()
    {
        List<IdentityDetail> identityDetails = Ebean.find(IdentityDetail.class).findList();
        return identityDetails;
    }
}
