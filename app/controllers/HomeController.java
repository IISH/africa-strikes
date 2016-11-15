package controllers;

import com.avaje.ebean.Ebean;
import models.Occupation;
import models.Sector;
import models.Strike;
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
                System.out.println("Sectors YAML loaded!");
            }
            catch(Exception e){
                System.out.println("Sectors YAML didnt load!");
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
                System.out.println("Sectors YAML loaded!");
            }
            catch (Exception e)
            {
                System.out.println("Occupations YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }

        List<String> sources = (List<String>) Yaml.load("source-data.yml");

        return ok(index.render("", formFactory.form(Strike.class), Sector.find.all(), sources, Occupation.find.all()));
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
            // Gets the data from the form
            Strike strike = formFactory.form(Strike.class).bindFromRequest().get();
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


            // Maps the occupations given by the form and puts them in te occupations list of the Strike
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
