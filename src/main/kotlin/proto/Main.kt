package proto

import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.response.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

val LOG = LoggerFactory.getLogger("proto")


fun main() = runBlocking {
    launch(Dispatchers.Default) {
        LOG.info("launch background")
        backgroundRequests()
    }

    launch(Dispatchers.Default) {
        LOG.info("launch ktor")
        startServer()
    }
    LOG.info("Should never be logged")
}

fun startServer() {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello world ${results.size}")
            }
        }
    }
    server.start(wait = true)
    LOG.info("ktor started")
}

val httpClient = HttpClient(Apache)
var results = listOf<Int>()

suspend fun backgroundRequests() {
    LOG.info("backgroundRequests")
    var loop = 0
    val loader = Loader()
    while (true) {
        LOG.info("loop ${loop++}")
        try {
            results = results + loader.loadIntegers()
        } catch (e: Exception) {
            LOG.error("Exception during retrieval of results", e)
        }
        delay(500)
    }
}

class Loader

suspend fun Loader.loadIntegers(): List<Int> = request("http://httpbin.org/get")

private suspend fun request(getUrl: String): List<Int> {
    LOG.info("request:: $getUrl")
    val resp: HttpResponse = httpClient.call(getUrl).response
    return when (resp.status) {
        HttpStatusCode.OK -> listOf(1)
        else -> error("Bad request?")
    }
}
