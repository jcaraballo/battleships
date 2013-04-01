Battleships
==========
Joaquín Caraballo

Instructions
------------
* You need sbt. (Tried with 0.12.1). Start sbt

        $ sbt

* Start the console

        > console

* Start the game server

        scala> val server = new org.casa.battleships.ApiServer().start

* Import DSL

        scala> import org.casa.battleships.Battleships._

* Start the game

        scala> start

* The user shoots with

        scala> shoot(3, 4)

* Once done, shutdown the server with

        scala> server.stop


Additionally
------------

* The game can be restarted at any point with

        scala> start

* There is a quick mode, handy for (automated or exploratory) testing, with a 4x4 grid and only a couple of ships. To configure the game in quick mode:

        scala> configure using quickMode

* The configuration can be reset with

        scala> reset

For other options (deterministic algorithms, etc) see BattleshipsFunctionalTest.scala and Battleships.scala itself.

API
---
You can use a web service on `/game` for a multi-player game (hardcoded board at the moment)

* Start ApiServer

* Create a new game for Deb and Bob

        $ curl -X POST --data 'Deb,Bob' localhost:8080/game

* Deb can look at her dashboard

        $ curl localhost:8080/game/1/dashboard/Deb

          Bob                      Deb
          1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
          ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
        1{                   }1  1{< - - - >          }1
        2{                   }2  2{< - - >            }2
        3{                   }3  3{< - >              }3
        4{                   }4  4{< - >              }4
        5{                   }5  5{< >                }5
        6{                   }6  6{                   }6
        7{                   }7  7{                   }7
        8{                   }8  8{                   }8
        9{                   }9  9{                   }9
        0{                   }0  0{                   }0
          ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
          1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0


* And shoots

        $ curl -X POST --data 'Deb,1,5' localhost:8080/game/1/shot

        Hit,Bob


* Deb's quite happy about his game now

        $ curl localhost:8080/game/1/dashboard/Deb

          Bob                      Deb
          1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
          ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
        1{                   }1  1{< - - - >          }1
        2{                   }2  2{< - - >            }2
        3{                   }3  3{< - >              }3
        4{                   }4  4{< - >              }4
        5{*                  }5  5{< >                }5
        6{                   }6  6{                   }6
        7{                   }7  7{                   }7
        8{                   }8  8{                   }8
        9{                   }9  9{                   }9
        0{                   }0  0{                   }0
          ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
          1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0

* Bob doesn't see it that way

        $ curl localhost:8080/game/1/dashboard/Bob

          Deb                      Bob
          1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
          ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
        1{                   }1  1{< - - - >          }1
        2{                   }2  2{< - - >            }2
        3{                   }3  3{< - >              }3
        4{                   }4  4{< - >              }4
        5{                   }5  5{* >                }5
        6{                   }6  6{                   }6
        7{                   }7  7{                   }7
        8{                   }8  8{                   }8
        9{                   }9  9{                   }9
        0{                   }0  0{                   }0
          ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
          1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0

* So he decides to retaliate

        $ curl -X POST --data 'Bob,1,6' localhost:8080/game/1/shot
        Water,Deb

* But not so much

        $ curl localhost:8080/game/1/dashboard/Bob

          Deb                      Bob
          1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
          ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
        1{                   }1  1{< - - - >          }1
        2{                   }2  2{< - - >            }2
        3{                   }3  3{< - >              }3
        4{                   }4  4{< - >              }4
        5{                   }5  5{* >                }5
        6{·                  }6  6{                   }6
        7{                   }7  7{                   }7
        8{                   }8  8{                   }8
        9{                   }9  9{                   }9
        0{                   }0  0{                   }0
          ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
          1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
