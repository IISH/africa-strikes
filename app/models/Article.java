package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.File;
import java.util.List;

/**
 * Created by Igor on 11/18/2016.
 */
@Entity
public class Article extends Model{

    @Id
    public Long id;
    public String articleName;

    public Article(String articleName)
    {
        this.articleName = articleName;
    }

    public static Model.Finder<Integer, Article> find = new Model.Finder<>(Article.class);

    public static List<Article> getAllArticles()
    {
        List<Article> articles = Ebean.find(Article.class).findList();
        return articles;
    }
}
