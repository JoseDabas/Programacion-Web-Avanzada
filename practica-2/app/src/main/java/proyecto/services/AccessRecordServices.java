package proyecto.services;

import proyecto.clases.AccessRecord;

import java.util.List;

public class AccessRecordServices extends MongoServices<AccessRecord> {
    private static AccessRecordServices instance = null;

    private AccessRecordServices() {
        super(AccessRecord.class);
    }

    public static AccessRecordServices getInstance() {
        if (instance == null) {
            instance = new AccessRecordServices();
        }
        return instance;
    }

    public List<AccessRecord> findByURL(String url) {
        return this.findByField("url", url).stream().toList();
    }
}
