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
public class CompanyName {

    @Id
    public Long id;
    public String companyNameText;

    public CompanyName(String companyName)
    {
        this.companyNameText = companyName;
    }

    public static Model.Finder<Integer, CompanyName> find = new Model.Finder<>(CompanyName.class);

    public static List<CompanyName> getAllCompanyNames()
    {
        List<CompanyName> companyNames = Ebean.find(CompanyName.class).findList();
        return companyNames;
    }
}
