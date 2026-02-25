import zio.*
import zio.http.*
import shared.Message

import java.io.File

object Main extends ZIOAppDefault:

  private def contentTypeFor(fileName: String): Header.ContentType =
    val mediaType = fileName.split('.').lastOption match
      case Some("html")               => MediaType.text.html
      case Some("js")                 => MediaType.application.javascript
      case Some("css")                => MediaType.text.css
      case Some("json")               => MediaType.application.json
      case Some("png")                => MediaType.image.png
      case Some("jpg") | Some("jpeg") => MediaType.image.jpeg
      case Some("gif")                => MediaType.image.gif
      case Some("svg")                => MediaType.image.`svg+xml`
      case Some("ico")                => MediaType.image.`x-icon`
      case Some("woff")               => MediaType.application.`font-woff`
      case Some("woff2")              => MediaType(mainType = "font", subType = "woff2")
      case Some("ttf")                => MediaType(mainType = "font", subType = "ttf")
      case Some("map")                => MediaType.application.json
      case _                          => MediaType.application.`octet-stream`
    Header.ContentType(mediaType)

  val apiRoutes: Routes[Any, Response] = Routes(
    Method.GET / "health" -> handler(Response.text("OK")),
    Method.GET / "api" / "message" -> handler {
      val msg = Message.hello
      Response.json(s"""{"text": "${msg.text}", "timestamp": ${msg.timestamp}}""")
    },
    Method.GET / "api" / "greet" / string("name") -> handler { (name: String, _: Request) =>
      val msg = Message.create(s"Hello, $name!")
      Response.json(s"""{"text": "${msg.text}", "timestamp": ${msg.timestamp}}""")
    }
  )

  private val staticDir = new File("public")

  // Read Vite manifest once at startup for SSR asset references
  private val viteAssets: Option[SsrRenderer.ViteAssets] =
    if staticDir.exists() then SsrRenderer.readViteAssets(staticDir) else None

  val staticRoutes: Routes[Any, Response] =
    if staticDir.exists() && staticDir.isDirectory then
      Routes(
        Method.GET / trailing -> handler { (path: Path, _: Request) =>
          val filePath = path.toString match
            case "" | "/" => "index.html"
            case p        => p

          // SSR for the home page when Vite assets are available
          if filePath == "index.html" && viteAssets.isDefined then
            ZIO.succeed(
              Response(
                body = Body.fromString(SsrRenderer.renderHomePage(viteAssets.get)),
                headers = Headers(Header.ContentType(MediaType.text.html))
              )
            )
          else
            val file = new File(staticDir, filePath)
            val targetFile =
              if file.exists() && file.isFile then file
              else new File(staticDir, "index.html") // SPA fallback

            Body.fromFile(targetFile).map { body =>
              Response(
                body = body,
                headers = Headers(contentTypeFor(targetFile.getName))
              )
            }
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
      _ <- ZIO.when(viteAssets.isDefined)(
        Console.printLine(s"  SSR enabled - entry: ${viteAssets.get.js}")
      )
      _ <- Server.serve(apiRoutes ++ staticRoutes).provide(Server.defaultWithPort(port))
    yield ()
