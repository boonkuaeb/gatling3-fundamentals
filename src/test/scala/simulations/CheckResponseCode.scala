package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration.DurationInt

class CheckResponseCode extends Simulation {

  // 1. Http conf
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")

  // 2. Scenario Definition
  val scn = scenario("Scenario : Get Video Game DB - 3 calls")

    // Call 1st time and waiting for 5 seconds
    .exec(http("1. Get all video games - 1st call")
      .get("videogames")
      .check(status.is(200)))
    .pause(100.milliseconds)

    // Call 2nd time and Random waiting 1-20 seconds
    .exec(http("2. Get specific game - 2nd call")
      .get("videogames/1")
      .check(status.in(200 to 201)))
    .pause(100.milliseconds, 800.milliseconds) // random pause time

    // Call 3rd time and waiting 3000.milliseconds
    .exec(http("3. Get all video games - 3rd call")
      .get("videogames")
      .check(status.not(404), status.not(500)))
    .pause(100.milliseconds)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
