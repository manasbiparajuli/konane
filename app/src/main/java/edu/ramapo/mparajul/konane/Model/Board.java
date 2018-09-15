//************************************************************
//        * Name:  Manasbi Parajuli                                    *
//        * Project:  Project 2- Konane            *
//        * Class:  AI 331                       *
//        * Date:  3/7/18                           *
//        ************************************************************

package edu.ramapo.mparajul.konane.Model;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.HashMap;

public class Board {

    private static final String BLACK_PIECE = "B";
    private static final String WHITE_PIECE = "W";
    private static final String EMPTY_PIECE = "E";

    private int boardRow;
    private int boardColumn;
    private int boardSize;
    private String[][] board;

    private static int moveScore = 1;
    private boolean moveSuccessful = false;     // flag for a successful move

    // Map to store the position of the board and its 2D coordinates
    private HashMap<Integer, int[]> gridMap = new HashMap<>();
    // HashMap with piece color and its corresponding board position
    protected HashMap<String, int[]> piecesRemoved = new HashMap<>();

    // Initialize board with pieces
    // Receives: none
    // Returns: a 2D representation of the board
    public String[][] makeBoard() {

        // keep track of position in the grid
        int count = 0;

        for (int i = 0; i < getBoardRow(); i++) {
            for (int j = 0; j < getBoardColumn(); j++) {

                // Black squares have combination of odd-odd or even-even
                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 == 1 && j % 2 == 1)) {
                    board[i][j] = BLACK_PIECE;
                }

                // White squares have combination of odd-even or even-odd
                if ((i % 2 == 1 && j % 2 == 0) || (i % 2 == 0 && j % 2 == 1)) {
                    board[i][j] = WHITE_PIECE;
                }

                // Form a map of position of the grid to its 2D coordinates
                int[] arr = {i, j};
                gridMap.put(count, arr);
                count++;
            }
        }
        return board;
    }

    // Get the updated board
    // Receives: board -> the 2D representation of the board
    // Returns: the updated 2D representation of the board
    public String[][] updatedBoard(String[][] board) {
        for (int i = 0; i < boardRow; i++) {
            for (int j = 0; j < boardColumn; j++) {
                this.board[i][j] = board[i][j];
            }
        }
        return board;
    }

    // Helper function to generate random numbers
    // Receives: minBound -> the minimum bound to generate a random number
    //           maxBound -> the maximum bound to generate a random number
    // Returns: a random number
    public int generateRandomNo(int maxBound) {
        Random rand = new Random();
        return rand.nextInt(maxBound + 1);
    }

    // Remove two pieces at the beginning of the game
    // Receives: none
    // Returns: a 2D representation of the board after two pieces are removed
    public String[][] removeTwoPieces() {
        int row, column;

        // Randomly remove a black piece at the beginning of the game
        // Black squares have combination of odd-odd or even-even
        do {
            row = generateRandomNo(getBoardRow() - 1);
            column = generateRandomNo(getBoardColumn() - 1);
        } while (!(row % 2 == 1 && column % 2 == 1) || (row % 2 == 0 && column % 2 == 0));

        board[row][column] = EMPTY_PIECE;
        int[] arr = {row, column};
        piecesRemoved.put("Black", arr);

        // Randomly remove a white piece at the beginning of the game
        // White squares have combination of odd-even or even-odd
        do {
            row = generateRandomNo(getBoardRow() - 1);
            column = generateRandomNo(getBoardColumn() - 1);
        } while (!(row % 2 == 1 && column % 2 == 0) || (row % 2 == 0 && column % 2 == 1));

        board[row][column] = EMPTY_PIECE;

        int[] arr1 = {row, column};
        piecesRemoved.put("White", arr1);

        return board;
    }

    // return the 2D coordinates for the position in the grid
    // Receives: position -> the position in the grid view
    // Returns: an array of the position in 2D plane
    public int[] get2DCoordinates(int position) {

        // Search for the position's 2D coordinates in the map
        String numberStr = Arrays.toString(gridMap.get(position));

        // Get the corresponding row and column values respectively
        int i = Character.getNumericValue(numberStr.charAt(1));
        int j = Character.getNumericValue(numberStr.charAt(4));

        // return the 2D points as an array
        int[] temp_arr = {i, j};
        return temp_arr;
    }

    // Get the current board state when a move is made
    // Receives: source -> the moving piece
    //           destination -> the destination for the moving piece
    // Returns: the 2D representation of the board after the move is made
    public String[][] makeMove(int source, int destination) {

        // temporarily store the current piece in the source position
        String temp;

        int sourceRow = get2DCoordinates(source)[0];
        int sourceColumn = get2DCoordinates(source)[1];
        int destinationRow = get2DCoordinates(destination)[0];
        int destinationColumn = get2DCoordinates(destination)[1];

        int columnDifference = Math.abs(sourceColumn - destinationColumn);
        int rowDifference = Math.abs(sourceRow - destinationRow);

        // Check if the move is valid
        // If success, swap the piece and remove the piece in between the source and
        // destination
        if (isValidMove(sourceRow, sourceColumn, destinationRow, destinationColumn)) {
            // Place an empty piece in between the source and the destination piece
            if (columnDifference == 2) {
                board[sourceRow][(sourceColumn + destinationColumn) / 2 ] = EMPTY_PIECE;
            }
            if (rowDifference == 2) {
                board[(sourceRow + destinationRow) / 2 ][sourceColumn] = EMPTY_PIECE;
            }

            temp = board[sourceRow][sourceColumn];
            // Source has empty piece and destination will have the source piece
            board[sourceRow][sourceColumn] = EMPTY_PIECE;
            board[destinationRow][destinationColumn] = temp;

            moveSuccessful = true;
            return board;
        }

        // User made an invalid move
        else {
            moveSuccessful = false;
            return board;
        }
    }

    // Returns if the move was valid or not
    // Receives: sourceRow -> the row value of the source
    //           sourceColumn -> the column value of the source
    //           destinationRow -> the row value of the destination
    //           destinationColumn -> the column value of the destination
    // Returns: the flag for a valid move
    public boolean isValidMove (int sourceRow, int sourceColumn, int destinationRow, int
            destinationColumn) {
        int columnDifference = Math.abs(sourceColumn - destinationColumn);
        int rowDifference = Math.abs(sourceRow - destinationRow);

        boolean isColumnMove = false,
                isRowMove = false;

        String opponentPiece = "";
        if (board[sourceRow][sourceColumn].equals(BLACK_PIECE)) {
            opponentPiece = WHITE_PIECE;
        }
        else if (board[sourceRow][sourceColumn].equals(WHITE_PIECE)) {
            opponentPiece = BLACK_PIECE;
        }

        // User moved the piece horizontally
        if (sourceRow == destinationRow) {isRowMove = true;}
        // User moved the piece vertically
        if (sourceColumn == destinationColumn) {isColumnMove = true;}

        // The column or values should have a difference of 2 to have a valid move
        if (isRowMove || isColumnMove) {
            if (columnDifference == 2 || rowDifference == 2) {
                // single move
                if (isRowMove && columnDifference == 2) {
                    // check if there is an empty piece in between the source and the destination piece
                    if (board[destinationRow][destinationColumn].equals(EMPTY_PIECE) && board[sourceRow][(sourceColumn + destinationColumn) / 2 ].equals(opponentPiece)) {
                        return true;
                    }
                }
                if (isColumnMove && rowDifference == 2) {
                    if (board[destinationRow][destinationColumn].equals(EMPTY_PIECE) && board[(sourceRow + destinationRow) / 2 ][sourceColumn].equals(opponentPiece)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Check if there are valid moves left for a piece in the board
    // Receives: pieceColor -> the board piece of which we are checking if any of its other
    // pieces can have a valid move
    // Returns: flag based on the availability of the moves left
    public boolean isMovesRemainingOnBoard(String pieceColor) {
        // position of the board
        int count = 0;
        // loop through the pieces in the board
        for (int i = 0; i < getBoardRow(); i++) {
            for (int j = 0; j < getBoardColumn(); j++) {
                // check only for the color piece and not the opponent's or empty pieces
                if (board[i][j].equals(pieceColor)) {
                    // break if there are moves left on the board for any piece
                    if (checkMoreMoves(count)){return true;}
                }
                // move to the next position in the board
                count++;
            }
        }
        return false;
    }

    // Get the position of the first pieceColor in the board
    // Receives: pieceColor -> the color of the piece of which we are finding the
    //                          position of
    // Returns: an array that corresponds to 2D position in the board
     public int[] firstPiecePosition (String pieceColor) {
        // position of the board
        int count = 0;
        // initialize array to have negative values
        int[] arr = {-1,-1, -1};
        // loop through the pieces in the board
        // return the 2D coordinates where the first piece of the pieceColor lies
        for (int i = 0; i < getBoardRow(); i++) {
            for (int j= 0; j < getBoardColumn(); j++){
                if (board[i][j].equals(pieceColor)) {
                    arr[0]=i;
                    arr[1]=j;
                    arr[2]=count;
                    return arr;
                }
                count++;
            }
         }
         // There are no pieces that match the piece color, this will return garbage value
         return arr;
    }

    // Default values for an array containing pieces North, East, South, West of a given piece
    private int[] pieceToNEWS = {-1,-1,-1,-1};

    // Get the position of piece north, east, south and west to a given piece
    // Receives: null
    // Returns: an array of pieces stored in the order of north, east, south, west
    public int[] getPieceToNEWS() {return pieceToNEWS;}

    // Check if there are moves available for a particular piece
    // Receives: currentPiece -> the piece which we try to move in top, right, bottom and left
    // direction
    // Returns: flag that returns if a piece can be moved in one of top, right, bottom and left
    // directions
    public boolean checkMoreMoves(int currentPiece) {
        // fill the direction array with negative values
        Arrays.fill(pieceToNEWS, -1);
        int sourceRow = get2DCoordinates(currentPiece)[0];
        int sourceColumn = get2DCoordinates(currentPiece)[1];

        // for successful north and south moves, only row values changes
        // for successful east and west move, only column values changes
        int north = sourceRow - 2,
                east = sourceColumn + 2,
                south = sourceRow + 2,
                west = sourceColumn - 2;

        boolean northExist = false, eastExist = false, southExist = false, westExist = false;

        //north move
        if ((north >= 0 && north <= getBoardRow() - 1) && isValidMove(sourceRow, sourceColumn,
                north,
                sourceColumn)){
            pieceToNEWS[0] = convert2DToPosition(north, sourceColumn);
            northExist = true;
        }
        // east move
        if ((east >= 0 && east <= getBoardRow() - 1) && isValidMove(sourceRow, sourceColumn,
                sourceRow, east)) {
            pieceToNEWS[1] = convert2DToPosition(sourceRow, east);
            eastExist = true;
        }
        // south move
        if ((south >= 0 && south <= getBoardRow() - 1) && isValidMove(sourceRow, sourceColumn,
                south,
                sourceColumn)) {
            pieceToNEWS[2] = convert2DToPosition(south, sourceColumn);
            southExist = true;
        }
        // west move
        if ((west >= 0 && west <= getBoardRow() - 1) && isValidMove(sourceRow, sourceColumn,
                sourceRow, west)) {
            pieceToNEWS[3] = convert2DToPosition(sourceRow, west);
            westExist = true;
        }

        if (northExist || eastExist || southExist || westExist) {
            return true;
        }
        // no valid moves possible in all 4 directions
        return false;
    }

    // Get the position of a piece in the grid based on 2D coordinates
    // Receives: row, column -> the row and column coordinates at a position in the grid
    // Returns: the position of the piece in the grid
    public int convert2DToPosition (int row, int column ) {
        int count = 0;

        if (row >= 0 && column >= 0) {
            for (int i = 0; i < getBoardRow(); i++) {
                for (int j = 0; j < getBoardColumn(); j++) {
                    if (i == row && j == column) {
                        return count;
                    }
                    count++;
                }
            }
        }
        return count;
    }

    public int getBoardRow() {
        return boardRow;
    }
    public void setBoardRow(int boardRow) {
        this.boardRow = boardRow;
    }

    public int getBoardColumn() {
        return boardColumn;
    }
    public void setBoardColumn(int boardColumn) {
        this.boardColumn = boardColumn;
    }

    public int getBoardSize() {
        return boardSize;
    }
    public void setBoardSize(int column, int row) {
        setBoardColumn(column);
        setBoardRow(row);
        this.boardSize = column * row;
        board = new String[row][column];
    }

    // Set the flag if there is a valid move
    // Receives: none
    // Returns: the flag for a successful move
    public boolean isMoveSuccessful() {
        return moveSuccessful;
    }

    // Get the board state
    // Receives: none
    // Returns: the 2D representation of the board
    public String[][] getBoard() {
        return board;
    }

    // Set the board state
    // Receives: board --> the 2D representation of the board
    // Returns: none
    public void setBoard(String[][] board) {
        this.board = board;
    }

    // Get the score of the move
    // Receives: none
    // Returns: the score of the move
    public static int getMoveScore() {
        return moveScore;
    }

    // Returns the HashMap with piece color and its corresponding board position
    // Receives: null
    // Returns: null
    public HashMap<String, int[]> getPiecesRemoved() {
        return piecesRemoved;
    }
}