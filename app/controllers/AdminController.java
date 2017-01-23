package controllers;

import com.avaje.ebean.Ebean;
import com.typesafe.config.ConfigFactory;
import models.*;
import play.data.FormFactory;
import play.libs.Yaml;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import security.Authorized;
import security.Secured;
import views.html.admin;
import views.html.update;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static play.libs.Json.toJson;

/**
 * Created by Igor on 11/4/2016.
 */
@Security.Authenticated(Secured.class)
@Authorized.AdminAuthorized
public class AdminController extends Controller{

    @Inject FormFactory formFactory;
    @Inject SecurityController securityController;
    private Strike strikeSelected = null;

    public Result index()
    {
        return ok(admin.render("", Strike.getAllStrikesAsArray(), formFactory.form(Strike.class)));
    }

    public Result update()
    {
        String[] postAction = request().body().asMultipartFormData().asFormUrlEncoded().get("updateStrikeButton");
        if(postAction == null || postAction.length == 0)
            return badRequest("You first need to select a strike");
        else{
            if("approve".equals(postAction[0])){
                strikeSelected.setChecked(true);
                strikeSelected.update();
                return redirect(routes.AdminController.index());
            }else if("update".equals(postAction[0])){
                if(strikeSelected != null)
                    return redirect("/admin/update/strike/" + strikeSelected.id);
                else
                    return badRequest("You first need to select a strike");
            }else if("discard".equals(postAction[0])){
                strikeSelected.delete();
                return redirect(routes.AdminController.index());
            }else if("logout".equals(postAction[0])) {
                securityController.logout();
            }else if("refresh".equals(postAction[0])){
                index();
            }
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
                System.out.println(postAction[0]);
                Http.MultipartFormData body = request().body().asMultipartFormData();
                updateStrike(body);
//                return edit(request());
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

        if(article.getFilename().length() > 0) {
            String[] temp = article.getFilename().split("\\.");
            String extension = temp[temp.length - 1].toString();

            // creating the file with the correct path
            File articleFile = null;
            try {
                articleFile = File.createTempFile("article-", "." + extension, new File(ConfigFactory.load().getString("articleFilePath")));
            } catch (Exception e) {
            }

            // Checks if the file has an image extension
            if (Pattern.compile("((?i)(jpg|png|gif|bmp)$)").matcher(extension).matches()) {
                BufferedImage image = null;
                try {
                    image = ImageIO.read(article.getFile());
                    ImageIO.write(image, extension, articleFile);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
            // Checks if the file has a pdf or tif extension
            else if (Pattern.compile("((?i)(pdf|tiff|tif)$)").matcher(extension).matches()) {
                try {
                    FileInputStream fs = new FileInputStream(article.getFile());
                    int b;
                    FileOutputStream os = new FileOutputStream(articleFile);
                    while ((b = fs.read()) != -1) {
                        os.write(b);
                    }
                    os.close();
                    fs.close();
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
            strike.setArticle(new Article(articleFile.getName()));
        }

        Ebean.beginTransaction();
        try {


            // --------------------------------------------------------------------------------- \\
            // Maps the sectors given by the form and puts them in the sectors list of the Strike
            Map<?, Sector> sectorMap = Sector.find.findMap();
            strike.setSectors(mapSelectedOptionsToTheStrike(body, strike, "sectors[]", sectorMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the occupations given by the form and puts them in the occupations list of the Strike
            Map<?, OccupationHisco> occupationHiscoMap = OccupationHisco.find.findMap();
            strike.setHiscoOccupations(mapSelectedOptionsToTheStrike(body, strike, "hiscoOccupations[]", occupationHiscoMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the causeOfDisputes given by the form and puts them in the causeOfDisputes list of the Strike
            Map<?, CauseOfDispute> causeOfDisputeMap = CauseOfDispute.find.findMap();
            strike.setCauseOfDisputes(mapSelectedOptionsToTheStrike(body, strike, "causeOfDisputes[]", causeOfDisputeMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the identityElements given by the form and puts them in the identityElements list of the Strike
            Map<?, IdentityElement> identityElementMap = IdentityElement.find.findMap();
            strike.setIdentityElements(mapSelectedOptionsToTheStrike(body, strike, "identityElements[]", identityElementMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the strikeDefinitions given by the form and puts them in the strikeDefinitions list of the Strike
            Map<?, StrikeDefinition> strikeDefinitionMap = StrikeDefinition.find.findMap();
            strike.setStrikeDefinitions(mapSelectedOptionsToTheStrike(body, strike, "strikeDefinitions[]", strikeDefinitionMap));

            // Saves the strike

            Long id = strikeSelected.id;
            strikeSelected = strike;
            strikeSelected.id = id;
            strikeSelected.update();
            Ebean.commitTransaction();
        }
        catch (Exception e){
            System.out.println("Iets in strike is leeg!");
            System.out.println(e.getMessage());
        }
        finally {
            Ebean.endTransaction();
        }
    }

    private <T> List<T> mapSelectedOptionsToTheStrike(Http.MultipartFormData body, Strike strike, String name,  Map<?, T> input)
    {
        try{
            String[] ids = (String[]) body.asFormUrlEncoded().get(name);
            return Stream.of(ids)
                    .map(id -> input.getOrDefault(new Long(id), null))
                    .collect(Collectors.toList());
        }
        catch (Exception e){
            // Create default sector to be given when no sector selected
            String[] ids = {"1"};
            return Stream.of(ids)
                    .map(id -> input.getOrDefault(new Long(id), null))
                    .collect(Collectors.toList());
        }
    }

    public Result strikeToUpdate(Long id){
        List<Integer> years = IntStream.rangeClosed(1700, 1950).boxed().collect(Collectors.toList());
        List<Integer> days = IntStream.rangeClosed(0, 31).boxed().collect(Collectors.toList());
        List<Integer> duration = IntStream.rangeClosed(0, 500).boxed().collect(Collectors.toList());
        return ok(update.render("",
                    formFactory.form(Strike.class).fill(strikeSelected),
                    Sector.find.all(),
                    (List<String>) Yaml.load("source-data.yml"),
                    OccupationHisco.find.all(),
                    CauseOfDispute.find.all(),
                    IdentityElement.find.all(),
                    StrikeDefinition.find.all(),
                    (List<String>) Yaml.load("country-data.yml"),
                    (List<String>) Yaml.load("labour-relations.yml"),
                    (List<String>) Yaml.load("months.yml"),
                    (List<String>) Yaml.load("number-of-participants.yml"),
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

    public Result getSelectedStrike(String selectedStrike)
    {
        strikeSelected = Strike.find.byId(Integer.parseInt(selectedStrike));
        return ok(toJson(strikeSelected));
    }
}
