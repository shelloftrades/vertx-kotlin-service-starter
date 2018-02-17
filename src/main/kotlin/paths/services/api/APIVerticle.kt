package paths.services.api

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.serviceproxy.ServiceProxyBuilder
import paths.services.AbstractServiceVerticle
import paths.services.auth.AuthVerticleV2
import paths.services.AuthService

@Suppress("unused")
class APIVerticle : AbstractServiceVerticle() {
    companion object {
        const val SERVICE_NAME = "API-service"
        const val CONFIG_PORT_KEY = "api.http.port"
        const val CONFIG_PORT_DEFAULT = 8080
    }
    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)


    private fun authenticate(context: RoutingContext) {
        logger.info("Authenticating")

        // Retrieve the parameters defined in the yaml file
        val params: RequestParameters = context.get<Any>("parsedParameters") as RequestParameters

        // retrieve specific parameters
        val username = params.queryParameter("username")?.string ?: ""
        val password = params.queryParameter("password")?.string ?: ""

        // Obtain the handler to the service
        val builder = ServiceProxyBuilder(vertx).setAddress(AuthVerticleV2.SERVICE_ADDRESS)
        val service = builder.build(AuthService::class.java)

        // TODO: Handle error, missing service, timeout, etc..
        // For the time being we assume that the service will always exists and return something
        //bbl

        logger.info("Calling authentication service for '$username'")
        service.authenticate(username, password) {
            when {
                it.succeeded() -> {
                    logger.info("Successful authentication for '$username'")
                    context.response()
                        .setStatusCode(200)
                            .endWithJson(json {obj("token" to it.result() )})
                }
                else -> {
                    logger.error("Authentication failure for '$username'", it.cause())

                    context.response()
                        .setStatusCode(401)
                        .end("Authentication failed")
                }
            }
        }

    }

    /**
     *
     * E.g.:
     *  http://localhost:8080/flows?tags=a,b,c
     */
    private fun getFlows(context: RoutingContext) {

        context.response()
                .setStatusCode(501)
                .end("Not implemented")
    }

    private fun getTest(context: RoutingContext) {

        // Example of how to get a service and call it from the registry
        // (Not needed if we use BUS service)
        getServiceClientByName("auth-service").setHandler {
            when {
                it.succeeded() -> {
                    val client = it.result()
                    client.getNow("/auth") { response ->
                        response.bodyHandler { body ->
                            val token = body.toString()
                            // Don't forget to release the service
                            ServiceDiscovery.releaseServiceObject(discovery, client)
                            // End the response
                            context.response().endWithJson(json { obj ("token" to token)})
                        }
                    }
                }
                else -> {
                    context.response().endWithJson(json { obj ("token" to "failed")})
                }
            }
        }
    }

    override fun start(startFuture: Future<Void>) {
        logger.info("Starting " + this::class.qualifiedName)

        OpenAPI3RouterFactory.create(vertx, "src/main/resources/api.yaml", { ar ->
            if (ar.succeeded()) {
                // Spec loaded with success
                val routerFactory = ar.result()

                // Install the handlers
                routerFactory.addHandlerByOperationId("authenticate-password", Handler<RoutingContext>(this::authenticate))
                routerFactory.addHandlerByOperationId("getFlows", Handler<RoutingContext>(this::getFlows))
                routerFactory.addHandlerByOperationId("getTest", Handler<RoutingContext>(this::getTest))


                // What's our server port
                val port = config().getInteger(CONFIG_PORT_KEY, CONFIG_PORT_DEFAULT)

                CompositeFuture.all(
                        startServer(port, routerFactory.router),
                        publishServiceRecord(SERVICE_NAME, "localhost", port, "/")
                ).setHandler {
                    when {
                        it.succeeded() -> startFuture.complete()
                        else -> startFuture.fail(it.cause())
                    }
                }

            } else {
                logger.error("Something went wrong during router factory initialization", ar.cause())
                // Something went wrong during router factory initialization
                startFuture.fail(ar.cause())
            }
        })

    }



    /*
    private fun createRouter() = Router.router(vertx).apply {
        get("/").handler(handlerRoot)
        get("/islands").handler(handlerIslands)
        get("/countries").handler(handlerCountries)
    }

    //
    // Handlers

    val handlerRoot = Handler<RoutingContext> { req ->
        req.response().end("Welcome Home!")
    }

    val handlerIslands = Handler<RoutingContext> { req ->
        req.response().endWithJson(MOCK_ISLANDS)
    }

    val handlerCountries = Handler<RoutingContext> { req ->
        req.response().endWithJson(MOCK_ISLANDS.map { it.country }.distinct().sortedBy { it.code })
    }

    //
    // Mock data
*/
}
