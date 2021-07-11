package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class CodeReuseWithObject extends Simulation {
  // 1. Http conf
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  // 2. Scenario Definition
  val scn = scenario("Code Reuse")
    .exec(getAllVideoGames())
    .pause(5)
    .exec(getSpecificVideoGame())
    .pause(5)
    .exec(getAllVideoGames())

  // 3. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)


  def getAllVideoGames() = {
    repeat(3) {
      exec(http("1. Get all video games - 1st call")
        .get("videogames")
        .check(status.is(200)))
    }
  }

  def getSpecificVideoGame() = {
    repeat(5) {
      exec(http("2. Get specific game - 2nd call")
        .get("videogames/1")
        .check(status.in(200 to 210)))
    }
  }

}
