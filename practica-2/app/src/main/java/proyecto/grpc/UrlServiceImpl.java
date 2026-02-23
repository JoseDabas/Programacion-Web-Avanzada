package proyecto.grpc;

import io.grpc.stub.StreamObserver;
import proyecto.clases.AccessRecord;
import proyecto.clases.URL;
import proyecto.services.AccessRecordServices;
import proyecto.services.URLServices;
import urlservice.UrlServiceGrpc;
import urlservice.UrlServiceOuterClass;

import java.util.List;

public class UrlServiceImpl extends UrlServiceGrpc.UrlServiceImplBase {

    @Override
    public void getUserUrls(UrlServiceOuterClass.UserRequest request,
            StreamObserver<UrlServiceOuterClass.UrlList> responseObserver) {
        // Get the username from the request
        String username = request.getUsername();

        // Use your service to get the list of URLs for the user
        List<URL> urls = URLServices.getInstance().findByUsername(username);

        // Convert your list of URLs to a UrlList message
        UrlServiceOuterClass.UrlList.Builder urlListBuilder = UrlServiceOuterClass.UrlList.newBuilder();
        for (URL url : urls) {
            UrlServiceOuterClass.UrlRecord urlRecord = UrlServiceOuterClass.UrlRecord.newBuilder()
                    .setFullUrl(url.getUrlViejo())
                    .setShortUrl(url.getUrlNuevo())
                    .setCreationDate(url.getFechaCreacion().toString())
                    .build();
            urlListBuilder.addUrls(urlRecord);
        }

        // Send the response
        responseObserver.onNext(urlListBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAccessRecords(UrlServiceOuterClass.AccessRecordRequest request,
            StreamObserver<UrlServiceOuterClass.AccessRecordList> responseObserver) {
        // Get the URL from the request
        String url = request.getUrl();

        // Use your service to get the list of access records for the URL
        List<AccessRecord> accessRecords = AccessRecordServices.getInstance().findByURL(url);

        // Convert your list of access records to an AccessRecordList message
        UrlServiceOuterClass.AccessRecordList.Builder accessRecordListBuilder = UrlServiceOuterClass.AccessRecordList
                .newBuilder();
        for (AccessRecord accessRecord : accessRecords) {
            UrlServiceOuterClass.AccessRecord accessRecordMessage = UrlServiceOuterClass.AccessRecord.newBuilder()
                    .setId(accessRecord.getId().toString())
                    .setAccessTime(accessRecord.getAccessTime().toString())
                    .setBrowser(accessRecord.getBrowser())
                    .setIpAddress(accessRecord.getIpAddress())
                    .setOperatingSystemPlatform(accessRecord.getOperatingSystemPlatform())
                    .setUrl(accessRecord.getUrl())
                    .build();
            accessRecordListBuilder.addAccessRecords(accessRecordMessage);
        }

        // Send the response
        responseObserver.onNext(accessRecordListBuilder.build());
        responseObserver.onCompleted();
    }
}
