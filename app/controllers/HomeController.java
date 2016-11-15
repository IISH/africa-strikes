package controllers;

import akka.dispatch.Foreach;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import models.Sector;
import models.Source;
import models.Strike;
import play.data.FormFactory;
import play.libs.Yaml;
import play.mvc.*;
import scala.Array;
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

        if(Source.find.findRowCount() == 0){
            try {
                List<Source> load = (List<Source>) Yaml.load("source-data.yml");
                for (int i = 0; i < load.size(); i++) {
                    Ebean.save(load.get(i));
                }
                System.out.println("Sources YAML loaded!");
            }
            catch(Exception e){
                System.out.println("Sources YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }

        // Returns the sectors from the table sector
        List<Sector> sectors = Sector.find.all();
        return ok(index.render("", formFactory.form(Strike.class), sectors, Source.find.all()));
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
            String[] ids = (String[]) body.asFormUrlEncoded().get("sectors.id[]");
            List<Sector> sectorsFromForm = Stream.of(ids)
                    .map(id -> map.getOrDefault(new Long(id), null))
                    .collect(Collectors.toList());
            strike.setSectors(sectorsFromForm);
            // Saves the strike
            strike.save();
            Ebean.commitTransaction();
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
