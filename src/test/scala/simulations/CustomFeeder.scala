package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random

class CustomFeeder extends Simulation {

  // 1. Set Http conf, Mockdata
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
    .proxy(Proxy("localhost",8866))

  var idNumbers = (11 to 20).iterator
  var idNumbersDelete =  (11 to 20).iterator
  val rnd = new Random()
  val now = LocalDate.now()
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate, random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val deleteIdFeeder = Iterator.continually(
    Map(
      "gameId" -> idNumbersDelete.next()
    )
  )


  val customFeeder = Iterator.continually(
    Map(
      "gameId" -> idNumbers.next(),
      "name" -> ("Game-" + randomString(5)),
      "releaseDate" -> getRandomDate(now, rnd),
      "reviewScore" -> rnd.nextInt(100),
      "category" -> ("Category-" + randomString(6)),
      "rating" -> ("Rating-" + randomString(4))
    )
  )

  //
  //  def postNewGame() = {
  //    repeat(5) {
  //      feed(customFeeder)
  //        .exec(http("Post New Game")
  //          .post("videogames/")
  //          .body(
  //            StringBody(
  //              "{" +
  //                "\"id\": ${gameId}," +
  //                "\"name\": \"${name}\"," +
  //                "\"releaseDate\": \"${releaseDate}\"," +
  //                "\"reviewScore\": ${reviewScore}," +
  //                "\"category\": \"${category}\"," +
  //                "\"rating\": \"${rating}\"" +
  //                "}"
  //            )
  //          ).asJson
  //          .check(status.is(200)))
  //        .pause(1)
  //    }
  //  }


  def postNewGame() = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("Post New Game")
          .post("videogames/")
          .body(ElFileBody("bodies/NewGameTemplate.json")).asJson
          .check(status.is(200)))
        .pause(1)
    }
  }


  def deleteGame() = {
    repeat(10) {
      feed(deleteIdFeeder)
        .exec(http("Delete Game")
          .delete("videogames/${gameId}")
          .check(status.is(200))
        )
        .pause(1)
    }
  }

  // 2. Scenario Definition
  var scn = scenario("Post New Game")
    .exec(deleteGame())
    .pause(5)
    .exec(postNewGame())

  // 3. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
