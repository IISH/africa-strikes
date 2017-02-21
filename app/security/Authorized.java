package security;

import controllers.routes;
import models.User;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Authorized {
    @play.mvc.With(AuthorizedAction.class)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface With {
        enum Authority {
            NO, SUBSCRIBER, ADMIN
        }

        Authority value() default Authority.NO;
    }

    public static class AuthorizedAction extends Action<With> {
        @Override
        public CompletionStage<Result> call(Http.Context ctx) {
//            if (isDev) {
//                return delegate.call(ctx);
//            }

            String username = ctx.session().get("username");
            if (username == null) {
                Result loginForm = redirect(routes.SecurityController.login());
                return CompletableFuture.completedFuture(loginForm);
            }

            User user = User.findByUsername(username);
            if (user == null) {
                ctx.session().clear();
                Result loginForm = redirect(routes.SecurityController.login());
                return CompletableFuture.completedFuture(loginForm);
            }

            if ((configuration.value() == With.Authority.SUBSCRIBER) && !user.hasAuthority()) {
                Result unauthorized = unauthorized(views.html.defaultpages.unauthorized.render());
                return CompletableFuture.completedFuture(unauthorized);
            }
            if ((configuration.value() == With.Authority.ADMIN) && !user.isAdmin()) {
                Result unauthorized = unauthorized(views.html.defaultpages.unauthorized.render());
                return CompletableFuture.completedFuture(unauthorized);
            }

            return delegate.call(ctx);
        }
    }
}
