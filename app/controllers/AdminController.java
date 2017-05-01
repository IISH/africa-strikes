package controllers;

import com.avaje.ebean.Ebean;
import com.typesafe.config.ConfigFactory;
import models.*;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import security.Authorized;
import security.Secured;
import views.html.*;
import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static play.libs.Json.toJson;

/**
 * Created by Igor on 11/4/2016.
 */
@Security.Authenticated(Secured.class)
//@Authorized.With(Authorized.With.Authority.ADMIN)
public class AdminController extends Controller{

    @Inject FormFactory formFactory;
    @Inject SecurityController securityController;
    @Inject StrikeController strikeController;
    private Strike strikeSelected = null;
    private final Logger.ALogger logger = Logger.of(this.getClass());

    /**
     * Returns the admin page of Africa Strikes with settings depending on the user.
     * @return admin page of Africa Strikes
     */
    public Result index()
    {
        if(securityController.isAdmin()) {
            strikeSelected = null;
            return ok(admin.render("", Strike.getAllStrikesIds(), formFactory.form(Strike.class), securityController.isAdmin()));
        }else if(securityController.isSubscriber()){
            strikeSelected = null;
            return ok(admin.render("", Strike.getStrikeIdsByUser(ctx().session().get("username")), formFactory.form(Strike.class), securityController.isAdmin()));
        }
        else
            return badRequest(noStrikeSelected.render("You don't have the correct rights for this page","/logout"));
    }

    /**
     * Handles a bad request
     * @param previousUrl
     * @return a redirect to the previous page
     */
    public Result handleBadRequest(String previousUrl){
        return redirect(previousUrl);
    }

    /**
     * Handles the actions of the user. E.g. update, logout etc.
     * @return a Result depending on the action
     */
    public Result update()
    {
        String[] postAction = request().body().asMultipartFormData().asFormUrlEncoded().get("updateStrikeButton");
        if(postAction == null || postAction.length == 0)
            return badRequest(noStrikeSelected.render("You first need to select a strike",request().getHeader("referer")));
        else{
            if(securityController.isSubscriber()){
                if("update".equals(postAction[0])){
                    if(strikeSelected != null && strikeSelected.getAuthorInformation().equals(ctx().session().get("username")))
                        return redirect("/admin/update/strike/" + strikeSelected.id);
                    else
                        return badRequest(noStrikeSelected.render("You first need to select a strike",request().getHeader("referer")));
                }else if("logout".equals(postAction[0])) {
                    securityController.logout();
                }else if("index".equals(postAction[0])){ // return to default page
                    return redirect(routes.HomeController.index());
                }
                strikeSelected = null;
            }
            else if(securityController.isAdmin()){
                if("approve".equals(postAction[0])){
                    if(strikeSelected != null) {
                        strikeSelected.setChecked(true);
                        strikeSelected.update();
                        return redirect(routes.AdminController.index());
                    }else
                        return badRequest(noStrikeSelected.render("You first need to select a strike", request().getHeader("referer")));
                }else if("update".equals(postAction[0])){
                    if(strikeSelected != null)
                        return redirect("/admin/update/strike/" + strikeSelected.id);
                    else
                        return badRequest(noStrikeSelected.render("You first need to select a strike",request().getHeader("referer")));
                }else if("discard".equals(postAction[0])){
                    if(strikeSelected != null) {
                        strikeSelected.delete();
                        return redirect(routes.AdminController.index());
                    }else
                        return badRequest(noStrikeSelected.render("You first need to select a strike",request().getHeader("referer")));
                }else if("logout".equals(postAction[0])) {
                    securityController.logout();
                }else if("index".equals(postAction[0])){ // return to default page
                    return redirect(routes.HomeController.index());
                }else if("userOverview".equals(postAction[0])){ // return to default page
                    return redirect(routes.UserOverviewController.index());
                }
                strikeSelected = null;
            }else{
                strikeSelected = null;
            }
        }
        return redirect(routes.AdminController.index());
    }

    /**
     * Handles the request for strikes by specific parameters
     * @param checked whether the strike has been approved or not
     * @return a Json object with the strikes requested
     */
    public Result getCheckedStrikes(String checked)
    {
        List<Long> checkedOrUncheckedStrikes = new ArrayList<>();
        if(securityController.isAdmin()) {
            if (checked == null) { // returns all the strikes as default.
                checkedOrUncheckedStrikes.addAll(Strike.getAllStrikes().stream().map(strike -> strike.id).collect(Collectors.toList()));
            } else {
                if ("checked".equals(checked)) {
                    checkedOrUncheckedStrikes.addAll(Strike.getAllStrikes().stream().filter(s -> s.getChecked()).map(strike -> strike.id).collect(Collectors.toList()));
                } else if ("unchecked".equals(checked)) {
                    checkedOrUncheckedStrikes.addAll(Strike.getAllStrikes().stream().filter(s -> !s.getChecked()).map(strike -> strike.id).collect(Collectors.toList()));
                } else if ("all".equals(checked)) {
                    checkedOrUncheckedStrikes.addAll(Strike.getAllStrikes().stream().map(strike -> strike.id).collect(Collectors.toList()));
                }
            }
        }else {
            if (checked == null) { // returns all the strikes as default.
                checkedOrUncheckedStrikes.addAll(Strike.getAllStrikesByUser(ctx().session().get("username")).stream().map(strike -> strike.id).collect(Collectors.toList()));
            } else {
                if ("checked".equals(checked)) {
                    checkedOrUncheckedStrikes.addAll(Strike.getAllStrikesByUser(ctx().session().get("username")).stream().filter(s -> s.getChecked()).map(strike -> strike.id).collect(Collectors.toList()));
                } else if ("unchecked".equals(checked)) {
                    checkedOrUncheckedStrikes.addAll(Strike.getAllStrikesByUser(ctx().session().get("username")).stream().filter(s -> !s.getChecked()).map(strike -> strike.id).collect(Collectors.toList()));
                } else if ("all".equals(checked)) {
                    checkedOrUncheckedStrikes.addAll(Strike.getAllStrikesByUser(ctx().session().get("username")).stream().map(strike -> strike.id).collect(Collectors.toList()));
                }
            }
        }
        response().setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
        return ok(toJson(checkedOrUncheckedStrikes));
    }

    /**
     * Saves the updates made to the strike by using the method updateStrike()
     * @return a redirect to the admin page
     */
    public Result saveUpdatedStrike()
    {
        String[] postAction = request().body().asMultipartFormData().asFormUrlEncoded().get("submitUpdateButton");
        if (postAction == null || postAction.length == 0) {
            return badRequest("You must provide a valid action");
        } else {
            if ("save".equals(postAction[0])) {
                if(!strikeSelected.getChecked()) { // check if the strike has already been approved
                    Http.MultipartFormData body = request().body().asMultipartFormData();
                    if (body.asFormUrlEncoded().get("authorInformation").equals(ctx().session().get("username"))) { // check if author provided and user are the same
                        updateStrike(body);
                    }else {return badRequest(noStrikeSelected.render("You cannot update the author information of the strike!","/admin"));}
                }else {return badRequest(noStrikeSelected.render("You cannot update the selected strike!","/admin"));}
            } else if ("undo".equals(postAction[0])) {
                return redirect(routes.AdminController.index());
            }
        }
        return redirect(routes.AdminController.index());
    }

    /**
     * The updating of the strike to the database
     * @param body the information to be saved in the strike
     */
    private void updateStrike(Http.MultipartFormData body)
    {
        // Code to save the given article image
        Http.MultipartFormData.FilePart<File> article = body.getFile("articleUpload");
        Strike strike = formFactory.form(Strike.class).bindFromRequest().get();

        strikeController.handleStrikeArticle(article, strike);

        Ebean.beginTransaction();
        try {
            strikeController.handleStrikeMapping(strike, body);

            // Saves the updated strike
            Long id = strikeSelected.id;
            strikeSelected = strike;
            strikeSelected.id = id;
            strikeSelected.update();
            Ebean.commitTransaction();
        }
        catch (Exception e){
            logger.error("Exception updating a strike " + e);
        }
        finally {
            Ebean.endTransaction();
        }
    }

    /**
     * Gets the strike needed to update via the id of the strike
     * @param id the id of the strike to update
     * @return the update page with the correct information of the strike
     */
    public Result strikeToUpdate(Long id){
        strikeSelected = Strike.find.byId(id);
        if(!strikeSelected.getAuthorInformation().equals(ctx().session().get("username"))){
            return badRequest(noStrikeSelected.render("You don't have access to the selected strike!","/admin"));
        }else if(strikeSelected.getChecked()){
            return badRequest(noStrikeSelected.render("You cannot update the selected strike as it has already been approved!","/admin"));
        }
        else {
            strikeController.checkFirstLoad();
            List<Integer> years = IntStream.rangeClosed(1700, 1950).boxed().collect(Collectors.toList());
            List<Integer> days = IntStream.rangeClosed(0, 31).boxed().collect(Collectors.toList());
            List<Integer> duration = IntStream.rangeClosed(0, 500).boxed().collect(Collectors.toList());
            return ok(update.render("",
                    formFactory.form(Strike.class).fill(strikeSelected),
                    Sector.find.all(),
                    strikeController.getSourceData(),
                    OccupationHisco.find.all(),
                    CauseOfDispute.find.all(),
                    IdentityElement.find.all(),
                    StrikeDefinition.find.all(),
                    strikeController.getCountryData(),
                    strikeController.getLabourRelations(),
                    strikeController.getMonths(),
                    strikeController.getNumberOfParticipants(),
                    years,
                    days,
                    duration,
                    id,
                    strikeSelected,
                    strikeSelected.getChecked(),
                    securityController.isSubscriber()
            ));
        }
    }

    /**
     * Gets the article added with the strike
     * @param selectedStrike the strike that has been selected in string format
     * @return the article in File format
     */
    public Result getArticleFile(String selectedStrike) {
        strikeSelected = Strike.find.byId(Long.parseLong(selectedStrike));
        if(!strikeSelected.getAuthorInformation().equals(ctx().session().get("username"))){
            return badRequest(noStrikeSelected.render("You don't have access to the selected article!","/admin"));
        }else {
            return ok(new File(ConfigFactory.load().getString("articleFilePath") + strikeSelected.getArticle().articleName));
        }
    }

    /**
     * Gets the selected strike
     * @param selectedStrike the strike selected in string format
     * @return the strike in Json format
     */
    public Result getSelectedStrike(String selectedStrike) {
        response().setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
        strikeSelected = Strike.find.byId(Long.parseLong(selectedStrike));
        if(securityController.isSubscriber()) {
            if (strikeSelected.getAuthorInformation().equals(ctx().session().get("username")))
                return ok(toJson(strikeSelected));
            else
                return  ok(toJson("Access denied"));
        }else{
            return ok(toJson(strikeSelected));
        }
    }

    /**
     * gets all id of all strikes in the database, depending on the user logged in
     * @return a Json file with the ids of the strikes
     */
    public Result getAllStrikeIds(){
        response().setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
        if(securityController.isAdmin())
            return ok(toJson(Strike.getAllStrikesIds()));
        else
            return ok(toJson(Strike.getStrikeIdsByUser(ctx().session().get("username"))));

    }
}
