import scalatags.Text.all.*
import scalatags.Text.tags2.{title as titleTag}
import shared.Message

import java.io.File
import java.nio.file.Files

object SsrRenderer:

  case class ViteAssets(js: String, css: List[String] = Nil)

  /** Read the Vite manifest to find hashed asset paths for the entry point. */
  def readViteAssets(publicDir: File): Option[ViteAssets] =
    val manifestFile = new File(publicDir, ".vite/manifest.json")
    if manifestFile.exists() then
      val content = new String(Files.readAllBytes(manifestFile.toPath))
      // Vite 6 uses the HTML entry as the manifest key
      val entryKey = if content.contains("\"index.html\"") then "\"index.html\"" else "\"main.js\""
      val mainIdx = content.indexOf(entryKey)
      if mainIdx >= 0 then
        val blockStr = content.substring(mainIdx)
        val filePattern = """"file"\s*:\s*"([^"]+)"""".r
        val cssPattern = """"css"\s*:\s*\[([^\]]*)\]""".r
        val jsFile = filePattern.findFirstMatchIn(blockStr).map(_.group(1))
        val cssFiles = cssPattern
          .findFirstMatchIn(blockStr)
          .map { m =>
            """"([^"]+)"""".r.findAllMatchIn(m.group(1)).map(_.group(1)).toList
          }
          .getOrElse(Nil)
        jsFile.map(js => ViteAssets(js, cssFiles))
      else None
    else None

  // Inline CSS — must match frontend/index.html styles
  private val inlineCss: String =
    """* { box-sizing: border-box; margin: 0; padding: 0; }
      |body {
      |  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      |  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      |  min-height: 100vh;
      |  display: flex;
      |  justify-content: center;
      |  align-items: center;
      |  padding: 20px;
      |}
      |.container {
      |  background: white;
      |  border-radius: 16px;
      |  padding: 40px;
      |  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      |  max-width: 500px;
      |  width: 100%;
      |}
      |h1 { color: #333; margin-bottom: 24px; text-align: center; }
      |h2 { color: #555; margin-bottom: 16px; font-size: 1.2rem; }
      |.message-box, .counter-box {
      |  background: #f8f9fa;
      |  border-radius: 8px;
      |  padding: 20px;
      |  margin-bottom: 20px;
      |}
      |p { color: #666; margin-bottom: 8px; line-height: 1.6; }
      |button {
      |  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      |  color: white;
      |  border: none;
      |  padding: 12px 24px;
      |  border-radius: 8px;
      |  cursor: pointer;
      |  font-size: 1rem;
      |  margin-right: 10px;
      |  margin-top: 10px;
      |  transition: transform 0.2s, box-shadow 0.2s;
      |}
      |button:hover {
      |  transform: translateY(-2px);
      |  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
      |}
      |button:active { transform: translateY(0); }
      |footer {
      |  text-align: center;
      |  margin-top: 20px;
      |  padding-top: 20px;
      |  border-top: 1px solid #eee;
      |}
      |footer p { color: #999; font-size: 0.9rem; }""".stripMargin

  /** Render a full HTML page with SSR content and Vite asset references. */
  def renderPage(
      pageTitle: String,
      pageDescription: String,
      assets: ViteAssets,
      pageContent: Frag
  ): String =
    "<!DOCTYPE html>" + html(
      attr("lang") := "en",
      head(
        meta(attr("charset") := "UTF-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1.0"),
        titleTag(pageTitle),
        meta(name := "description", content := pageDescription),
        meta(attr("property") := "og:title", content := pageTitle),
        meta(attr("property") := "og:description", content := pageDescription),
        meta(attr("property") := "og:type", content := "website"),
        tag("style")(raw(inlineCss)),
        assets.css.map(cssPath => link(rel := "stylesheet", href := s"/$cssPath"))
      ),
      body(
        div(
          id := "app",
          pageContent
        ),
        script(`type` := "module", src := s"/${assets.js}")
      )
    ).render

  /** SSR render of the home page. */
  def renderHomePage(assets: ViteAssets): String =
    val msg = Message.hello
    renderPage(
      pageTitle = "Scala Full-Stack App",
      pageDescription = "A full-stack Scala application built with ZIO, Laminar, and Scala.js",
      assets = assets,
      pageContent = div(
        cls := "container",
        h1("Scala Full-Stack App"),
        div(
          cls := "message-box",
          p(s"Shared Message: ${msg.text}"),
          p(s"Timestamp: ${msg.timestamp}")
        ),
        div(
          cls := "counter-box",
          h2("Reactive Counter Demo"),
          p("Count: 0"),
          button("Increment"),
          button("Decrement")
        ),
        tag("footer")(
          p("Built with Scala.js + Laminar + ZIO")
        )
      )
    )
