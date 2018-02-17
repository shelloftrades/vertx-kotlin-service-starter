package paths

import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.serviceproxy.ServiceProxyBuilder
import paths.services.SampleService


/**
 * A verticle that consume a service on the bus
 */
@Suppress("unused")
class SampleServiceBusConsumerVerticle : AbstractServiceVerticle() {
    companion object {
        const val SERVICE_ADDRESS = "database-service-address"
    }

    override fun start(startFuture: Future<Void>) {
        // Set up the router
        val router = Router.router(vertx)

        // Create a router that process the request using the service on the bus
        router.get("/").handler({ ctx ->

            // Obtain the handler to the service
            val builder = ServiceProxyBuilder(vertx).setAddress(SERVICE_ADDRESS)
            val service = builder.build(SampleService::class.java!!)

            ctx.response().putHeader("Content-Type", "text/plain")

            service.process(json { obj("hello" to "world")}) {
                when {
                    it.succeeded() -> {
                        val result = it.result()
                        ctx.response().end(result.encodePrettily())
                    }
                    else -> {
                        ctx.response().end(json { obj("failed" to "Yup")  }.encodePrettily())
                    }
                }
            }
        })

        // Start the server
        startServer(9090, router).setHandler {
            startFuture.complete()
        }

    }

    override fun stop(stopFuture: Future<Void>?) {
        stopFuture?.complete()
    }
}