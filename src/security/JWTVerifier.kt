package my.ktor.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

private val algorithm = Algorithm.HMAC256("secret")
fun makeJwtVerifier(): JWTVerifier = JWT
    .require(algorithm)
    .build()
