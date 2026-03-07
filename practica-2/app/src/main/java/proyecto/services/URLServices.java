package proyecto.services;

import proyecto.clases.URL;

import java.util.List;

public class URLServices extends MongoServices<URL> {
    private static URLServices instance = null;

    private URLServices() {
        super(URL.class);
    }

    public static URLServices getInstance() {
        if (instance == null) {
            instance = new URLServices();
        }
        return instance;
    }

    public URL findByShortURL(String shortURL) {
        return this.findOne("urlNuevo", shortURL);
    }

    public List<URL> findByUsername(String username) {
        return this.findByField("usuario", username).stream().toList();
    }

    public void deleteByShortURL(String shortURL) {
        this.delete("urlNuevo", shortURL);
    }
}
