package paths

import io.vertx.core.AbstractVerticle
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.HttpEndpoint
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject


@Suppress("unused")
abstract class AbstractServiceVerticle : AbstractVerticle() {
    private val logger = LoggerFactory.getLogger("AbstractServiceVerticle.FlowVerticle")
    private val flowsController = FlowController()

    val discovery: ServiceDiscovery by lazy {
        ServiceDiscovery.create(vertx)
    }
    private var record: Record? = null
    private var server: HttpServer? = null

    /**
     * Start the HTTP server
     */
    fun startServer(port: Int, router: Router): Future<Any> {
        val done = Future.future<Any>()

        this.server = vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(port) {
                    when {
                        it.succeeded() -> done.complete()
                        else -> done.fail(it.cause())
                    }
                }
        return done
    }

    /**
     * Publish the service record
     */
    fun publishServiceRecord(name: String, host:String, port:Int, root: String): Future<Any> {
        logger.info("Publishing service:")
        val done = Future.future<Any>()
        val record = HttpEndpoint.createRecord(name, host, port, root)

        discovery.publish(record, {
            when {
                it.succeeded() -> {
                    done.complete()
                    this.record = it.result()
                    logger.info("Service published")
                }
                else -> done.fail(it.cause())
            }

        })

        return done
    }

    /**
     * Unpublish the service record
     */
    fun unpublishRecord(): Future<Any> {
        logger.info("Unpublishing service")
        val done = Future.future<Any>()
        val record = this.record

        // Never published (or publication failed)
        if (record == null) {
            done.complete()
            return done
        }

        // Unpublish and nullify the record
        discovery.unpublish(record.registration, {
            when {
                it.succeeded() -> done.complete()
                else -> done.fail(it.cause())
            }

            // remove the discovery object and nullify the record
            discovery.close()
            this.record = null
        })

        return done
    }

    fun shutdownServer(): Future<Any> {
        val done = Future.future<Any>()

        logger.info("Shutting down HttpServer")
        if (server == null) {
            done.complete()
        }
        else {
            server?.close {
                when {
                    it.succeeded() -> done.complete()
                    else -> done.fail(it.cause())
                }
            }
        }
        return done
    }

    /**
     * Automatically unpublish the service when it stops
     */
    override fun stop(stopFuture: Future<Void>?) {
        logger.info("Stopping " + this::class.qualifiedName)


        // Shutting the server AND waiting for the answer should prevents a
        //  java.net.BindException: Address already in use with graddle (but does not)

        CompositeFuture.all(
            unpublishRecord(),
            shutdownServer()
        )
        .setHandler {
            when {
                it.succeeded() -> {
                    stopFuture?.complete()
                }
                else -> stopFuture?.fail(it.cause())
            }
        }
    }

    //
    // Utilities

    /**
     * Extension to the HTTP response to output JSON objects.
     */
    fun HttpServerResponse.endWithJson(data: Any) {

        this.putHeader("Content-Type", "application/json; charset=utf-8")

        if (data is JsonObject) {
            this.end(data.encodePrettily())
        }
        else {
            this.end(io.vertx.core.json.Json.encodePrettily(data))
        }


    }


    /**
     * Get a service
     */
    fun getServiceClientByName(name: String): Future<HttpClient> {
        val future = Future.future<HttpClient>()

        // Get a record by name
        discovery.getRecord({ r ->
            r.name == name
        }, { ar ->
            if (ar.succeeded()) {
                val record = ar.result()
                if (record != null) {
                    // we have a record, let's get a reference and a client
                    val reference = discovery.getReference(record)
                    val client = reference.getAs(HttpClient::class.java)
                    future.complete(client)

                } else {
                    future.fail("The lookup succeeded, but no matching service")
                }
            } else {
                future.fail(ar.cause())
                // lookup failed
            }
        })
        return future
    }
}
