import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedList
import scala.collection.immutable.Queue

object Solution {

  class GameState(val board: IndexedSeq[Int], val zeroIndex: Int, val moves: Seq[String],
    val currDist: Int) extends Ordered[GameState] {

    private val _manhattanDistToGoal = calculateManhattanDistance

    def copyAndMakeMove(move: String): GameState = {
      // TODO
      new GameState(board, zeroIndex, moves, currDist)
    }

    private[this] def calculateManhattanDistance: Int = {

      var sum = 0
      for ((value, index) <- board zipWithIndex) {
        val (goalRow, goalCol) = getRowCol(index)
        val (row, col) = getRowCol(value)
        sum += Math.abs(goalRow - row) + Math.abs(goalCol - col)
      }

      sum
    }

    def compare(other: GameState) =
      (this.currDist + this._manhattanDistToGoal) - (other.currDist + other._manhattanDistToGoal)

    private[this] def getRowCol(index: Int): (Int, Int) =
      (index / GameState._n, index % GameState._n)

  }

  object GameState {

    var _n: Int = _

  }

  def main(args: Array[String]) {

    val n = (readLine toInt)
    GameState._n = n
    val input = new ArrayBuffer[Int]

    (1 to n * n) foreach (_ => input append (readLine toInt))

    val zeroIndex = input.indexOf(0)
    val initialGameState = new GameState(input, zeroIndex, Queue.empty[String], 0)
    val q = new scala.collection.mutable.PriorityQueue[GameState]
    q += initialGameState

    var currentGameState: GameState = null
    do {

      currentGameState = q.dequeue
      val candidateMoves = getCandidateMoves(currentGameState)

      candidateMoves foreach {
        move =>
          val newGameState = currentGameState.copyAndMakeMove(move)
          q += newGameState
      }

    } while (!isGoal(currentGameState.board))

    val solution = currentGameState.moves
    println(solution.size)
    solution.foreach { println(_) }

  }

  def isGoal(board: IndexedSeq[Int]): Boolean = {

    for ((value, index) <- board zipWithIndex) {
      if (value != index)
        return false
    }

    true
  }

  def getCandidateMoves(gs: GameState): Seq[String] = {
    // TODO
    //use zeroIndex
    //also need a set of visited states
    new LinkedList[String]()
  }
}