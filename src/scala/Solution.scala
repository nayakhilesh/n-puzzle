package scala

import scala.Array.canBuildFrom
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

object Solution {

  val UP = 1
  val RIGHT = 2
  val DOWN = 4
  val LEFT = 3

  class GameState(val board: Array[Int], private[this] val zeroIndex: Int,
    val currDist: Int, private var sumOfManhattanDistsToGoal: Int = -1) extends Ordered[GameState] {

    if (sumOfManhattanDistsToGoal == -1)
      sumOfManhattanDistsToGoal = getSumOfManhattanDistsToGoal

    def copyAndMakeMove(move: Int): GameState = {

      val row = zeroIndex / GameState.n
      val col = zeroIndex % GameState.n

      val (targetRow, targetCol) = move match {
        case Solution.UP => (row - 1, col)
        case Solution.RIGHT => (row, col + 1)
        case Solution.DOWN => (row + 1, col)
        case Solution.LEFT => (row, col - 1)
      }

      val targetIndex = targetRow * GameState.n + targetCol
      val targetValue = board(targetIndex)

      val targetGoalRow = targetValue / GameState.n
      val targetGoalCol = targetValue % GameState.n
      val targetMove = 5 - move

      val isZeroCloser = (row > 0 && move == Solution.UP) ||
        (col > 0 && move == Solution.LEFT)
      val isTargetCloser = (targetRow - targetGoalRow > 0 && targetMove == Solution.UP) ||
        (targetRow - targetGoalRow < 0 && targetMove == Solution.DOWN) ||
        (targetCol - targetGoalCol > 0 && targetMove == Solution.LEFT) ||
        (targetCol - targetGoalCol < 0 && targetMove == Solution.RIGHT);

      val newBoard = copyAndSwapIndexes(board, zeroIndex, targetIndex)
      val newSumOfManhattanDistsToGoal = sumOfManhattanDistsToGoal +
        (if (isZeroCloser) -1 else 1) + (if (isTargetCloser) -1 else 1)

      new GameState(newBoard, targetIndex, currDist + 1, newSumOfManhattanDistsToGoal)
    }

    private[this] def getSumOfManhattanDistsToGoal: Int = {

      var sum = 0
      for ((value, index) <- board zipWithIndex) {
        sum += math.abs(value / GameState.n - index / GameState.n) + math.abs(value % GameState.n - index % GameState.n)
      }

      sum
    }

    def compare(other: GameState) =
      (this.currDist + this.sumOfManhattanDistsToGoal) - (other.currDist + other.sumOfManhattanDistsToGoal)

    private[this] def copyAndSwapIndexes(board: Array[Int], index1: Int, index2: Int): Array[Int] = {
      val newBoard = board.clone

      val temp = newBoard(index1)
      newBoard(index1) = newBoard(index2)
      newBoard(index2) = temp

      newBoard
    }

    def getCandidateMoves: Seq[Int] = {

      val listOfMoves = new ListBuffer[Int]

      val row = zeroIndex / GameState.n
      val col = zeroIndex % GameState.n
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
    val input = new Array[Int](n * n)

    (0 to n * n - 1) foreach (input.update(_, (readLine toInt)))

    val zeroIndex = input indexOf (0)
    val initialGameState = new GameState(input, zeroIndex, 0)
    val pq = new java.util.PriorityQueue[GameState]
    pq add initialGameState

    val visitedBoards = collection.mutable.Map[Array[Int], GameState]()
    visitedBoards += (initialGameState.board -> initialGameState)

    val cameFrom = collection.mutable.Map[Array[Int], (Array[Int], Int)]()
    cameFrom += (input -> null)

    // TODO identify impossible input configurations

    var currentGameState: GameState = initialGameState
    while (currentGameState != null && !isGoal(currentGameState.board)) {

      val candidateMoves = currentGameState.getCandidateMoves

      candidateMoves foreach {
        move =>
          val newGameState = currentGameState copyAndMakeMove move
          if (!visitedBoards.contains(newGameState.board)) {
            pq add newGameState
            visitedBoards += (newGameState.board -> newGameState)
            cameFrom += (newGameState.board -> (currentGameState.board, move))
          } else {
            val oldGameState = visitedBoards(newGameState.board)
            if (newGameState.currDist < oldGameState.currDist) {
              pq remove oldGameState
              pq add newGameState
              visitedBoards += (newGameState.board -> newGameState)
              cameFrom += (newGameState.board -> (currentGameState.board,
                move))
            }
          }
      }
      currentGameState = pq poll

    }

    // TODO check
    def follow(board: Array[Int]): List[Int] = board match {
      case null => List.empty[Int]
      case board => 
        val (newBoard, move) = cameFrom(board)
        follow(newBoard) :+ move
    }
    val moves = follow(currentGameState.board)
    //

    println(moves.size);

    moves foreach {
      case UP => println("UP")
      case RIGHT => println("RIGHT")
      case DOWN => println("DOWN")
      case LEFT => println("LEFT")
    }

  }

  def isGoal(board: Array[Int]): Boolean = {

    for ((value, index) <- board zipWithIndex) {
      if (value != index)
        return false
    }

    true
  }

}