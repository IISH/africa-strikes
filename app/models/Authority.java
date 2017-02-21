package models;

import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Igor on 2/20/2017.
 */
@Entity
public class Authority extends Model{

    @Id
    private Integer id;
    private String role;

    public static Model.Finder<Integer, Authority> find = new Model.Finder<>(Authority.class);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
