package controllers;

import akka.dispatch.Foreach;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import models.Sector;
import models.Strike;
import play.data.FormFactory;
import play.libs.Yaml;
import play.mvc.*;
import scala.Array;
import views.html.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if(Sector.find.findRowCount() == 0) {
            try {
                List<Sector> load = (List<Sector>) Yaml.load("sector-data.yml");
                for (int i = 0; i < load.size(); i++) {
                    Ebean.save(load.get(i));
                }
                System.out.println("YAML loaded!");
            }
            catch(Exception e){
                System.out.println("YAML didnt load!");
                System.out.println(e.getMessage());
            }
        }
        System.out.println(toJson(new Model.Finder(Sector.class).all()));

        List<Sector> sectors = Sector.find.all();
//        Map<String, String> sectors = new HashMap<>();
//        for(int i = 0; i < list.size(); i++) {
//            sectors.put((String.valueOf( list.get(i).getId())), list.get(i).sectorName);}

        return ok(index.render("", formFactory.form(Strike.class), sectors));
    }

    public Result addStrike()
    {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("articleUpload");
        if(file.equals(null))
            System.out.println("LEEG");
        else
            System.out.println(file.getFilename());

        Ebean.beginTransaction();
        try {
            Strike strike = formFactory.form(Strike.class).bindFromRequest().get();
            Map<?, Sector> map = Sector.find.findMap();

            String[] ids = (String[]) body.asFormUrlEncoded().get("sectors.id[]");
            List<Sector> sectorsFromForm = Stream.of(ids)
                    .map(id -> map.getOrDefault(new Long(id), null))
                    .collect(Collectors.toList());
            strike.setSectors(sectorsFromForm);

            System.out.println(toJson(strike));
            System.out.println(toJson(strike.getSectors()));
            strike.save();
            Ebean.commitTransaction();
//            System.out.println(toJson(strike));
//            System.out.println(toJson(strike.getSectors()));
        }
        finally {
            Ebean.endTransaction();
        }
        try {
            System.out.println(toJson(new Model.Finder(Strike.class).all()));
        }
        catch(Exception e){
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
        }

        return redirect(routes.HomeController.index());
    }

    public Result getStrikes(){
//        List<Strike> strikes = new Model.Finder(Strike.class).all();
        return ok(toJson(Strike.getAllStrikes()));
    }
}
