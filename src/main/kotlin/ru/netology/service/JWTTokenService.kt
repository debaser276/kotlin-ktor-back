package ru.netology.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

class JWTTokenService {
    private val secret = "a5ce3f44-0e15-4805-8e74-3bec4039e244"
    private val algo = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algo).build()

    fun generate(id: Int): String = JWT.create().withClaim("id", id).sign(algo)
}