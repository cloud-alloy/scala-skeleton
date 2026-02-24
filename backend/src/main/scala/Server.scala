import zio.*
import zio.http.*
import shared.Message

import java.io.File

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

  // Serve static frontend files from /public if it exists (production)
  private val staticDir = new File("public")

  val staticRoutes: Routes[Any, Response] =
    if staticDir.exists() && staticDir.isDirectory then
      Routes(
        Method.GET / trailing -> handler { (path: Path, _: Request) =>
          val filePath = path.toString match
            case "" | "/" => "index.html"
            case p        => p

          val file = new File(staticDir, filePath)
          val targetFile =
            if file.exists() && file.isFile then file
            else new File(staticDir, "index.html") // SPA fallback

          Body.fromFile(targetFile).map(body => Response(body = body))
        }
      )
    else Routes.empty

  override def run =
    val port = sys.env.getOrElse("PORT", "8080").toInt
    for
      _ <- Console.printLine(s"Starting server on http://localhost:$port")
      _ <- Console.printLine("Endpoints:")
      _ <- Console.printLine("  GET /health - Health check")
      _ <- Console.printLine("  GET /api/message - Get hello message")
      _ <- Console.printLine("  GET /api/greet/:name - Get personalised greeting")
      _ <- ZIO.when(staticDir.exists())(
        Console.printLine("  GET /* - Static frontend (SPA mode)")
      )
      _ <- Server.serve(apiRoutes ++ staticRoutes).provide(Server.defaultWithPort(port))
    yield ()
