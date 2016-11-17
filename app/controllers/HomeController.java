package controllers;

import com.avaje.ebean.Ebean;
import models.*;
import play.data.FormFactory;
import play.libs.Yaml;
import play.mvc.*;
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
public class HomeController extends Controller {

    @Inject FormFactory formFactory;
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result index() {
        // Fills the table sector with sectors
        if(Sector.find.findRowCount() == 0) {
            try {
                List<Sector> load = (List<Sector>) Yaml.load("sector-data.yml");
                for (int i = 0; i < load.size(); i++) {
                    Ebean.save(load.get(i));
                }
                System.out.println("Sector YAML loaded!");
            }
            catch(Exception e){
                System.out.println("Sector YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }

        if(Occupation.find.findRowCount() == 0)
        {
            try{
                List<Occupation> load = (List<Occupation>) Yaml.load("occupation-data.yml");
                for (int i = 0; i < load.size(); i++) {
                    Ebean.save(load.get(i));
                }
                System.out.println("Occupation YAML loaded!");
            }
            catch (Exception e)
            {
                System.out.println("Occupation YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }

        if(CauseOfDispute.find.findRowCount() == 0)
        {
            try{
                List<CauseOfDispute> load = (List<CauseOfDispute>) Yaml.load("cause-of-dispute-data.yml");
                for (int i = 0; i < load.size(); i++) {
                    Ebean.save(load.get(i));
                }
                System.out.println("CauseOfDispute YAML loaded!");
            }
            catch (Exception e)
            {
                System.out.println("CauseOfDispute YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }

        if(IdentityElement.find.findRowCount() == 0)
        {
            try{
                List<IdentityElement> load = (List<IdentityElement>) Yaml.load("identity-element-data.yml");
                for (int i = 0; i < load.size(); i++) {
                    Ebean.save(load.get(i));
                }
                System.out.println("IdentityElement YAML loaded!");
            }
            catch (Exception e)
            {
                System.out.println("IdentityElement YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }

        if(StrikeDefinition.find.findRowCount() == 0)
        {
            try{
                List<StrikeDefinition> load = (List<StrikeDefinition>) Yaml.load("strike-definition-data.yml");
                for (int i = 0; i < load.size(); i++) {
                    Ebean.save(load.get(i));
                }
                System.out.println("StrikeDefinition YAML loaded!");
            }
            catch (Exception e)
            {
                System.out.println("StrikeDefinition YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }

        if(CompanyName.find.findRowCount() == 0)
        {
            try{
                List<CompanyName> load = (List<CompanyName>) Yaml.load("company-name-data.yml");
                for (int i = 0; i < load.size(); i++) {
                    Ebean.save(load.get(i));
                }
                System.out.println("CompanyName YAML loaded!");
            }
            catch (Exception e)
            {
                System.out.println("CompanyName YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }

        List<String> sources = (List<String>) Yaml.load("source-data.yml");
        List<String> countries = (List<String>) Yaml.load("country-data.yml");
        List<CompanyName> companies = (List<CompanyName>) Yaml.load("company-name-data.yml");

        return ok(index.render("", formFactory.form(Strike.class),
                Sector.find.all(), sources, Occupation.find.all(),
                CauseOfDispute.find.all(), IdentityElement.find.all(),
                StrikeDefinition.find.all(), countries, CompanyName.getAllCompanyNames()));
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
            Map<?, Sector> map = Sector.find.findMap();
            try {
                String[] ids = (String[]) body.asFormUrlEncoded().get("sectors.id[]");
                List<Sector> sectorsFromForm = Stream.of(ids)
                        .map(id -> map.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setSectors(sectorsFromForm);
            }
            catch (NullPointerException e) {
                // Create default sector to be given when no sector selected
                String[] ids = {"1"};
                List<Sector> sectorsFromForm = Stream.of(ids)
                        .map(id -> map.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setSectors(sectorsFromForm);
            }

            // --------------------------------------------------------------------------------- \\
            // Maps the occupations given by the form and puts them in the occupations list of the Strike
            Map<?, Occupation> occupationMap = Occupation.find.findMap();
            try{
                String[] ids = (String[]) body.asFormUrlEncoded().get("occupations.id[]");
                List<Occupation> occupationsFromForm = Stream.of(ids)
                        .map(id -> occupationMap.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setOccupations(occupationsFromForm);
            }
            catch (NullPointerException e){
                // Create default sector to be given when no sector selected
                String[] ids = {"1"};
                List<Occupation> occupationsFromForm = Stream.of(ids)
                        .map(id -> occupationMap.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setOccupations(occupationsFromForm);
            }

            // --------------------------------------------------------------------------------- \\
            // Maps the causeOfDisputes given by the form and puts them in the causeOfDisputes list of the Strike
            Map<?, CauseOfDispute> causeOfDisputeMap = CauseOfDispute.find.findMap();
            try{
                String[] ids = (String[]) body.asFormUrlEncoded().get("causeOfDisputes.id[]");
                List<CauseOfDispute> causeOfDisputesFromForm = Stream.of(ids)
                        .map(id -> causeOfDisputeMap.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setCauseOfDisputes(causeOfDisputesFromForm);
            }
            catch (NullPointerException e){
                // Create default sector to be given when no sector selected
                String[] ids = {"1"};
                List<CauseOfDispute> causeOfDisputesFromForm = Stream.of(ids)
                        .map(id -> causeOfDisputeMap.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setCauseOfDisputes(causeOfDisputesFromForm);
            }

            // --------------------------------------------------------------------------------- \\
            // Maps the identityElements given by the form and puts them in the identityElements list of the Strike
            Map<?, IdentityElement> identityElementMap = IdentityElement.find.findMap();
            try{
                String[] ids = (String[]) body.asFormUrlEncoded().get("identityElements.id[]");
                List<IdentityElement> occupationsFromForm = Stream.of(ids)
                        .map(id -> identityElementMap.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setIdentityElements(occupationsFromForm);
            }
            catch (NullPointerException e){
                // Create default sector to be given when no sector selected
                String[] ids = {"1"};
                List<IdentityElement> occupationsFromForm = Stream.of(ids)
                        .map(id -> identityElementMap.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setIdentityElements(occupationsFromForm);
            }

            // --------------------------------------------------------------------------------- \\
            // Maps the strikeDefinitions given by the form and puts them in the strikeDefinitions list of the Strike
            Map<?, StrikeDefinition> strikeDefinitionMap = StrikeDefinition.find.findMap();
            try{
                String[] ids = (String[]) body.asFormUrlEncoded().get("strikeDefinitions.id[]");
                List<StrikeDefinition> occupationsFromForm = Stream.of(ids)
                        .map(id -> strikeDefinitionMap.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setStrikeDefinitions(occupationsFromForm);
            }
            catch (NullPointerException e){
                // Create default sector to be given when no sector selected
                String[] ids = {"1"};
                List<StrikeDefinition> occupationsFromForm = Stream.of(ids)
                        .map(id -> strikeDefinitionMap.getOrDefault(new Long(id), null))
                        .collect(Collectors.toList());
                strike.setStrikeDefinitions(occupationsFromForm);
            }

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
}
