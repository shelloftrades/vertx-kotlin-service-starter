package paths

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject
import io.vertx.serviceproxy.ServiceBinder
import paths.sample.SampleServiceImpl
import paths.services.SampleService


/**
 * A verticle that register a service on the bus
 */
@Suppress("unused")
class SampleServiceBusVerticle : AbstractVerticle() {
    companion object {
        const val SERVICE_ADDRESS = "database-service-address"
    }
    val service by lazy {
        SampleServiceImpl()
    }

    var consumer: MessageConsumer<JsonObject>? = null

    override fun start(startFuture: Future<Void>) {

        // Register the handler
        consumer = ServiceBinder(vertx)
                .setAddress(SERVICE_ADDRESS)
                .register(SampleService::class.java, service)

        startFuture.complete()
    }

    override fun stop(stopFuture: Future<Void>?) {
        if (consumer != null)
            ServiceBinder(vertx).unregister(consumer)
        stopFuture?.complete()
    }
}