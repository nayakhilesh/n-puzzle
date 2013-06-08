package scala

import scala.collection.immutable.Queue
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

object Solution {

  val UP = "UP"
  val RIGHT = "RIGHT"
  val DOWN = "DOWN"
  val LEFT = "LEFT"

  class GameState(val board: Seq[Int], private[this] val zeroIndex: Int, val moves: Seq[String],
    val currDist: Int, private var sumOfManhattanDistsToGoal: Int = -1) extends Ordered[GameState] {

    if (sumOfManhattanDistsToGoal == -1)
      sumOfManhattanDistsToGoal = getSumOfManhattanDistsToGoal

    def copyAndMakeMove(move: String): GameState = {

      val (row, col) = getRowCol(zeroIndex)

      val (targetRow, targetCol) = move match {
        case Solution.UP => (row - 1, col)
        case Solution.RIGHT => (row, col + 1)
        case Solution.DOWN => (row + 1, col)
        case Solution.LEFT => (row, col - 1)
      }

      val targetIndex = getIndex(targetRow, targetCol)
      val targetValue = board(targetIndex)

      val (targetGoalRow, targetGoalCol) = getRowCol(targetValue)
      val targetMove = getOppMove(move)

      val isZeroCloser = (row > 0 && move == Solution.UP) ||
        (col > 0 && move == Solution.LEFT)
      val isTargetCloser = (targetRow - targetGoalRow > 0 && targetMove == Solution.UP) ||
        (targetRow - targetGoalRow < 0 && targetMove == Solution.DOWN) ||
        (targetCol - targetGoalCol > 0 && targetMove == Solution.LEFT) ||
        (targetCol - targetGoalCol < 0 && targetMove == Solution.RIGHT);

      val newBoard = copyAndSwapIndexes(board, zeroIndex, targetIndex)
      val newZeroIndex = targetIndex
      val newMoves = moves :+ move
      val newCurrDist = currDist + 1
      val newSumOfManhattanDistsToGoal = sumOfManhattanDistsToGoal +
        (if (isZeroCloser) -1 else 1) + (if (isTargetCloser) -1 else 1)

      new GameState(newBoard, newZeroIndex, newMoves, newCurrDist, newSumOfManhattanDistsToGoal)
    }

    private[this] def getSumOfManhattanDistsToGoal: Int = {

      var sum = 0
      for ((value, index) <- board zipWithIndex) {
        sum += getManhattanDistToGoal(value, index)
      }

      sum
    }

    private[this] def getManhattanDistToGoal(value: Int, index: Int): Int = {
      val (goalRow, goalCol) = getRowCol(value)
      val (row, col) = getRowCol(index)
      math.abs(goalRow - row) + math.abs(goalCol - col)
    }

    def compare(other: GameState) =
      (this.currDist + this.sumOfManhattanDistsToGoal) - (other.currDist + other.sumOfManhattanDistsToGoal)

    private[this] def getRowCol(index: Int): (Int, Int) =
      (index / GameState.n, index % GameState.n)

    private[this] def copyAndSwapIndexes(board: Seq[Int], index1: Int, index2: Int): Seq[Int] = {
      val newBoard = new ArrayBuffer[Int]

      board foreach { newBoard append _ }
      val temp = newBoard(index1)
      newBoard(index1) = newBoard(index2)
      newBoard(index2) = temp

      newBoard
    }

    private[this] def getOppMove(move: String): String = {
      move match {
        case Solution.UP => Solution.DOWN
        case Solution.RIGHT => Solution.LEFT
        case Solution.DOWN => Solution.UP
        case Solution.LEFT => Solution.RIGHT
      }
    }

    private[this] def getIndex(row: Int, col: Int): Int = {
      (row * 3) + col
    }

    def getCandidateMoves: Seq[String] = {

      val listOfMoves = new ListBuffer[String]()

      val (row, col) = getRowCol(zeroIndex)
      if (row != 0)
        listOfMoves += Solution.UP
      if (row != GameState.n - 1)
        listOfMoves += Solution.DOWN
      if (col != 0)
        listOfMoves += Solution.LEFT
      if (col != GameState.n - 1)
        listOfMoves += Solution.RIGHT

      listOfMoves.toSeq
    }

  }

  object GameState {

    var n: Int = _

  }

  def main(args: Array[String]) {

    val n = (readLine toInt)
    GameState.n = n
    val input = new ArrayBuffer[Int]

    (1 to n * n) foreach (_ => input append (readLine toInt))

    val zeroIndex = input indexOf (0)
    val initialGameState = new GameState(input, zeroIndex, Queue.empty[String], 0)
    val pq = new java.util.PriorityQueue[GameState]
    pq add initialGameState

    val visitedBoards = collection.mutable.Map[Seq[Int], GameState]()
    visitedBoards += (initialGameState.board -> initialGameState)

    // TODO identify impossible input configurations

    var currentGameState: GameState = null
    do {

      currentGameState = pq poll
      val candidateMoves = currentGameState.getCandidateMoves

      candidateMoves foreach {
        move =>
          val newGameState = currentGameState copyAndMakeMove move
          if (!visitedBoards.contains(newGameState.board)) {
            pq add newGameState
            visitedBoards += (newGameState.board -> newGameState)
          }
          else {
            val oldGameState = visitedBoards(newGameState.board)
            if (newGameState.currDist < oldGameState.currDist) {
              pq remove oldGameState
              pq add newGameState
              visitedBoards += (newGameState.board -> newGameState)
            }
          }
      }

    } while (!isGoal(currentGameState.board))

    val solution = currentGameState.moves
    println(solution.size)
    solution foreach { println(_) }

  }

  def isGoal(board: Seq[Int]): Boolean = {

    for ((value, index) <- board zipWithIndex) {
      if (value != index)
        return false
    }

    true
  }

}