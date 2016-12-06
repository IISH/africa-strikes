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
public class StrikeDefinition extends Model{

    @Id
    public long id;
    public String strikeDefinitionText;

    public StrikeDefinition(String strikeDefinition)
    {
        this.strikeDefinitionText = strikeDefinition;
    }

    public static Model.Finder<Integer, StrikeDefinition> find = new Model.Finder<>(StrikeDefinition.class);

    public static List<StrikeDefinition> getAllStrikeDefinitions()
    {
        List<StrikeDefinition> strikeDefinitions = Ebean.find(StrikeDefinition.class).findList();
        return strikeDefinitions;
    }
}
