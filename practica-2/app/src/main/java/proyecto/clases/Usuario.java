package proyecto.clases;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("Usuario")
public class Usuario {
    @Id
    private ObjectId id;
    private String username;
    private String password;
    private boolean admin;

    public Usuario(ObjectId id, String username, String password, boolean admin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.admin = admin;
    }

    public Usuario() {
    }

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
