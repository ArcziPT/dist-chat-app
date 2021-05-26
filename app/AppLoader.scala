import play.api.ApplicationLoader
import play.api.Configuration
import play.api.inject._
import play.api.inject.guice._

class AppLoader extends GuiceApplicationLoader() {
  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val hostname = sys.env("HOSTNAME")
    val extra = Configuration.from(Map("akka.remote.classic.netty.tcp.hostname" -> hostname))
    initialBuilder
      .in(context.environment)
      .loadConfig(extra ++ context.initialConfiguration)
      .overrides(overrides(context): _*)
  }
}