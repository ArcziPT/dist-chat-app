package com.arczipt.chatService.system

import play.api.libs.json.JsObject
import pdi.jwt.JwtJson
import play.api.libs.json.Json
import pdi.jwt.JwtAlgorithm
import com.google.inject.Inject
import play.api.Configuration
import java.time.Clock

class JWT @Inject() (config: Configuration){
    implicit val clock: Clock = Clock.systemUTC
    val algo = JwtAlgorithm.HS256
    val key = config.underlying.getString("chat_app.secret_key")

    def encode(claims: JsObject) = JwtJson.encode(claims, key, algo)
    def decode(token: String) = JwtJson.decodeJson(token.substring(7), key, Seq(algo))
}