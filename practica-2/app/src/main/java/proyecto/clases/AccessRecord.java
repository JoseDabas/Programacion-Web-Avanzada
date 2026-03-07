package proyecto.clases;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Entity("AccessRecord")
public class AccessRecord {
    @Id
    private ObjectId id;
    private LocalDateTime accessTime;
    private String browser;
    private String ipAddress;
    private String operatingSystemPlatform;
    private String url;

    public AccessRecord(ObjectId id, LocalDateTime accessTime, String browser, String ipAddress, String clientDomain,
            String operatingSystemPlatform, String url) {
        this.id = id;
        this.accessTime = accessTime;
        this.browser = browser;
        this.ipAddress = ipAddress;
        this.operatingSystemPlatform = operatingSystemPlatform;
        this.url = url;
    }

    public AccessRecord() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDateTime getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(LocalDateTime accessTime) {
        this.accessTime = accessTime;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOperatingSystemPlatform() {
        return operatingSystemPlatform;
    }

    public void setOperatingSystemPlatform(String operatingSystemPlatform) {
        this.operatingSystemPlatform = operatingSystemPlatform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
