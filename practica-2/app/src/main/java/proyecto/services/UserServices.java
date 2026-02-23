package proyecto.services;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import proyecto.clases.Usuario;

import java.util.List;

public class UserServices extends MongoServices<Usuario> {
    private static UserServices instance = null;

    private UserServices() {
        super(Usuario.class);
    }

    public static UserServices getInstance() {
        if (instance == null) {
            instance = new UserServices();
        }
        return instance;
    }

    public Usuario findByUsername(String username) {
        return this.findOne("username", username);
    }

    public List<Usuario> findAll(int pageNumber, int pageSize) {
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        Datastore datastore = getConexionMorphia();
        List<Usuario> usuarios = datastore.find(Usuario.class)
                .iterator(new FindOptions().skip((pageNumber - 1) * pageSize).limit(pageSize))
                .toList();
        return usuarios;
    }

    public void deleteByUsername(String username) {
        this.delete("username", username);
    }
}
