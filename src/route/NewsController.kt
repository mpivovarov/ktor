package my.ktor.route


import io.ktor.application.call
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import model.News
import my.ktor.dao.NewsDAO
import my.ktor.model.SuccessResponse
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import java.time.Instant

@Location("/news") class NewsListLoc
@Location("/news") class NewsLoc//(val id: Long? = null, val title: String, val text: String)
@Location("/news/{id}") data class NewsIdLoc(val id: Long)

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
}