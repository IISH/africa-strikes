package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.FormFactory;
import play.libs.Yaml;
import play.mvc.*;
import play.twirl.api.Html;
import views.html.*;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static play.libs.Json.toJson;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject FormFactory formFactory;
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

        return ok(index.render("",
                formFactory.form(Strike.class),
                Sector.find.all(),
                (List<String>) Yaml.load("source-data.yml"),
                OccupationHisco.find.all(),
                CauseOfDispute.find.all(),
                IdentityElement.find.all(),
                StrikeDefinition.find.all(),
                (List<String>) Yaml.load("country-data.yml"),
                (List<String>) Yaml.load("labour-relations.yml"),
                (List<String>) Yaml.load("months.yml"),
                (List<String>) Yaml.load("number-of-participants.yml")));
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

    //// Method to convert the input text to a List to save in the strike
//    private <T> List<T> convertSelectizeInputToList(Http.MultipartFormData body, String name)
//    {
//        String[] elements = (String[]) body.asFormUrlEncoded().get(name);
//        List<String> elementList = Arrays.asList(elements[0].split(","));
//        List<CompanyName> elementsToSave = new ArrayList<>();
//        for (String s : elementList) {
//            elementsToSave.add(new CompanyName(s));
//        }
//        return elementsToSave;
//    }

    private Html handleBadForm(String message)
    {
        return (index.render(message,
                formFactory.form(Strike.class),
                Sector.find.all(),
                (List<String>) Yaml.load("source-data.yml"),
                OccupationHisco.find.all(),
                CauseOfDispute.find.all(),
                IdentityElement.find.all(),
                StrikeDefinition.find.all(),
                (List<String>) Yaml.load("country-data.yml"),
                (List<String>) Yaml.load("labour-relations.yml"),
                (List<String>) Yaml.load("months.yml"),
                (List<String>) Yaml.load("number-of-participants.yml")));
    }

    public Result addStrike()
    {
        Http.MultipartFormData body = request().body().asMultipartFormData();

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
//            if(body.asFormUrlEncoded().get("source") == null)
//                System.out.println("Source is null");
//            else if(body.asFormUrlEncoded().get("source") == "")
//                System.out.println("Source is empty");
//            else
//                System.out.println(toJson(body.asFormUrlEncoded().get("source")));

//            if(body.asFormUrlEncoded().get("source") == "") {
//                System.out.println("Source is: "+toJson(body.asFormUrlEncoded().get("source")));
//                return badRequest(handleBadForm("SourceStrike"));
//            }
//            else if(body.asFormUrlEncoded().get("country") == ""){
//                return badRequest(handleBadForm("Country"));
//            }
//            else if(body.asFormUrlEncoded().get("yearStrikeStarted") == ""){
//                return badRequest(handleBadForm("YearStrikeStarted"));
//            }
//            else if(body.asFormUrlEncoded().get("occupations") == ""){
//                return badRequest(handleBadForm("Occupation"));
//            }
//            else if(body.asFormUrlEncoded().get("workersSituation") == ""){
//                return badRequest(handleBadForm("WorkersSituation"));
//            }
//            else if(body.asFormUrlEncoded().get("dominantGender") == ""){
//                return badRequest(handleBadForm("DominantGender"));
//            }
//            else if(body.asFormUrlEncoded().get("authorInformation") == ""){
//                return badRequest(handleBadForm("Author"));
//            }



            // Gets the companyNames from the form and adds them to a list to save in the collected strike form.
            String[] companies = (String[]) body.asFormUrlEncoded().get("companyNames[]");
            List<String> companyNames = Arrays.asList(companies[0].split(","));
            List<CompanyName> companyNamesToSave = new ArrayList<>();
            for (String s : companyNames) {
                companyNamesToSave.add(new CompanyName(s));
            }

            // Gets the occupations from the form and adds them to a list to save in the collected strike form.
            String[] occupations = (String[]) body.asFormUrlEncoded().get("occupations[]");
            List<String> occupationNames = Arrays.asList(occupations[0].split(","));
            List<Occupation> occupationsToSave = new ArrayList<>();
            for(String s : occupationNames){
                occupationsToSave.add(new Occupation(s));
            }

            String[] identityDetails = (String[]) body.asFormUrlEncoded().get("identityDetails[]");
            List<String> identityDetailNames = Arrays.asList(identityDetails[0].split(","));
            List<IdentityDetail> identityDetailsToSave = new ArrayList<>();
            for(String s : identityDetailNames){
                identityDetailsToSave.add(new IdentityDetail(s));
            }

            // Collects the strike form and sets the company names
            Strike strike = formFactory.form(Strike.class).bindFromRequest().get();
            strike.setCompanyNames(companyNamesToSave);
            strike.setOccupations(occupationsToSave);
            strike.setIdentityDetails(identityDetailsToSave);
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
            strike.save();
            Ebean.commitTransaction();
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
