package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class User extends Model {
    @Id
    private Integer id;
    private String username;
    private String fullName;
    private String rights;

    public static Model.Finder<Integer, User> find = new Model.Finder<>(User.class);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRights() { return rights; }

    public void setRights(String rights) { this.rights = rights; }

    public static List<User> getAllUsers(){
        List<User> users = Ebean.find(User.class).findList();
        return users;
    }

    public static User[] getAllUsersAsArray(){
        List<User> users = Ebean.find(User.class).findList();
        return users.toArray(new User[ users.size()]);
    }

    public static String[] getAllUserNames(){
        List<String> userNames = Ebean.find(User.class).findList().stream().map(User::getUsername).collect(Collectors.toList());
        return userNames.toArray(new String[userNames.size()]);
    }

    public boolean isAdmin() {
        return ConfigFactory.load().getStringList("admins").contains(username);
    }

    public static User findByUsername(String username) {
        return find.where().eq("username", username).findUnique();
    }

    public static User authenticate(String username, String password) {
        DirContext ctx = null;
        Config conf = ConfigFactory.load();

        try {
            String principle = conf.getString("ldap.principal").replace("{0}", username);

            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, conf.getString("ldap.url"));
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, principle);
            env.put(Context.SECURITY_CREDENTIALS, password);

            ctx = new InitialDirContext(env);

            User user = findByUsername(username);
            if (user == null)
                user = new User();

            Attributes attributes = ctx.getAttributes(principle);
            user.setUsername(username);
            user.setFullName(attributes.get("displayName").get().toString().trim());
            user.save();

            return user;
        }
        catch (NamingException e) {
            return null;
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (NamingException e) {
                    // Ignore exception
                }
            }
        }
    }
}
