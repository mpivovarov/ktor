package my.ktor.route

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import my.ktor.MySession
import io.ktor.routing.*
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set

object Route {
    fun Application.routes() = run {
        routing {
            get("/") {
                call.respond(MySession())
            }

            // Static feature. Try to access `/static/ktor_logo.svg`
            static("/static") {
                resources("static")
            }

            get("/session/increment") {
                val session = call.sessions.get<MySession>() ?: MySession()
                call.sessions.set(session.copy(count = session.count + 1))
                call.respondText("Counter is ${session.count}. Refresh to increment.")
            }

            get("/json/jackson") {
                call.respond(mapOf("hello" to "world"))
            }
        }
    }
}