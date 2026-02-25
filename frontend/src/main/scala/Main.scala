import com.raquo.laminar.api.L.*
import org.scalajs.dom
import shared.Message

object Main:
  def main(args: Array[String]): Unit =
    val message = Message.hello
    val counter = Var(0)

    // Clear any server-rendered content before Laminar takes over
    val appContainer = dom.document.getElementById("app")
    appContainer.innerHTML = ""

    val app = div(
      cls := "container",
      h1("Scala Full-Stack App"),
      div(
        cls := "message-box",
        p(s"Shared Message: ${message.text}"),
        p(s"Timestamp: ${message.timestamp}")
      ),
      div(
        cls := "counter-box",
        h2("Reactive Counter Demo"),
        p(
          "Count: ",
          child.text <-- counter.signal.map(_.toString)
        ),
        button(
          "Increment",
          onClick --> { _ => counter.update(_ + 1) }
        ),
        button(
          "Decrement",
          onClick --> { _ => counter.update(_ - 1) }
        )
      ),
      footerTag(
        p("Built with Scala.js + Laminar + ZIO")
      )
    )

    render(appContainer, app)
