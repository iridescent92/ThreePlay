package gamesuite;

/**
 * @author Brendon Murthum
 */

/*
 *  Internal Code:
 *  	"UPDATE ME" is at the spots for future updates
 *  Terms:
 *  	Board - The 9x9 complete array
 * 		Box - A 3x3 chunk of squares
 * 		Square - A single, changeable, smallest unit
 * 
 * 		completeBoard - The full correct board
 * 		initialBoard - The board first given to the player
 * 		currentBoard - The board as the game continues (load to this)
 * 		errorsBoard - The errors on the board as the game continues
 * 	User Solving Methods:
 * 		Single Candidate method - to look to a spot to see if only
 * 			option exists for that spot
 * 		Single Position method - to look to a row, col, or box, to
 * 			see that a number within it only exists at a spot
 * 	Difficulties:
 * 		Easy - Using Single Candidate and Single Position to solve
 * 			~30 squares of the board
 * 		Medium - Using the former methods to solve 50 squares of
 * 			the board
 * 		Hard - TO ADD... using more methods
 * 	Credit:
 * 		This site describes the techniques used to solve a board.
 * 		http://www.sudokuoftheday.com/techniques/
 *	To-Do:
 *		Transfer code to object-oriented focus
 *		Write in the "Single Position" method of solving
 *			- Allows for more complex puzzles
 *			- More spaces opened on the board as well
 */

/** 
 * Logic for the game of Sudoku. Model allows users to click squares,
 * on the board, controls input into the game, saves the game, loads the
 * game, and determines when the game is over. The model is called
 * from the controller.
 * @author Brendon Murthum
 */
public class SudokuLogic implements IGameLogic {
	
	/** 
	 * Current board status of the game. Stores the values of 
	 *  the squares in each square as an object.
	 */
	private SudokuPiece[][] board;
	
	/** Used in IGameLogic, though not in Sudoku. */
	@SuppressWarnings("unused")
	private Player player;
	
	/** Stores the finished 'goal' board to be achieved. */
	private int[][] completeBoard = new int[9][9];
	
	/** Stores the games initial board. These squares are unchangeable. */
	private int[][] initialBoard = new int[9][9];
	
	/** Stores the current board. Used in initializing the board. */
	private int[][] currentBoard = new int[9][9];
	
	/** Stores booleans of where the errors in the board are. */
	private boolean[][] errorsBoard = new boolean[9][9];
	
	/** This value is true when the game is complete. */
	@SuppressWarnings("unused")
	private boolean gameComplete;
	
	/** 
	 * The width and hight of the grid of squares in the game. 
	 * Should be 9. 
	 */
	private int size;
	
	/** 
	 *  The current value 0-8 of the row and column clicked by the user.
	 *  Used to communicate between the GUI and the arrays 
	 *  containing square-values.
	 */
	private int clickedX, clickedY;
	
	/** 
	 * This is the number of squares to remove from the initial complete 
	 *  board to have the user solve.
	 */
	private int removeThisMany = 10;
	
	/** 
	 *  Initializes completeBoard, initialBoard, 
	 *  currentBoard and errorsBoard.
	 *  */
	public SudokuLogic() {
		this.initializeGame();
		this.gameComplete = false;
		this.size = 9;
	}
	
	/**
	 * This method checks the width/height of the board.
	 * @return - Returns INT of the width/height of the Sudoku board.
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Used to communicate between the IPiece array of 
	 * squares with information.
	 * @param row - The 'Y' value. The 0-8 rows down from the top
	 * 				of the board.
	 * @param col - The 'X' value. The 0-8 cols right from the left
	 * 				edge of the board.
	 * @return - Returns INT of the value contained in the Square
	 * 			 of the object at [row][col].
	 */
	public int getNumber(final int row, final int col) {
		//return currentBoard[row][col];
		return this.board[row][col].getNum();
	}
	
	/**
	 *  Used to communicate to the GUI which backgrounds of JPanels
	 *  should be grey to indicate what is not available to change.
	 * @param row - The 'Y' value. The 0-8 rows down from the top
	 * 				of the board.
	 * @param col - The 'X' value. The 0-8 cols right from the left
	 * 				edge of the board.
	 * @return - Returns TRUE if the square at [row][col] is an 
	 * 			 initial square.
	 */
	public boolean isInitial(final int row, final int col) {
		if (initialBoard[row][col] != 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Changes the selected pieces number.
	 * @param num number to change selected piece to.
	 */
	public void setNumber(final int num) {
		board[clickedY][clickedX].setNum(num);
		if (board[clickedY][clickedX].getNum() 
				!= completeBoard[clickedY][clickedX]) {
			errorsBoard[clickedY][clickedX] = true;
		} else {
			errorsBoard[clickedY][clickedX] = false;
		}
	}
	
	/**
	 * Used at game completion to return which squares 
	 * filled in incorrectly.
	 * @param row - The 'Y' value. The 0-8 rows down from the top
	 * 				edge of the board.
	 * @param col - The 'X' value. The 0-8 cols right from the left
	 * 				edge of the board.
	 * @return Returns TRUE if square at [row][col] is in error.
	 */
	public boolean isError(final int row, final int col) {
		return errorsBoard[row][col];
	}

	/**
	 *  This initializes the game board. (1) Sets the IPieces board,
	 *  then, (2) generates the complete board, (3) generates the 
	 *  initial board, (4) generates the errors board, (5) generates
	 *  the current board and the associated IPiece-current board.
	 */
	private void initializeGame() {
		this.board = new SudokuPiece[9][9];
		this.completeBoard = generateBoard();
		this.initialBoard = generateInitialBoard(removeThisMany);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.errorsBoard[i][j] = false;
				this.currentBoard[i][j] = initialBoard[i][j];
				board[i][j] 
					= new SudokuPiece(currentBoard[i][j]);
			}
		}
	}
	
	/** Generates the initialBoard */
	/**
	 * Generates the initial board.
	 * @param removeQuantity - The number of squares to remove from
	 * 						   the total 81 squares.
	 * @return - Returns an array of integers to be given to the
	 * 			 current board.
	 */
	public int[][] generateInitialBoard(final int removeQuantity) {
		int[][] atempBoard = new int[9][9];
		// Set all the values of atempBoard == array
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				atempBoard[i][j] = this.completeBoard[i][j];
			}
		}
		int repeatCount = 0;
		int removedCount = 0;
		int rowRandom;
		int colRandom;
		int numberHolder;
		int counter;
		boolean shouldLoop;
		boolean shouldRepeat = false;
		
		while (removedCount < removeQuantity) {
			// Starts again if comes to dead end
			if (shouldRepeat) {
				shouldRepeat = false;
				removedCount = 0;
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						atempBoard[i][j] 
						  = this.completeBoard[i][j];
					}
				}
				repeatCount = repeatCount + 1;
			}
			
			/* 
			 * Remove a square. Hold onto that removed number
			 * until it's checked to still be solvable
			 */
			counter = 0;
			shouldLoop = true;
			while (shouldLoop) {
				rowRandom = (int) (Math.random() * 9);
				colRandom = (int) (Math.random() * 9);
				numberHolder = atempBoard[rowRandom][colRandom];
				if (atempBoard[rowRandom][colRandom] != 0) {
					atempBoard[rowRandom][colRandom] = 0;
					if (solveBoard(atempBoard)) {
						removedCount = removedCount + 1;
						shouldLoop = false;
					} else {
			        atempBoard[rowRandom][colRandom] = numberHolder;
						counter = counter + 1;
						if (counter > 90) {
							shouldRepeat = true;
							shouldLoop = false;
							if (repeatCount > 10) {
							  return atempBoard;
							}
						}
					}
				}
			}			
		}
		// We're here once the removeQuantity is met
		if (!solveBoard(atempBoard)) {
			System.out.println(
			"ERROR IN INTERNAL GENERATING MECHANIC!");
		}
		return atempBoard;
	}
	
	/**
	 * For testing. This allows for checking if the initial
	 * board is solvable, from the GUI and testing class.
	 * @return - Returns TRUE if the initial board is solvable.
	 *  		 Returns FALSE if the initial board is not.
	 */
	public boolean isSolvable() {
		return solveBoard(initialBoard);
	}
	
	/**
	 * Solves the given board. Used to check if a generated
	 * board is still solvable through the pre-programmed
	 * methods.
	 * @param array - An incomplete INT array board.
	 * @return - Returns TRUE if the board is solvable at
	 * 			 its current state.
	 */
	public static boolean solveBoard(final int[][] array) {
		int[][] tempBoard = new int[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				tempBoard[i][j] = array[i][j];
			}
		}
		// This will improve as more methods of 
		// solving come into play
		boolean solved = false;
		boolean madeProgress = false;
		while (!solved) {
			// To make sure progress is being made
			madeProgress = false;
			// Try entire board for Single Candidate solutions
			for (int i = 0; i < 9; i++) {
			 for (int j = 0; j < 9; j++) {
				// If square is empty
			  if (tempBoard[i][j] == 0) {
			   // If solvable square, solve it
			   if (solvedBySingleCandidate(i, j, tempBoard) != 0) {
					tempBoard[i][j] 
				= solvedBySingleCandidate(i, j, tempBoard);
				madeProgress = true;
			   }
		      }
			 }
			}
			
			// UPDATE ME: Add a future solving method HERE
			
			// Check if solved 
			solved = true;
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (tempBoard[i][j] == 0) {
						solved = false;
					}
				}
			}
			if (solved) {
				return true;
			}
			if (!madeProgress) {
				return false;
			}
		}
		// This is a catch-all and should not be seen
		System.out.println("ERROR IN INTERNAL SOLVING MECHANIC!");	
		return false;
	}
	
	/**
	 * Used when user clicks on a square. For communication between
	 * the GUI, game logic, and the square IPieces.
	 * @param row - The 'Y' value. The 0-8 rows down from the top
	 * 				edge of the board.
	 * @param col - The 'X' value. The 0-8 rows right from the left
	 * 				edge of the board.
	 */
	public void clickedOn(final int row, final int col) {
		clickedX = col;
		clickedY = row;
	}
	
	/**
	 * Communicates to the GUI which square is currently selected.
	 * @return - Returns INT 0-8 of the cols right from the left
	 * 			 edge of the board.
	 */
	public int currentClickedX() {
		return clickedX;
	}
	
	/**
	 * Communicates to the GUI which square is currently selected.
	 * @return - Returns INT 0-8 of the rows down from the top
	 * 			 edge of the board.
	 */
	public int currentClickedY() {
		return clickedY;
	}
	
	/**
	 * Used in generation of the initial board, in the method of
	 * making sure the board is solvable by Single Candidate Method.
	 * @param array - Array[9] that contains possibilities.
	 * @param num - Number to check if inside of those possibilites.
	 * @return - Returns TRUE if the array does contain that num.
	 */
	public static boolean arrayContains(final int[] array, final int num) {
		for (int i = 0; i < 9; i++) {
			if (array[i] == num) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * Removes a number from the array of possibilities.
	 * @param array - Array[9] that contains possibilities.
	 * @param num - The number to take away from that array.
	 */
	public static void scArrayRemove(final int[] array, final int num) {
		// No need to check for emptiness
		if (num == 0) {
			return;
		}
		// Remove the num from the array
		for (int i = 0; i < 9; i++)	{
			if (array[i] == num) {
				array[i] = 0;
				return;
			}
		}
	}
	
	/**
	 * Method to see if a specific square is solvable by the
	 * Single Candidate Method.
	 * @param row - The 'Y' value. The 0-8 rows down from the top
	 * 				edge of the board.
	 * @param col - The 'X' value. The 0-8 rows right from the left
	 * 				edge of the board.
	 * @param array - The board INT array to check.
	 * @return - Returns 0, if the square is not currently
	 * 			 solvable. Returns #, if the square is solvable 
	 * 			 to some single INT.
	 */
	public static int solvedBySingleCandidate(
			final int row, final int col, final int[][] array) {
		
		// Start with all possibility
		int[] possible = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		// If square is filled, return true
		if (array[row][col] != 0) {
			return array[row][col];
		}
		// Eliminates all numbers in that row from possibility
		for (int i = 0; i < 9; i++) {
			if (arrayContains(possible, array[row][i])) {
				scArrayRemove(possible, array[row][i]);
			}
		}
		// Eliminate all numbers in that column from possibility
		for (int i = 0; i < 9; i++) {
			if (arrayContains(possible, array[i][col])) {
				scArrayRemove(possible, array[i][col]);
			}
		}
		// Eliminate all numbers 
		// in that box from possibility			
		for (int i = 0; i < 3; i++) {
		 for (int j = 0; j < 3; j++) {
		  if (arrayContains(possible, 
			  array[(((row / 3) * 3) + i)]
			  [(((col / 3) * 3) + j)])) {
			scArrayRemove(possible, array[(((row / 3) * 3) + i)]
			[(((col / 3) * 3) + j)]);
		  }
		 }
		}
		// See if there are multiple possibilities left in this square
		int stillPossible = 0;
		int lastRemaining = 0;
		for (int i = 0; i < 9; i++) {
			if (possible[i] != 0) {
				stillPossible = stillPossible + 1;
				lastRemaining = possible[i];
			}
			if (stillPossible > 1) {
				return 0;
			}
		}
		return lastRemaining;
	}
	
	/**
	 * Outputs a board to the console. Used for testing.
	 * @param array - Which board INT array to output.
	 */
	public static void outputToConsole(final int[][] array) {
		int aCounter = 0;
		int bCounter = 0;
		for (int i = 0; i < 9; i++) {
			bCounter = bCounter + 1;
			for (int j = 0; j < 9; j++) {
				aCounter = aCounter + 1;
				if (array[i][j] == 0) {
					System.out.print("-");
				} else {
					System.out.print(array[i][j]);
				}
				if (aCounter == 3) {
					System.out.print(" ");
					aCounter = 0;
				}
			}
			System.out.print("\n");
			if (bCounter == 3) {
				System.out.print("\n");
				bCounter = 0;
			}
		}	
	}
	
	/**
	 * Swaps chunks in the original board. For initializing the board.
	 * @param someBoard - An array board in the initializing process.
	 * @return - An array board now with possibly swapped chunks.
	 */
	private static int[][] swapChunks(final int[][] someBoard) {
		int[][] ctempBoard = new int[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				ctempBoard[i][j] = someBoard[i][j];
			}
		}
		
		int decidingRandom, holdingNumber;
	//  Swap two horizontal 3-tall chunks
		decidingRandom = (int) (Math.random() * 4); // 0, 1, 2, 3
		if (decidingRandom == 0) {
			for (int h = 0; h < 3; h++) {
				for (int i = 0; i < 9; i++) {
					holdingNumber = ctempBoard[h][i];
					ctempBoard[h][i] 
					 = ctempBoard[h + 3][i];
					ctempBoard[h + 3][i] 
					 = holdingNumber;
				}
			}
		}
		if (decidingRandom == 1) {
			for (int h = 0; h < 3; h++) {
				for (int i = 0; i < 9; i++) {
					holdingNumber = ctempBoard[h][i];
					ctempBoard[h][i] = ctempBoard[h + 6][i];
					ctempBoard[h + 6][i] = holdingNumber;
				}
			}
		}
		if (decidingRandom == 2) {
			for (int h = 3; h < 6; h++) {
				for (int i = 0; i < 9; i++) {
					holdingNumber = ctempBoard[h][i];
					ctempBoard[h][i] = ctempBoard[h + 3][i];
					ctempBoard[h + 3][i] = holdingNumber;
				}
			}
		}
		decidingRandom = (int) (Math.random() * 4); // 0, 1, 2, 3
		if (decidingRandom == 0) {
			for (int h = 0; h < 3; h++) {
				for (int i = 0; i < 9; i++) {
					holdingNumber = ctempBoard[i][h];
					ctempBoard[i][h] = ctempBoard[i][h + 3];
					ctempBoard[i][h + 3] = holdingNumber;
				}
			}
		}
		if (decidingRandom == 1) {
			for (int h = 0; h < 3; h++) {
				for (int i = 0; i < 9; i++) {
					holdingNumber = ctempBoard[i][h];
					ctempBoard[i][h] = ctempBoard[i][h + 6];
					ctempBoard[i][h + 6] = holdingNumber;
				}
			}
		}
		if (decidingRandom == 2) {
			for (int h = 3; h < 6; h++) {
				for (int i = 0; i < 9; i++) {
					holdingNumber = ctempBoard[i][h];
					ctempBoard[i][h] = ctempBoard[i][h + 3];
					ctempBoard[i][h + 3] = holdingNumber;
				}
			}
		}
		
		return ctempBoard;
	}
	
	/**
	 * Generates a rendomized complete board from
	 * the beginning 'key' board.
	 * @return - Returns an INT array of a randomized board.
	 */
	public static int[][] generateBoard() {
		int[][] btempBoard = new int[9][9];
		btempBoard = outputBeginningBoard();
		int holdingNumber;
		int decidingRandom;
		/* Swap some random rows */
		decidingRandom = (int) (Math.random() * 2); // 0, 1, 2
		if (decidingRandom == 1) {
			for (int i = 0; i < 9; i++) {
				holdingNumber = btempBoard[0][i];
				btempBoard[0][i] = btempBoard[1][i];
				btempBoard[1][i] = holdingNumber;
			}
		} 
		if (decidingRandom == 2) {
			for (int i = 0; i < 9; i++) {
				holdingNumber = btempBoard[0][i];
				btempBoard[0][i] = btempBoard[2][i];
				btempBoard[2][i] = holdingNumber;
			}
		}
		decidingRandom = (int) (Math.random() * 3); // 0, 1, 2
		if (decidingRandom == 1) {
			for (int i = 0; i < 9; i++) {
				holdingNumber = btempBoard[3][i];
				btempBoard[3][i] = btempBoard[4][i];
				btempBoard[4][i] = holdingNumber;
			}
		} 
		if (decidingRandom == 2) {
			for (int i = 0; i < 9; i++) {
				holdingNumber = btempBoard[3][i];
				btempBoard[3][i] = btempBoard[5][i];
				btempBoard[5][i] = holdingNumber;
			}
		}
		decidingRandom = (int) (Math.random() * 3); // 0, 1, 2
		if (decidingRandom == 1) {
			for (int i = 0; i < 9; i++) {
				holdingNumber = btempBoard[6][i];
				btempBoard[6][i] = btempBoard[7][i];
				btempBoard[7][i] = holdingNumber;
			}
		}
		if (decidingRandom == 2) {
			for (int i = 0; i < 9; i++) {
				holdingNumber = btempBoard[6][i];
				btempBoard[6][i] = btempBoard[8][i];
				btempBoard[8][i] = holdingNumber;
			}
		}
		
		// Swap chunks of the initial board
		btempBoard = swapChunks(btempBoard);
		
	 //  On the overall board, swap the complete placements of two integers.
		int randomX = (int) ((Math.random() * 9) + 1);
		int randomY = (int) ((Math.random() * 9) + 1);
		while (randomX == randomY) {
			randomY = (int) ((Math.random() * 9) + 1);
		}
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (btempBoard[i][j] == randomX) {
					btempBoard[i][j] = randomY;
				} else if (btempBoard[i][j] == randomY) {
					btempBoard[i][j] = randomX;
				}
			}
		}
		return btempBoard;
	}
	
	/**
	 * Returns an array of a 'key' board. Internal uses.
	 * @return - Returns an INT array of an initial board.
	 */
	public static int[][] outputBeginningBoard() {
		String[][] stringBoard = new String[9][9];
		int[][] someBoard = new int[9][9];
		stringBoard[0] = "3 2 9 6 5 7 8 4 1".split(" ");
		stringBoard[1] = "7 4 5 8 3 1 2 9 6".split(" ");
		stringBoard[2] = "6 1 8 2 4 9 3 7 5".split(" ");
		stringBoard[3] = "1 9 3 4 6 8 5 2 7".split(" ");
		stringBoard[4] = "2 7 6 1 9 5 4 8 3".split(" ");
		stringBoard[5] = "8 5 4 3 7 2 6 1 9".split(" ");
		stringBoard[6] = "4 3 2 7 1 6 9 5 8".split(" ");
		stringBoard[7] = "5 8 7 9 2 3 1 6 4".split(" ");
		stringBoard[8] = "9 6 1 5 8 4 7 3 2".split(" ");
		for (int i = 0; i < 9; i++) {
		  for (int j = 0; j < 9; j++) {
		   someBoard[i][j] = Integer.parseInt(stringBoard[i][j]);
		  }
		}
		return someBoard;
	}
	
	/**
	 * Used to trigger the end of game.
	 * @return - Return TRUE if the board is filled completely.
	 * 			 Return FALSE if the board is not filled.
	 */
	public boolean isFilled() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (board[i][j].getNum() == 0) {
					return false;
				}
			}
		}
		return true;	
	}
	
	/**
	 * Used to trigger the end of game.
	 * @return - Return TRUE if the board is complete and correct.
	 * 			 Return FALSE if the board is not complete 
	 * 			 and correct.
	 */
	public boolean isCorrect() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
			  if (board[i][j].getNum() != completeBoard[i][j]) {
				return false;
			  }
			}
		}
		return true;
	}

	/**
	 * Checks to see if the game is over.
	 * @return True if board is filled and all squares are correct.
	 */
	public final boolean isGameOver() { 
		if (isFilled() && isCorrect()) {
			return true;
		}
		return false;	
	}

	@Override
	public final boolean isMove(final Move m) {	
		return false; 
	}

	@Override
	public final boolean isMove(final int x, final int y) { 
		return false; 
	}

	@Override
	public void saveState(final String filename) throws Exception { }

	@Override
	public void loadState(final String filename) throws Exception { } 
	
}
