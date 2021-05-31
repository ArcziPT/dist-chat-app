package com.arczipt.chatService.auth

import play.api.mvc.Request
import com.arczipt.chatService.system.JWT

object Auth{
    def getSenderId[A](jwt: JWT, request: Request[A]) = {
        for{
            token <- request.headers.get("Authorization")
            claims <- {
                val claims = jwt.decode(token)
                if(claims.isSuccess) Some(claims.get)
                else None
            }
        } yield (claims \ "userId").as[Long]
    }
}