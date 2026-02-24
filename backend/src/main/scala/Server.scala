import zio.*
import zio.http.*
import shared.Message

object Main extends ZIOAppDefault:

  val apiRoutes: Routes[Any, Response] = Routes(
    // Health check endpoint
    Method.GET / "health" -> handler(Response.text("OK")),

    // API endpoint returning a message
    Method.GET / "api" / "message" -> handler {
      val msg = Message.hello
      Response.json(s"""{"text": "${msg.text}", "timestamp": ${msg.timestamp}}""")
    },

    // Simple greeting endpoint
    Method.GET / "api" / "greet" / string("name") -> handler { (name: String, _: Request) =>
      val msg = Message.create(s"Hello, $name!")
      Response.json(s"""{"text": "${msg.text}", "timestamp": ${msg.timestamp}}""")
    }
  )

  override def run =
    for
      _ <- Console.printLine("Starting ZIO HTTP server on http://localhost:8080")
      _ <- Console.printLine("Endpoints:")
      _ <- Console.printLine("  GET /health - Health check")
      _ <- Console.printLine("  GET /api/message - Get hello message")
      _ <- Console.printLine("  GET /api/greet/:name - Get personalized greeting")
      _ <- Server.serve(apiRoutes).provide(Server.defaultWithPort(8080))
    yield ()
