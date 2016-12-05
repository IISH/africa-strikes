package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.FormFactory;
import play.libs.Yaml;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.admin;
import views.html.update;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class AdminController extends Controller{

    @Inject FormFactory formFactory;
    private Strike strikeSelected = null;

    public Result index()
    {
        return ok(admin.render("", Strike.getAllStrikesAsArray(), formFactory.form(Strike.class)));
    }

    public Result update()
    {
        if(strikeSelected != null)
            return redirect("/admin/update/strike/" + strikeSelected.id);
        else
            return  redirect(routes.AdminController.index());
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
                System.out.println(postAction[0]);
//                return remove(request());
            } else {
//                return badRequest("This action is not allowed");
            }
        }
        return redirect(routes.AdminController.index());
    }

    private void updateStrike(Http.MultipartFormData body)
    {
        // Code to save the given article image
        Http.MultipartFormData.FilePart<File> article = body.getFile("articleUpload");
        String[] temp = article.getFilename().split("\\.");
        String extension = temp[temp.length - 1].toString();

        // creating the file with the correct path
        File articleFile = null;
        try{
            articleFile = File.createTempFile("article-", "."+extension, new File("C:\\AfricaStrikes\\Articles\\"));
        }
        catch(Exception e){
        }

        // Checks if the file has an image extension
        if (Pattern.compile("((?i)(jpg|png|gif|bmp)$)").matcher(extension).matches()){
            BufferedImage image = null;
            try {
                image = ImageIO.read(article.getFile());
                ImageIO.write(image, extension, articleFile);
            } catch (Exception e) {
                e.getMessage();
            }
        }
        // Checks if the file has a pdf or tif extension
        else if(Pattern.compile("((?i)(pdf|tiff|tif)$)").matcher(extension).matches()){
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

        Ebean.beginTransaction();
        try {
            Strike strike = formFactory.form(Strike.class).bindFromRequest().get();
            strike.setArticle(new Article(articleFile.getName()));

            // --------------------------------------------------------------------------------- \\
            // Maps the sectors given by the form and puts them in the sectors list of the Strike
            Map<?, Sector> sectorMap = Sector.find.findMap();
            strike.setSectors(mapSelectedOptionsToTheStrike(body, strike, "sectors.id[]", sectorMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the occupations given by the form and puts them in the occupations list of the Strike
            Map<?, OccupationHisco> occupationHiscoMap = OccupationHisco.find.findMap();
            strike.setHiscoOccupations(mapSelectedOptionsToTheStrike(body, strike, "hiscoOccupations.id[]", occupationHiscoMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the causeOfDisputes given by the form and puts them in the causeOfDisputes list of the Strike
            Map<?, CauseOfDispute> causeOfDisputeMap = CauseOfDispute.find.findMap();
            strike.setCauseOfDisputes(mapSelectedOptionsToTheStrike(body, strike, "causeOfDisputes.id[]", causeOfDisputeMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the identityElements given by the form and puts them in the identityElements list of the Strike
            Map<?, IdentityElement> identityElementMap = IdentityElement.find.findMap();
            strike.setIdentityElements(mapSelectedOptionsToTheStrike(body, strike, "identityElements.id[]", identityElementMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the strikeDefinitions given by the form and puts them in the strikeDefinitions list of the Strike
            Map<?, StrikeDefinition> strikeDefinitionMap = StrikeDefinition.find.findMap();
            strike.setStrikeDefinitions(mapSelectedOptionsToTheStrike(body, strike, "strikeDefinitions.id[]", strikeDefinitionMap));

            // Saves the strike

            Long id = strikeSelected.id;
            strikeSelected = strike;
            strikeSelected.id = id;
//            Ebean.update(strikeSelected);
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
                    id));
    }

    public Result getArticleFile(String selectedStrike) {
        strikeSelected = Strike.find.byId(Integer.parseInt(selectedStrike));
        return ok(new File("C:/AfricaStrikes/Articles/" + strikeSelected.getArticle().articleName));
    }

    public Result getSelectedStrike(String selectedStrike)
    {
        strikeSelected = Strike.find.byId(Integer.parseInt(selectedStrike));
        return ok(toJson(strikeSelected));
    }
}
