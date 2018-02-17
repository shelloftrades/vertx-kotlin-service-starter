package paths.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;



/**
 * The service interface.
 */
@ProxyGen // Generate the proxy and handler
@VertxGen // Generate clients in non-java languages
public interface SampleService {

    int NO_NAME_ERROR = 2;
    int BAD_NAME_ERROR = 3;

    // A couple of factory methods to create an instance and a proxy
//    static SampleService create(Vertx vertx) {
//        return new SampleServiceImpl();
//    }

//    static SampleService createProxy(Vertx vertx, String address) {
//        return new SampleServiceVertxEBProxy(vertx, address)
//    }

    // The service methods
    void process(JsonObject document, Handler<AsyncResult<JsonObject>> resultHandler);
}