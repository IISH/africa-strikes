package controllers;

import com.avaje.ebean.Ebean;
import com.typesafe.config.ConfigFactory;
import models.*;
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
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result index() {
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
                if (article.getFilename().length() > 0) {
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
                strike.setChecked(false);
                strike.setAuthorInformation(ctx().session().get("username"));

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

    public Result getStrikes(){
        return ok(toJson(Strike.getAllStrikes()));
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
}
