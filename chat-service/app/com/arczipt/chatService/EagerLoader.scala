package com.arczipt.chatService

import com.google.inject.AbstractModule
import com.google.inject.name.Names

// A Module is needed to register bindings
class StartUpLoaderModule extends AbstractModule {
    override def configure() = {
        bind(classOf[StartUpService]).asEagerSingleton
    }
}