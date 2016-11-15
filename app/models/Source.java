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
public class Source {

    @Id
    public long id;
    public String sourceText;

    public Source()
    {}

    public static Model.Finder<Integer, Source> find = new Model.Finder<>(Source.class);

    public static List<Source> getAllSources()
    {
        List<Source> sources = Ebean.find(Source.class).findList();
        return sources;
    }
}
