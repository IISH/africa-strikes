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
@Authorized.With(Authorized.With.Authority.ADMIN)
public class AdminController extends Controller{

    @Inject FormFactory formFactory;
    @Inject SecurityController securityController;
    @Inject StrikeController strikeController;
    private Strike strikeSelected = null;
    private final Logger.ALogger logger = Logger.of(this.getClass());

    public Result index()
    {
        strikeSelected = null;
        return ok(admin.render("", Strike.getAllStrikesIds(), formFactory.form(Strike.class)));
    }

    public Result update()
    {
        String[] postAction = request().body().asMultipartFormData().asFormUrlEncoded().get("updateStrikeButton");
        if(postAction == null || postAction.length == 0)
            return badRequest("You first need to select a strike");
        else{
            if("approve".equals(postAction[0])){
                if(strikeSelected != null) {
                    strikeSelected.setChecked(true);
                    strikeSelected.update();
                    return redirect(routes.AdminController.index());
                }else
                    return badRequest(noStrikeSelected.render("You first need to select a strike"));
            }else if("update".equals(postAction[0])){
                if(strikeSelected != null)
                    return redirect("/admin/update/strike/" + strikeSelected.id);
                else
                    return badRequest(noStrikeSelected.render("You first need to select a strike"));
            }else if("discard".equals(postAction[0])){
                if(strikeSelected != null) {
                    strikeSelected.delete();
                    return redirect(routes.AdminController.index());
                }else
                    return badRequest(noStrikeSelected.render("You first need to select a strike"));
            }else if("logout".equals(postAction[0])) {
                securityController.logout();
            }else if("index".equals(postAction[0])){ // return to default page
                return redirect(routes.HomeController.index());
            }else if("userOverview".equals(postAction[0])){ // return to default page
                return redirect(routes.UserOverviewController.index());
            }
            strikeSelected = null;
        }
        return redirect(routes.AdminController.index());
    }

    public Result getCheckedStrikes(String checked)
    {
        List<Long> checkedOrUncheckedStrikes = new ArrayList<>();
        if(checked == null){ // returns all the strikes as default.
            checkedOrUncheckedStrikes.addAll(Strike.getAllStrikes().stream().map(strike -> strike.id).collect(Collectors.toList()));
        } else {
            if("checked".equals(checked)){
                checkedOrUncheckedStrikes.addAll(Strike.getAllStrikes().stream().filter(s -> s.getChecked()).map(strike -> strike.id).collect(Collectors.toList()));
            } else if ("unchecked".equals(checked)){
                checkedOrUncheckedStrikes.addAll(Strike.getAllStrikes().stream().filter(s -> !s.getChecked()).map(strike -> strike.id).collect(Collectors.toList()));
            }
            else if("all".equals(checked)){
                checkedOrUncheckedStrikes.addAll(Strike.getAllStrikes().stream().map(strike -> strike.id).collect(Collectors.toList()));
            }
        }

        return ok(toJson(checkedOrUncheckedStrikes));
    }

    public Result saveUpdatedStrike()
    {
        String[] postAction = request().body().asMultipartFormData().asFormUrlEncoded().get("submitUpdateButton");
        if (postAction == null || postAction.length == 0) {
            return badRequest("You must provide a valid action");
        } else {
            if ("save".equals(postAction[0])) {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                updateStrike(body);
            } else if ("undo".equals(postAction[0])) {
                return redirect(routes.AdminController.index());
            }
        }
        return redirect(routes.AdminController.index());
    }

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

    public Result strikeToUpdate(Long id){
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
                    strikeSelected));
    }

    public Result getArticleFile(String selectedStrike) {
        strikeSelected = Strike.find.byId(Integer.parseInt(selectedStrike));
        return ok(new File(ConfigFactory.load().getString("articleFilePath") + strikeSelected.getArticle().articleName));
    }

    public Result getSelectedStrike(String selectedStrike) {
        strikeSelected = Strike.find.byId(Integer.parseInt(selectedStrike));
        return ok(toJson(strikeSelected));
    }

    public Result getAllStrikeIds(){
        return ok(toJson(Strike.getAllStrikesIds()));
    }
}
