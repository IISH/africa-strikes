package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.FormFactory;
import play.libs.Yaml;
import play.mvc.*;
import security.Authorized;
import security.Secured;
import views.html.*;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static play.libs.Json.toJson;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Security.Authenticated(Secured.class)
@Authorized.AdminAuthorized
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

        if(Occupation.find.findRowCount() == 0) {
            saveYamlFileToDatabase((List<Occupation>) Yaml.load("occupation-data.yml"));
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

        List<String> sources = (List<String>) Yaml.load("source-data.yml");
        List<String> countries = (List<String>) Yaml.load("country-data.yml");

        return ok(index.render("", formFactory.form(Strike.class),
                Sector.find.all(), sources, Occupation.find.all(),
                CauseOfDispute.find.all(), IdentityElement.find.all(),
                StrikeDefinition.find.all(), countries));
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
        Http.MultipartFormData body = request().body().asMultipartFormData();
        // Gets the uploaded file from the form and checks if it is empty
        Http.MultipartFormData.FilePart file = body.getFile("articleUpload");
        if(file.equals(null))
            System.out.println("LEEG");
        else
            System.out.println(file.getFilename());

        Ebean.beginTransaction();
        try {

            // Gets the companyNames from the form and adds them to a list to save in the collected strike form.
            String[] cmops = (String[]) body.asFormUrlEncoded().get("companyNames[]");
            List<String> companyNames = Arrays.asList(cmops[0].split(","));
            List<CompanyName> companyNamesToSave = new ArrayList<>();
            for (String s : companyNames) {
                companyNamesToSave.add(new CompanyName(s));
            }

            // Collects the strike form and sets the company names
            Strike strike = formFactory.form(Strike.class).bindFromRequest().get();
            strike.setCompanyNames(companyNamesToSave);

            // --------------------------------------------------------------------------------- \\
            // Maps the sectors given by the form and puts them in the sectors list of the Strike
            Map<?, Sector> sectorMap = Sector.find.findMap();
            strike.setSectors(mapSelectedOptionsToTheStrike(body, strike, "sectors.id[]", sectorMap));

            // --------------------------------------------------------------------------------- \\
            // Maps the occupations given by the form and puts them in the occupations list of the Strike
            Map<?, Occupation> occupationMap = Occupation.find.findMap();
            strike.setOccupations(mapSelectedOptionsToTheStrike(body, strike, "occupations.id[]", occupationMap));

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
