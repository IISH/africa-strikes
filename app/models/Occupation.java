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
public class Occupation {

    @Id
    public long id;
    public String occupationText;

    public Occupation()
    {}

    public long getId()
    {
        return id;
    }

    public static Model.Finder<Integer, Occupation> find = new Model.Finder<>(Occupation.class);

    public static List<Occupation> getAllOccupations()
    {
        List<Occupation> occupations = Ebean.find(Occupation.class).findList();
        return occupations;
    }
}
