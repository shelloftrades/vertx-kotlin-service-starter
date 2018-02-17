package paths.services.auth

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

import io.vertx.kotlin.ext.auth.KeyStoreOptions
import io.vertx.kotlin.ext.jwt.JWTOptions
import paths.services.AbstractServiceVerticle


@Suppress("unused")
class AuthVerticle : AbstractServiceVerticle() {
    companion object {
        const val SERVICE_NAME = "auth-service"
        const val CONFIG_PORT_KEY = "AuthVerticle.http.port"
        const val CONFIG_PORT_DEFAULT = 8081
    }

    private val logger = LoggerFactory.getLogger("Path.AuthVerticle")

    override fun start(startFuture: Future<Void>) {
        logger.info("Stopping " + this::class.qualifiedName)

        // Get values from config
        val keystore = config().getString("auth.keystore")
        val password = config().getString("auth.keystore.password")

        logger.info("Loading keystore at " + keystore)

        // Set up the JWT options
        val jwtAuthOptions = JWTAuthOptions()
        jwtAuthOptions.keyStore = KeyStoreOptions(
                type = "pkcs12",
                path = keystore,
                password = password)

        // Create the JWT token builder
        val jwt = JWTAuth.create(vertx, jwtAuthOptions)

        // Set up the router
        val router = Router.router(vertx)

        // This is our route
        router.get("/auth").handler({ ctx ->
            // TODO: Authenticate the user
            val username = "Joe"

            val jwtOptions = JWTOptions(expiresInSeconds = 60)
            ctx.response().putHeader("Content-Type", "text/plain")
            ctx.response().end(jwt.generateToken(json { obj("username" to username) }, jwtOptions))
        })

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

    }
}

