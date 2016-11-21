package controllers;

import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

public class SecurityController extends Controller {
    @Inject
    private FormFactory formFactory;

    public Result login() {
        if (isDev) {
            ctx().session().set("username", 'bkdl;sakd;l');
            return redirect(routes.HomeController.index());
        }

        String login = ctx().session().get("username");
        if (login != null)
            return redirect(routes.HomeController.index());
        return ok(views.html.login.render(formFactory.form(Login.class)));
    }

    public Result authenticate() {
        Form<Login> loginForm = formFactory.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors())
            return badRequest(views.html.login.render(loginForm));

        session("username", loginForm.get().username);
        return redirect(routes.HomeController.index());
    }

    public Result logout() {
        session().clear();
        flash("success", "You were successfully logged out!");
        return redirect(routes.HomeController.index());
    }

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