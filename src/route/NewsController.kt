package my.ktor.route


import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import kotlinx.coroutines.*
import model.News
import my.ktor.dao.NewsDAO
import my.ktor.model.SuccessResponse
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.time.Instant
import java.util.*
import java.util.concurrent.Executors

@Location("/news") class NewsListLoc
@Location("/news") class NewsLoc//(val id: Long? = null, val title: String, val text: String)
@Location("/news/{id}") data class NewsIdLoc(val id: Long)
@Location("/news/generate/{count}") data class NewsGenLoc(val count: Int)

val execPool: ExecutorCoroutineDispatcher by lazy {
    Executors.newFixedThreadPool(4).asCoroutineDispatcher()
}

fun Route.newsHandler(kodein: Kodein) {

    val dao by kodein.instance<NewsDAO>()

    get<NewsListLoc> {
        val news = dao.readAll()
        call.respond(SuccessResponse(news))
    }

    get<NewsIdLoc> { dto ->
        val obj = dao.read(dto.id)
        call.respond(SuccessResponse(obj))
    }

}
fun Route.newsAdminHandler(kodein: Kodein) {
    val dao by kodein.instance<NewsDAO>()

    post<NewsLoc> {
        val n = call.receive<News>()
        val obj = dao.create(n)
        call.respond(SuccessResponse(obj))
    }

    put<NewsLoc> { dto ->
        val n = call.receive<News>()
        val obj = dao.update(n)
        call.respond(SuccessResponse(obj))
    }

    delete<NewsIdLoc> { dto ->
        val result = dao.delete(dto.id)
        call.respond(SuccessResponse(result))
    }

    get<NewsGenLoc> { dto ->
        println("main thread ${Thread.currentThread().name}")
        launch(execPool) { generateNews(dto.count, dao) }
        call.respond(SuccessResponse("generated"))
    }

}

private fun generateNews(count: Int, dao: NewsDAO)  {
    println("begin generate")
    println("gen thread ${Thread.currentThread().name}")
    (1..count).forEach {
        dao.create(News(null, Instant.now(), UUID.randomUUID().toString(), UUID.randomUUID().toString()))
    }
    println("end generate")
}
