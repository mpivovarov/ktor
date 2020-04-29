package my.ktor

import com.fasterxml.jackson.databind.SerializationFeature
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
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
//    install(Authentication) {
//        basic("name") {
//            realm = "ktor"
//            validate { credentials ->
//                if (credentials.password == "password") UserIdPrincipal(credentials.name) else null
//            }
//        }
////        val jwtVerifier = makeJwtVerifier("testIssuer", "testAudience")
////        jwt {
////            verifier(jwtVerifier)
////            validate { credential ->
////                if (credential.payload.audience.contains("testAudience"))
////                    JWTPrincipal(credential.payload)
////                else
////                    null
////            }
////        }
//    }

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
//                authenticate {
                    route("admin") {
                        newsAdminHandler(kodein)
                    }
//                }
            }
        }
    }
}

