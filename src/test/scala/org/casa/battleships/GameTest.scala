package org.casa.battleships

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.Game._
class GameTest extends JUnitSuite {
  @Test def plays() {
    reset
    configure using deterministicChooser
    configure using deterministicShooter

    expect("""
==========
New Game
==========
Comp   You
___________  ___________
 1234567890   1234567890
1            1<---><-->^
2            2<-><>····|
3            3·········v
4            4··········
5            5··········
6            6··········
7            7··········
8            8··········
9            9··········
0            0··········
___________  ___________

Enter your move:
"""){start}

    expect("""
User: (1, 1) => hit
Computer: (1, 1) => hit

Comp   You
___________  ___________
 1234567890   1234567890
1*           1*---><-->^
2            2<-><>····|
3            3·········v
4            4··········
5            5··········
6            6··········
7            7··········
8            8··········
9            9··········
0            0··········
___________  ___________

Enter your move:
"""){shoot(1, 1)}

    expect("""
User: (2, 1) => hit
Computer: (1, 1) => hit

Comp   You
___________  ___________
 1234567890   1234567890
1**          1*---><-->^
2            2<-><>····|
3            3·········v
4            4··········
5            5··········
6            6··········
7            7··········
8            8··········
9            9··········
0            0··········
___________  ___________

Enter your move:
"""){shoot(2, 1)}

    shoot(3, 1)
    shoot(4, 1)

    expect("""
User: (5, 1) => sunk
Computer: (1, 1) => hit

Comp   You
___________  ___________
 1234567890   1234567890
1*****       1*---><-->^
2            2<-><>····|
3            3·········v
4            4··········
5            5··········
6            6··········
7            7··········
8            8··········
9            9··········
0            0··········
___________  ___________

Enter your move:
"""){shoot(5, 1)}

    expect("""
User: (10, 10) => water
Computer: (1, 1) => hit

Comp   You
___________  ___________
 1234567890   1234567890
1*****       1*---><-->^
2            2<-><>····|
3            3·········v
4            4··········
5            5··········
6            6··········
7            7··········
8            8··········
9            9··········
0         ·  0··········
___________  ___________

Enter your move:
"""){shoot(10, 10)}
  }

  @Test def playsQuickMode(){
    reset
    configure using quickMode
    configure using deterministicChooser
    configure using deterministicShooter

    expect("""
==========
New Game
==========
Comp   You
_____  _____
 1234   1234
1      1<->^
2      2···v
3      3····
4      4····
_____  _____

Enter your move:
"""){start}

    expect("""
User: (2, 2) => water
Computer: (1, 1) => hit

Comp   You
_____  _____
 1234   1234
1      1*->^
2 ·    2···v
3      3····
4      4····
_____  _____

Enter your move:
"""){shoot(2, 2)}

    expect("""
User: (4, 2) => hit
Computer: (1, 1) => hit

Comp   You
_____  _____
 1234   1234
1      1*->^
2 · *  2···v
3      3····
4      4····
_____  _____

Enter your move:
"""){shoot(4, 2)}

    shoot(4, 1)
    shoot(3, 1)
    shoot(2, 1)

    expect("""
User: (1, 1) => sunk
Computer: (1, 1) => hit

Comp   You
_____  _____
 1234   1234
1****  1*->^
2 · *  2···v
3      3····
4      4····
_____  _____

You win!

==========
New Game
==========
Comp   You
_____  _____
 1234   1234
1      1<->^
2      2···v
3      3····
4      4····
_____  _____

Enter your move:
"""){shoot(1, 1)}
  }
}