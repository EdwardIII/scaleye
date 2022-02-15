import sttp.client3._
import java.net.URI
import sttp.model.Uri
import scala.util.{Try,Success,Failure}
import scala.util.Failure
import java.util.Calendar
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit.SECONDS
import java.time.temporal.ChronoUnit

@main def report: Unit = {
  val start = java.time.LocalDateTime.now

  // TODO: On first run, if no file in resources/, create and warn
  // TODO: If has failures, return different exit code

  val urls = scala.io.Source.fromResource( "urls.txt" ).getLines

  // So in the real world, I would probably use Akka HTTP or similar,
  // but wanted to try out some of the Scala concurrency features first-hand
  val futures = urls.map(url => Future { verify(url) } )
  val results = Await.result(Future.sequence(futures), Duration(50, SECONDS))
  println(results.mkString(", "))


  val end = java.time.LocalDateTime.now
  println(start.until(end, ChronoUnit.SECONDS))

}

def errorText(url: String, reason: String) = s"${url}: ${reason} - Failure"

def verify(url: String): String = {
  sttp.model.Uri.parse(url) match {
      case Left(reason) => errorText(url, reason) // failed due to a uri parse error
      case Right(uri) => responseToStatus(uri)
    }
  }

def responseToStatus(url: sttp.model.Uri): String = {
  tryRequest(url) match {
    case Failure(failure) => errorText(url.toString(), failure.getMessage())
    case Success(response) => if(response.code.isSuccess) s"${response.request.uri} - OK" else errorText(url.toString(), response.statusText)
    }
  }

def tryRequest(url: sttp.model.Uri) = Try {
  val backend = HttpURLConnectionBackend()
  basicRequest.get(url).send(backend)
}
