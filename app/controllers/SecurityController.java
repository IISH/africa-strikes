package controllers;

import com.typesafe.config.ConfigFactory;
import models.Authority;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.libs.Yaml;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

public class SecurityController extends Controller {
    @Inject private FormFactory formFactory;
    @Inject private StrikeController strikeController;

    /**
     * handles the login for the application
     * @return a Result depending on the login
     */
    public Result login() {
        if(Authority.find.findRowCount() == 0)
            strikeController.saveYamlFileToDatabase((List<Authority>) Yaml.load("authorities.yml"));
        if(User.find.findRowCount() == 0)
            strikeController.saveYamlFileToDatabase((List<User>) Yaml.load("default-users.yml"));

        String login = ctx().session().get("username");
        if (login != null)
            return redirect(routes.HomeController.index());
        return ok(views.html.login.render(formFactory.form(Login.class), ""));
    }

    /**
     * checks whether the current user has the Administrator authority
     * @return a boolean stating the user is Administrator or not
     */
    public boolean isAdmin(){
        String username = ctx().session().get("username");
        User user = User.findByUsername(username);
        return (user.isAdmin());
    }

    /**
     * checks whether the current user has the Subscriber authority
     * @return a boolean stating the user is Subscriber or not
     */
    public boolean isSubscriber(){
        String username = ctx().session().get("username");
        User user = User.findByUsername(username);
        return (user.isSubscriber());
    }

    /**
     * Authenticates the user when accessing (a part of) the application
     * @return a Result depending on the outcome of the authentication
     */
    public Result authenticate() {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors())
            return badRequest(views.html.login.render(loginForm, ""));

        session("username", loginForm.get().username);
        User user = User.findByUsername(ctx().session().get("username"));

        if(user.hasAuthority()) {
            return redirect(routes.HomeController.index());
        }else{
            user.setAuthority("Unauthorized");
            user.save();
            return badRequest(views.html.login.render(loginForm,
                    ConfigFactory.load().getString("unauthorizedText")));
        }
    }

    /**
     * logs out the user from the application
     * @return a redirect to the home page
     */
    public Result logout() {
        session().clear();
        flash("success", "You were successfully logged out!");
        return redirect(routes.HomeController.index());
    }

    /**
     * The Login class for the login into the application
     */
    public static class Login {
        @Constraints.Required
        private String username;

        @Constraints.Required
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String validate() {
            User user = User.authenticate(username, password);
            if (user == null)
                return "Invalid username and/or password!";
            return null;
        }
    }
}