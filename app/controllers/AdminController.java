package controllers;

import models.Strike;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by Igor on 11/4/2016.
 */
public class AdminController extends Controller{

    public Result index()
    {
        return ok(Json.toJson(Strike.getAllStrikes()));
    }

}
