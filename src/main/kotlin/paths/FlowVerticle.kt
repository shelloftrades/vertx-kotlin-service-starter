package paths

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.RequestParameter
import io.vertx.ext.web.api.RequestParameters
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.servicediscovery.ServiceDiscovery

@Suppress("unused")
class FlowVerticle : AbstractServiceVerticle() {
    companion object {
        const val SERVICE_NAME = "flow-service"
        const val CONFIG_PORT_KEY = "FlowVerticle.http.port"
        const val CONFIG_PORT_DEFAULT = 8080
    }
    private val logger = LoggerFactory.getLogger(this::class.qualifiedName)
    private val flowsController = FlowController()

    /**
     *
     * E.g.:
     *  http://localhost:8080/flows?tags=a,b,c
     */
    private fun getFlows(context: RoutingContext) {
        logger.info("getFlows")

        // Retrieve the parameters defined in the yaml file
        val params: RequestParameters = context.get<Any>("parsedParameters") as RequestParameters

        // retrieve specific parameters
        val limitParm = params.queryParameter("limit")?.integer ?: 0
        val tagsParm = params.queryParameter("tags")?.array ?: emptyList<RequestParameter>()
        // var pathParam = params.pathParameter("pathParam").getFloat()

        // Display the parameters in the console
        logger.info("limit param: " + limitParm)
        logger.info("tags_params>" + tagsParm.size)
        for (p in tagsParm) {
            logger.info("tag param:" +  p.string)
        }

        // Create a List<String> from the List<RequestParameters>
        val tags: List<String> = tagsParm.map{  it.string  }

        // Call the controller to get the list of flows as per the parameters
        val flows = flowsController.getFlows(tags, limitParm)

        // End the response with the list of flows as Json
        context.response().endWithJson(flows)
    }

    private fun getFlowsFailure(context: RoutingContext) {
        logger.info("findPetsFailure")
        context.fail(500)
    }


    private fun getTest(context: RoutingContext) {

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
                routerFactory.addHandlerByOperationId("getFlows", Handler<RoutingContext>(this::getFlows))
                routerFactory.addFailureHandlerByOperationId("getFlows", Handler<RoutingContext>(this::getFlowsFailure))

                routerFactory.addHandlerByOperationId("getTest", Handler<RoutingContext>(this::getTest))

                // get the router
                val router = routerFactory.router

                // What's our server port
                val port = config().getInteger(CONFIG_PORT_KEY, CONFIG_PORT_DEFAULT)

                CompositeFuture.all(
                    startServer(port, router),
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
