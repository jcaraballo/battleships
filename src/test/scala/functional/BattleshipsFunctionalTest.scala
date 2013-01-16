package functional

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.Battleships._

class BattleshipsFunctionalTest extends JUnitSuite {
  @Test def plays() {
    reset
    configure using deterministicChooser
    configure using deterministicShooter

    expectResult("""
==========
New Game
==========
  Computer                 You
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
1{                   }1  1{< - - - > < - - > ^}1
2{                   }2  2{< - > < >         |}2
3{                   }3  3{                  v}3
4{                   }4  4{                   }4
5{                   }5  5{                   }5
6{                   }6  6{                   }6
7{                   }7  7{                   }7
8{                   }8  8{                   }8
9{                   }9  9{                   }9
0{                   }0  0{                   }0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0

Enter your move:
"""){trimOnTheRight(start)}

    expectResult("""
User: (1, 1) => Hit
Computer: (1, 1) => Hit

  Computer                 You
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
1{*                  }1  1{* - - - > < - - > ^}1
2{                   }2  2{< - > < >         |}2
3{                   }3  3{                  v}3
4{                   }4  4{                   }4
5{                   }5  5{                   }5
6{                   }6  6{                   }6
7{                   }7  7{                   }7
8{                   }8  8{                   }8
9{                   }9  9{                   }9
0{                   }0  0{                   }0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0

Enter your move:
"""){trimOnTheRight(shoot(1, 1))}

    expectResult("""
User: (2, 1) => Hit
Computer: (1, 1) => Hit

  Computer                 You
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
1{* *                }1  1{* - - - > < - - > ^}1
2{                   }2  2{< - > < >         |}2
3{                   }3  3{                  v}3
4{                   }4  4{                   }4
5{                   }5  5{                   }5
6{                   }6  6{                   }6
7{                   }7  7{                   }7
8{                   }8  8{                   }8
9{                   }9  9{                   }9
0{                   }0  0{                   }0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0

Enter your move:
"""){trimOnTheRight(shoot(2, 1))}

    shoot(3, 1)
    shoot(4, 1)

    expectResult("""
User: (5, 1) => Sunk
Computer: (1, 1) => Hit

  Computer                 You
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
1{* * * * *          }1  1{* - - - > < - - > ^}1
2{                   }2  2{< - > < >         |}2
3{                   }3  3{                  v}3
4{                   }4  4{                   }4
5{                   }5  5{                   }5
6{                   }6  6{                   }6
7{                   }7  7{                   }7
8{                   }8  8{                   }8
9{                   }9  9{                   }9
0{                   }0  0{                   }0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0

Enter your move:
"""){trimOnTheRight(shoot(5, 1))}

    expectResult("""
User: (10, 10) => Water
Computer: (1, 1) => Hit

  Computer                 You
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
1{* * * * *          }1  1{* - - - > < - - > ^}1
2{                   }2  2{< - > < >         |}2
3{                   }3  3{                  v}3
4{                   }4  4{                   }4
5{                   }5  5{                   }5
6{                   }6  6{                   }6
7{                   }7  7{                   }7
8{                   }8  8{                   }8
9{                   }9  9{                   }9
0{                  ·}0  0{                   }0
  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~
  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0

Enter your move:
"""){trimOnTheRight(shoot(10, 10))}
  }

  @Test def playsQuickMode(){
    reset
    configure using quickMode
    configure using deterministicChooser
    configure using deterministicShooter

    expectResult("""
==========
New Game
==========
  Computer     You
  1 2 3 4      1 2 3 4
  ~~~~~~~      ~~~~~~~
1{       }1  1{< - > ^}1
2{       }2  2{      v}2
3{       }3  3{       }3
4{       }4  4{       }4
  ~~~~~~~      ~~~~~~~
  1 2 3 4      1 2 3 4

Enter your move:
"""){trimOnTheRight(start)}

    expectResult("""
User: (2, 2) => Water
Computer: (1, 1) => Hit

  Computer     You
  1 2 3 4      1 2 3 4
  ~~~~~~~      ~~~~~~~
1{       }1  1{* - > ^}1
2{  ·    }2  2{      v}2
3{       }3  3{       }3
4{       }4  4{       }4
  ~~~~~~~      ~~~~~~~
  1 2 3 4      1 2 3 4

Enter your move:
"""){trimOnTheRight(shoot(2, 2))}

    expectResult("""
User: (4, 2) => Hit
Computer: (1, 1) => Hit

  Computer     You
  1 2 3 4      1 2 3 4
  ~~~~~~~      ~~~~~~~
1{       }1  1{* - > ^}1
2{  ·   *}2  2{      v}2
3{       }3  3{       }3
4{       }4  4{       }4
  ~~~~~~~      ~~~~~~~
  1 2 3 4      1 2 3 4

Enter your move:
"""){trimOnTheRight(shoot(4, 2))}

    shoot(4, 1)
    shoot(3, 1)
    shoot(2, 1)

    expectResult("""
User: (1, 1) => Sunk
Computer: (1, 1) => Hit

  Computer     You
  1 2 3 4      1 2 3 4
  ~~~~~~~      ~~~~~~~
1{* * * *}1  1{* - > ^}1
2{  ·   *}2  2{      v}2
3{       }3  3{       }3
4{       }4  4{       }4
  ~~~~~~~      ~~~~~~~
  1 2 3 4      1 2 3 4

You win!

==========
New Game
==========
  Computer     You
  1 2 3 4      1 2 3 4
  ~~~~~~~      ~~~~~~~
1{       }1  1{< - > ^}1
2{       }2  2{      v}2
3{       }3  3{       }3
4{       }4  4{       }4
  ~~~~~~~      ~~~~~~~
  1 2 3 4      1 2 3 4

Enter your move:
"""){trimOnTheRight(shoot(1, 1))}
  }

  private def trimOnTheRight(string: String) = string.split('\n').map(_.reverse.dropWhile(_ == ' ').reverse).toList.mkString("\n")+'\n'
}