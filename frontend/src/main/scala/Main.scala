import com.raquo.laminar.api.L.*
import org.scalajs.dom
import shared.Message

object Main:
  def main(args: Array[String]): Unit =
    // Create a message using shared code
    val message = Message.hello

    // Simple reactive counter to demonstrate Laminar reactivity
    val counter = Var(0)

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

    // Mount to DOM
    render(dom.document.getElementById("app"), app)
