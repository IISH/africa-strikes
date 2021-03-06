package controllers;

import com.avaje.ebean.Ebean;
import models.Authority;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import security.Authorized;
import security.Secured;
import views.html.*;
import play.data.FormFactory;
import javax.inject.Inject;

import static play.libs.Json.toJson;

/**
 * Created by Igor on 2/17/2017.
 */
@Security.Authenticated(Secured.class)
@Authorized.With(Authorized.With.Authority.ADMIN)
public class UserOverviewController extends Controller{

    @Inject FormFactory formFactory;
    @Inject SecurityController securityController;
    private User userSelected = null;

    public Result index(){
        resetUser();
        return ok(userOverview.render(
                "User Overview",
                User.getAllUsersAsArray(),
                formFactory.form(User.class),
                Authority.find.all()));
    }

    public void resetUser(){
        userSelected = null;
    }

    public Result update(){
        String[] postAction = request().body().asMultipartFormData().asFormUrlEncoded().get("userOverviewButton");
        if(postAction == null || postAction.length == 0)
            return badRequest(noStrikeSelected.render("You first need to select a user", request().getHeader("referer")));
        else{
            if("update".equals(postAction[0])){
                if(userSelected != null) {
                    Http.MultipartFormData body = request().body().asMultipartFormData();
                    updateUser(body);
                    return redirect(routes.UserOverviewController.index());
                }else
                    return badRequest(noStrikeSelected.render("You first need to select a user", request().getHeader("referer")));
            }else if("remove".equals(postAction[0])){
                if(userSelected != null){
                    userSelected.delete();
                    return redirect(routes.UserOverviewController.index());
                }else
                    return badRequest(noStrikeSelected.render("You first need to select a user", request().getHeader("referer")));
            }else if("logout".equals(postAction[0])) {
                securityController.logout();
            }
            else if("admin".equals(postAction[0])){ // return to default page
                return redirect(routes.AdminController.index());
            }
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

    public Result getAllUsers(){
        response().setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
        resetUser();
        return ok(toJson(User.getAllUsers()));
    }

    public Result getSelectedUser(String userId){
        response().setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
        User user = User.find.byId(Integer.parseInt(userId));
        if(user.getAuthority() == null){
            user.setAuthority("Unauthorized");
            user.save();
        }
        userSelected = user;
        return ok(toJson(user));
    }
}
