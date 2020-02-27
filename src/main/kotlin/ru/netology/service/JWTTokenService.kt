package ru.netology.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JWTTokenService {
    private val secret = "a5ce3f44-0e15-4805-8e74-3bec4039e244"
    private val algo = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algo).build()

    fun generate(id: Int): String = JWT.create()
        .withClaim("id", id)
        .withExpiresAt(Calendar.getInstance().apply { add(Calendar.MINUTE, 5) }.time)
        .sign(algo)
}