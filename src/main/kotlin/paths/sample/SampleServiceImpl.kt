package paths.sample

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import paths.services.SampleService

class SampleServiceImpl : SampleService {
    override fun process(document: JsonObject?,
                         resultHandler: Handler<AsyncResult<JsonObject>>?) {

        resultHandler?.handle(Future.succeededFuture( json { obj("toto" to "tata")}  ))

    }
}