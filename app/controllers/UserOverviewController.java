package controllers;

import com.avaje.ebean.Ebean;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import security.Authorized;
import security.Secured;
import views.html.userOverview;
import play.data.FormFactory;
import javax.inject.Inject;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * Created by Igor on 2/17/2017.
 */
@Security.Authenticated(Secured.class)
@Authorized.AdminAuthorized
public class UserOverviewController extends Controller{

    @Inject FormFactory formFactory;
    @Inject SecurityController securityController;
    private User userSelected = null;

    public Result index(){
        Config conf = ConfigFactory.load();
        List<String> rights = conf.getStringList("rights");
        return ok(userOverview.render("User Overview", User.getAllUsersAsArray(), formFactory.form(User.class), rights));
    }

    public Result update(){
        String[] postAction = request().body().asMultipartFormData().asFormUrlEncoded().get("userOverviewButton");
        if(postAction == null || postAction.length == 0)
            return badRequest("You first need to select a strike");
        else{
            if("update".equals(postAction[0])){
                if(userSelected != null) {
                    Http.MultipartFormData body = request().body().asMultipartFormData();
                    updateUser(body);
                    return redirect(routes.UserOverviewController.index());
                }else
                    return badRequest("You first need to select a user");
            }else if("logout".equals(postAction[0])) {
                securityController.logout();
            }
            else if("admin".equals(postAction[0])){ // return to default page
                return redirect(routes.AdminController.index());
            }
            userSelected = null;
        }
        return redirect(routes.UserOverviewController.index());
    }

    public void updateUser(Http.MultipartFormData body){
        User user  = formFactory.form(User.class).bindFromRequest().get();

        Ebean.beginTransaction();
        try{
            Integer id = userSelected.getId();
            userSelected = user;
            userSelected.setId(id);
            userSelected.update();
            Ebean.commitTransaction();
        }catch(Exception e){
            Logger.error("Exception updating a user" + e);
        }finally {
            Ebean.endTransaction();
        }
    }

    public Result getSelectedUser(String userId){
        User user = User.find.byId(Integer.parseInt(userId));
        userSelected = user;
        return ok(toJson(user));
    }
}
