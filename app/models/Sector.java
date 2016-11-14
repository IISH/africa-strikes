package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Sector extends Model {

    @Id
    public long id;
    public String sectorName;

    public Sector(long sectorId, String name)
    {
        this.id = sectorId;
        this.sectorName = name;
    }

    public long getId()
    {
        return id;
    }

    public static Finder<Integer, Sector> find = new Finder<>(Sector.class);

    public static List<Sector> getAllSectors()
    {
        List<Sector> sectors = Ebean.find(Sector.class).findList();
        return sectors;
    }
}
