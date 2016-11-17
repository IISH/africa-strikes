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
public class IdentityElement extends Model {

    @Id
    public long id;
    public String identityElementText;

    public IdentityElement()
    {}

    public static Model.Finder<Integer, IdentityElement> find = new Model.Finder<>(IdentityElement.class);

    public static List<IdentityElement> getAllIdentityElements()
    {
        List<IdentityElement> identityElements = Ebean.find(IdentityElement.class).findList();
        return identityElements;
    }
}
