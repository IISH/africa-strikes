package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by Igor van der Bom on 24-10-2017.
 */
@Entity
public class Label extends Model {

    @Id
    public Long id;
    public String label;

    public Label(String label){
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static Model.Finder<Integer, Label> find = new Model.Finder<>(Label.class);

    public static List<Label> getAllLabels(){
        List<Label> labels = Ebean.find(Label.class).findList();
        return labels;
    }

    public static Label getLabelById(Long id){
        Label lbl = Ebean.find(Label.class, id);
        return lbl;
    }

    public static void addLabelToDatabase(Long id, String text){
        Label lbl = new Label(text);
        if(Ebean.find(Label.class, id) == null){
            Ebean.beginTransaction();
            lbl.save();
            Ebean.commitTransaction();
            Ebean.endTransaction();
        }
    }

}
