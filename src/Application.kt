package my.ktor

import com.fasterxml.jackson.databind.SerializationFeature
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import my.ktor.dao.Datasource
import my.ktor.dao.NewsDAO
import my.ktor.route.newsAdminHandler
import my.ktor.route.newsHandler
import my.ktor.security.makeJwtVerifier
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)


@kotlin.jvm.JvmOverloads
fun Application.module() {
    val dbConf: Config = ConfigFactory.load()
    val ds = Datasource.build(dbConf)

    module(Kodein {
        bind<NewsDAO>() with singleton { NewsDAO(ds) }
    })

}

fun Application.module(kodein: Kodein) {
    install(Authentication) {
        val jwtVerifier = makeJwtVerifier()
        jwt {
            verifier(jwtVerifier)
            validate { credential ->
                // TODO any jwt token applied, with no verifiaction. Just for tests
                JWTPrincipal(credential.payload)
            }
        }
    }

    install(Locations)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(StatusPages) {
//        exception<AuthenticationException> { cause ->
//            call.respond(HttpStatusCode.Unauthorized)
//        }
//        exception<AuthorizationException> { cause ->
//            call.respond(HttpStatusCode.Forbidden)
//        }
        exception<Throwable> { cause ->
            // dummy
            call.respond(HttpStatusCode.InternalServerError, message = cause.message?:"")
        }
    }

    routing {
        get("/") {
            call.respond("index")
        }
        route("api") {
            route("v1") {
                newsHandler(kodein)
                authenticate {
                    route("admin") {
                        newsAdminHandler(kodein)
                    }
                }
            }
        }
    }
}

