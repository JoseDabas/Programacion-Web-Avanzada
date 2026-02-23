package urlservice;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * The URL service definition.
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.62.2)",
    comments = "Source: url_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UrlServiceGrpc {

  private UrlServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "urlservice.UrlService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<urlservice.UrlServiceOuterClass.UserRequest,
      urlservice.UrlServiceOuterClass.UrlList> getGetUserUrlsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetUserUrls",
      requestType = urlservice.UrlServiceOuterClass.UserRequest.class,
      responseType = urlservice.UrlServiceOuterClass.UrlList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<urlservice.UrlServiceOuterClass.UserRequest,
      urlservice.UrlServiceOuterClass.UrlList> getGetUserUrlsMethod() {
    io.grpc.MethodDescriptor<urlservice.UrlServiceOuterClass.UserRequest, urlservice.UrlServiceOuterClass.UrlList> getGetUserUrlsMethod;
    if ((getGetUserUrlsMethod = UrlServiceGrpc.getGetUserUrlsMethod) == null) {
      synchronized (UrlServiceGrpc.class) {
        if ((getGetUserUrlsMethod = UrlServiceGrpc.getGetUserUrlsMethod) == null) {
          UrlServiceGrpc.getGetUserUrlsMethod = getGetUserUrlsMethod =
              io.grpc.MethodDescriptor.<urlservice.UrlServiceOuterClass.UserRequest, urlservice.UrlServiceOuterClass.UrlList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetUserUrls"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  urlservice.UrlServiceOuterClass.UserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  urlservice.UrlServiceOuterClass.UrlList.getDefaultInstance()))
              .setSchemaDescriptor(new UrlServiceMethodDescriptorSupplier("GetUserUrls"))
              .build();
        }
      }
    }
    return getGetUserUrlsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<urlservice.UrlServiceOuterClass.AccessRecordRequest,
      urlservice.UrlServiceOuterClass.AccessRecordList> getGetAccessRecordsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetAccessRecords",
      requestType = urlservice.UrlServiceOuterClass.AccessRecordRequest.class,
      responseType = urlservice.UrlServiceOuterClass.AccessRecordList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<urlservice.UrlServiceOuterClass.AccessRecordRequest,
      urlservice.UrlServiceOuterClass.AccessRecordList> getGetAccessRecordsMethod() {
    io.grpc.MethodDescriptor<urlservice.UrlServiceOuterClass.AccessRecordRequest, urlservice.UrlServiceOuterClass.AccessRecordList> getGetAccessRecordsMethod;
    if ((getGetAccessRecordsMethod = UrlServiceGrpc.getGetAccessRecordsMethod) == null) {
      synchronized (UrlServiceGrpc.class) {
        if ((getGetAccessRecordsMethod = UrlServiceGrpc.getGetAccessRecordsMethod) == null) {
          UrlServiceGrpc.getGetAccessRecordsMethod = getGetAccessRecordsMethod =
              io.grpc.MethodDescriptor.<urlservice.UrlServiceOuterClass.AccessRecordRequest, urlservice.UrlServiceOuterClass.AccessRecordList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetAccessRecords"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  urlservice.UrlServiceOuterClass.AccessRecordRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  urlservice.UrlServiceOuterClass.AccessRecordList.getDefaultInstance()))
              .setSchemaDescriptor(new UrlServiceMethodDescriptorSupplier("GetAccessRecords"))
              .build();
        }
      }
    }
    return getGetAccessRecordsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UrlServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UrlServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UrlServiceStub>() {
        @java.lang.Override
        public UrlServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UrlServiceStub(channel, callOptions);
        }
      };
    return UrlServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UrlServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UrlServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UrlServiceBlockingStub>() {
        @java.lang.Override
        public UrlServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UrlServiceBlockingStub(channel, callOptions);
        }
      };
    return UrlServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UrlServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UrlServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UrlServiceFutureStub>() {
        @java.lang.Override
        public UrlServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UrlServiceFutureStub(channel, callOptions);
        }
      };
    return UrlServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * The URL service definition.
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Returns a list of URLs published by a user including associated statistics.
     * </pre>
     */
    default void getUserUrls(urlservice.UrlServiceOuterClass.UserRequest request,
        io.grpc.stub.StreamObserver<urlservice.UrlServiceOuterClass.UrlList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserUrlsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Returns a list of access records for a URL.
     * </pre>
     */
    default void getAccessRecords(urlservice.UrlServiceOuterClass.AccessRecordRequest request,
        io.grpc.stub.StreamObserver<urlservice.UrlServiceOuterClass.AccessRecordList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetAccessRecordsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service UrlService.
   * <pre>
   * The URL service definition.
   * </pre>
   */
  public static abstract class UrlServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return UrlServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service UrlService.
   * <pre>
   * The URL service definition.
   * </pre>
   */
  public static final class UrlServiceStub
      extends io.grpc.stub.AbstractAsyncStub<UrlServiceStub> {
    private UrlServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UrlServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UrlServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns a list of URLs published by a user including associated statistics.
     * </pre>
     */
    public void getUserUrls(urlservice.UrlServiceOuterClass.UserRequest request,
        io.grpc.stub.StreamObserver<urlservice.UrlServiceOuterClass.UrlList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserUrlsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Returns a list of access records for a URL.
     * </pre>
     */
    public void getAccessRecords(urlservice.UrlServiceOuterClass.AccessRecordRequest request,
        io.grpc.stub.StreamObserver<urlservice.UrlServiceOuterClass.AccessRecordList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetAccessRecordsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service UrlService.
   * <pre>
   * The URL service definition.
   * </pre>
   */
  public static final class UrlServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<UrlServiceBlockingStub> {
    private UrlServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UrlServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UrlServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns a list of URLs published by a user including associated statistics.
     * </pre>
     */
    public urlservice.UrlServiceOuterClass.UrlList getUserUrls(urlservice.UrlServiceOuterClass.UserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserUrlsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Returns a list of access records for a URL.
     * </pre>
     */
    public urlservice.UrlServiceOuterClass.AccessRecordList getAccessRecords(urlservice.UrlServiceOuterClass.AccessRecordRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetAccessRecordsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service UrlService.
   * <pre>
   * The URL service definition.
   * </pre>
   */
  public static final class UrlServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<UrlServiceFutureStub> {
    private UrlServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UrlServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UrlServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Returns a list of URLs published by a user including associated statistics.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<urlservice.UrlServiceOuterClass.UrlList> getUserUrls(
        urlservice.UrlServiceOuterClass.UserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserUrlsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Returns a list of access records for a URL.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<urlservice.UrlServiceOuterClass.AccessRecordList> getAccessRecords(
        urlservice.UrlServiceOuterClass.AccessRecordRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetAccessRecordsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_USER_URLS = 0;
  private static final int METHODID_GET_ACCESS_RECORDS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_USER_URLS:
          serviceImpl.getUserUrls((urlservice.UrlServiceOuterClass.UserRequest) request,
              (io.grpc.stub.StreamObserver<urlservice.UrlServiceOuterClass.UrlList>) responseObserver);
          break;
        case METHODID_GET_ACCESS_RECORDS:
          serviceImpl.getAccessRecords((urlservice.UrlServiceOuterClass.AccessRecordRequest) request,
              (io.grpc.stub.StreamObserver<urlservice.UrlServiceOuterClass.AccessRecordList>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetUserUrlsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              urlservice.UrlServiceOuterClass.UserRequest,
              urlservice.UrlServiceOuterClass.UrlList>(
                service, METHODID_GET_USER_URLS)))
        .addMethod(
          getGetAccessRecordsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              urlservice.UrlServiceOuterClass.AccessRecordRequest,
              urlservice.UrlServiceOuterClass.AccessRecordList>(
                service, METHODID_GET_ACCESS_RECORDS)))
        .build();
  }

  private static abstract class UrlServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UrlServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return urlservice.UrlServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UrlService");
    }
  }

  private static final class UrlServiceFileDescriptorSupplier
      extends UrlServiceBaseDescriptorSupplier {
    UrlServiceFileDescriptorSupplier() {}
  }

  private static final class UrlServiceMethodDescriptorSupplier
      extends UrlServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    UrlServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (UrlServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UrlServiceFileDescriptorSupplier())
              .addMethod(getGetUserUrlsMethod())
              .addMethod(getGetAccessRecordsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
