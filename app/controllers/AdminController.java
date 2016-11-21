package controllers;

import models.Article;
import models.Strike;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.admin;
import javax.inject.Inject;
import java.io.File;
import static play.libs.Json.toJson;

/**
 * Created by Igor on 11/4/2016.
 */
public class AdminController extends Controller{

    @Inject FormFactory formFactory;

    public Result index()
    {
        return ok(admin.render("", Strike.getAllStrikesAsArray(), formFactory.form(Strike.class)));
    }

    public Result updateSelectedStrike()
    {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        System.out.println(toJson(body));

        return ok();
    }

    public static File getArticleOfStrike(Strike strike)
    {
        Article article = strike.getArticle();
        return article.article;
    }

}
