package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation {

  // 1. Http conf
  val httpConf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept", "application/json")


  // 2. Scenario Definition
  val scn = scenario("Check JSON path")
    .exec(http("Get specify game No. 1")
      .get("videogames/1")
      .check(jsonPath("$.name").is("Resident Evil 4"))
    )

    .exec(http("Get all video games")
      .get("videogames")
      .check(jsonPath("$[1].id").saveAs("gameId"))
    )
    // Debug here
    .exec{ session=> println(session);session}

    .exec(http("Get Specific game")
      .get("videogames/${gameId}")
      .check(jsonPath("$.name").is("Gran Turismo 3"))
      .check(bodyString.saveAs("responseBody")))

    // Debug here
    .exec{ session => println(session("responseBody").as[String]); session}

  // 3. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
