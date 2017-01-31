package controllers;

import com.avaje.ebean.Ebean;
import com.typesafe.config.ConfigFactory;
import models.*;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.FormFactory;
import play.libs.Yaml;
import play.mvc.*;
import play.twirl.api.Html;
import security.Secured;
import views.html.*;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static play.libs.Json.toJson;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Security.Authenticated(Secured.class)
public class HomeController extends Controller{

    @Inject FormFactory formFactory;
    @Inject SecurityController securityController;
    @Inject StrikeController strikeController;
    private boolean isFirstLoad = true;
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        if(isFirstLoad){
            strikeController.loadYamlFiles();
            isFirstLoad = false;
        }
        // Fills the tables with the correct data if the tables are empty
        if(Sector.find.findRowCount() == 0) {
            saveYamlFileToDatabase((List<Sector>) Yaml.load("sector-data.yml"));
        }

        if(OccupationHisco.find.findRowCount() == 0) {
            saveYamlFileToDatabase((List<OccupationHisco>) Yaml.load("occupation-hisco-data.yml"));
        }

        if(CauseOfDispute.find.findRowCount() == 0) {
            saveYamlFileToDatabase((List<CauseOfDispute>) Yaml.load("cause-of-dispute-data.yml"));
        }

        if(IdentityElement.find.findRowCount() == 0) {
            saveYamlFileToDatabase((List<IdentityElement>) Yaml.load("identity-element-data.yml"));
        }

        if(StrikeDefinition.find.findRowCount() == 0) {
            saveYamlFileToDatabase((List<StrikeDefinition>) Yaml.load("strike-definition-data.yml"));
        }

        if(CompanyName.find.findRowCount() == 0) {
            saveYamlFileToDatabase((List<CompanyName>) Yaml.load("company-name-data.yml"));
        }

        Map<String, String> messages = new HashMap<>();
        return handleRequest(messages, new Strike());
    }

    private Result handleRequest(Map<String, String> errorMessages, Strike strike) {
        List<Integer> years = IntStream.rangeClosed(1700, 1950).boxed().collect(Collectors.toList());
        List<Integer> days = IntStream.rangeClosed(0, 31).boxed().collect(Collectors.toList());
        return ok(index.render(errorMessages,
                formFactory.form(Strike.class).fill(strike),
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
                strike));
    }

    private <T> void saveYamlFileToDatabase(List<T> yamlList)
    {
        try {
            for (int i = 0; i < yamlList.size(); i++) {
                Ebean.save(yamlList.get(i));
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Result addStrike()
    {
        Ebean.beginTransaction();
        try {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            String[] postAction = request().body().asMultipartFormData().asFormUrlEncoded().get("indexButton");
            if("logout".equals(postAction[0])){
                securityController.logout();
            }else if("add".equals(postAction[0])) {
                Strike strike = formFactory.form(Strike.class).bindFromRequest().get();

                // Code to save the given article image
                Http.MultipartFormData.FilePart<File> article = body.getFile("articleUpload");
                strikeController.handleStrikeArticle(article, strike);

                strike.setChecked(false);
                strike.setAuthorInformation(ctx().session().get("username"));

                strikeController.handleStrikeMapping(strike, body);

                if (checkIfValid(strike).size() != 0) {
                    return handleRequest(checkIfValid(strike), strike);
                }

                // Saves the strike
                strike.save();
                Ebean.commitTransaction();
            }
        }
        catch (NullPointerException e)
        {
            System.out.println("Iets in strike is leeg!");
            System.out.println(e.getMessage());
        }
        finally {
            Ebean.endTransaction();
        }
        return redirect(routes.HomeController.index());
    }

    private Map<String, String> checkIfValid(Strike strike)
    {
        Map<String, String> result = new HashMap<>();
        if (strike.getYearStart() == 0) {
            result.put("yearStart", "You need to select the year the strike started!!");
        }
        if (strike.getSource().isEmpty()) {
            result.put("source", "You need to fill in a source!!");
        }
        if (strike.getCountry().isEmpty()) {
            result.put("country", "You need to select a country!!");
        }
        if (strike.getWorkersSituation() == null) {
            result.put("workersSituation", "You need to select what the workers on strike are!!");
        }
        if (strike.getDominantGender() == null) {
            result.put("dominantGender", "You need to select a predominant gender!!");
        }
        if (strike.getOccupations().isEmpty()) {
            result.put("occupations", "You need to fill in at least one occupation!!");
        }
        return result;
    }

//    public Result getStrikes(){
//        return ok(toJson(Strike.getAllStrikes()));
//    }
}
