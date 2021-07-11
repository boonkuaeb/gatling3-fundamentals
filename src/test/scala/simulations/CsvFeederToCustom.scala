package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeederToCustom extends Simulation {
  // 1. Set Http conf, Mockdata
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")
    .proxy(Proxy("localhost",8866))

  var idNumber = {
    1 to 10
  }.iterator

  val customFeeder = Iterator.continually(
    Map(
      "gameId" -> idNumber.next()
    )
  )

  // 2. Scenario Definition
  val scn = scenario("Csv Feeder test")
    .exec(getSpecificVideoGame())

  // 3. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

  // ######################################

  def getSpecificVideoGame() = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("Get Specific video game")
          .get("videogames/${gameId}")
          .check(status.is(200))
        )
        .pause(1)
    }
  }
}
