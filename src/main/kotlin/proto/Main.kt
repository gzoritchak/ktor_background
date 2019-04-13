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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

val LOG = LoggerFactory.getLogger("proto")

var stopping = false

suspend fun main() = coroutineScope {

    launch(Dispatchers.Default) {
        LOG.info("launch background")
        backgroundRequests()
    }

    val server = startServer()

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            stopping = true
            server.stop(2L, 2L, TimeUnit.SECONDS)
            println("Shutdown hook ran!")
        }
    })
}

fun startServer() = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello world ${results.size}")
            }
        }
    }.start()

val httpClient = HttpClient(Apache)
var results = listOf<Int>()

suspend fun backgroundRequests() {
    LOG.info("backgroundRequests")
    var loop = 0
    val loader = Loader()
    while (!stopping) {
        LOG.info("loop ${loop++}")
        try {
            results = results + loader.loadIntegers()
        } catch (e: Exception) {
            LOG.error("Exception during retrieval of results", e)
        }
        delay(500)
    }
    LOG.info("stopping background requests")
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

