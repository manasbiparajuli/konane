//************************************************************
//        * Name:  Manasbi Parajuli                                    *
//        * Project:  Project 2- Konane            *
//        * Class:  AI 331                       *
//        * Date:  3/7/18                           *
//        ************************************************************
package edu.ramapo.mparajul.konane.Model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by Manasbi on 2/1/2018.
 */

public class Game {

    // Instantiate player 1 and player 2 with appropriate color piece
    public Player player1 = new Player("B", true, 0);
    public Player player2 = new Player("W", false,0);

    // Instantiate board for the game
    public Board board = new Board();

    // the current board state
    private String[][] boardState = new String[board.getBoardRow()][board.getBoardColumn()];
    private boolean moveSuccess = false;
    // the best possible score for branch and bound
    private int bestPossibleScore = 0;

    // Start the game
    // Receives: none
    // Returns: null
    public void initializeGame(int boardRow, int boardColumn) {
        board.setBoardSize(boardRow, boardColumn);
        board.makeBoard();
        setBoardState(board.removeTwoPieces());
    }

    // Get the possible moves for a piece using Depth-First Search
    // Receives: pieceColor ->  the color of the piece of which we are populating the
    //                          possible moves
    // Returns: an array list of pairs of all possible moves for a given piece color
    public List<Pair<Integer, List<Integer>>> searchByDFS (String pieceColor) {
        // create a list of all the possible moves
        List<Pair<Integer, List<Integer>>> allPossibleMoves = new ArrayList<>();
        // get the position in the grid of the graph as we are traversing
        int startPosition = board.firstPiecePosition(pieceColor)[2];

        for (int parentPosition = startPosition ; parentPosition < board.getBoardSize(); parentPosition++) {
            List<Integer> manipulatedPositions = new ArrayList<>();

            // initialize all the board places to not visited
            Boolean[] visited = new Boolean[board.getBoardSize()];
            for (int j = 0; j < board.getBoardSize(); j++){
                visited[j] = false;
            }

            // Create a stack and push root node into the stack
            Stack<Integer> stack = new Stack<>();
            stack.push(parentPosition);

            // reset our moves list
            Pair<Integer, List<Integer>> moves;
            List<Integer> destMoveList = new ArrayList<>();

            while (!stack.empty()){
                int currPiece = stack.peek();
                stack.pop();


                // Get the row and column values for the current piece
                int     currPieceRow = board.get2DCoordinates(currPiece)[0],
                        currPieceColumn = board.get2DCoordinates(currPiece)[1];

                // Traverse only through unvisited nodes in the graph
                if (!visited[currPiece]) {
                    // set the flag as visited
                    visited[currPiece] = true;

                    System.out.println("pos popped from stack: " + currPiece);


                    //we are traversing based on the piece color
                    if (board.getBoard()[currPieceRow][currPieceColumn].equals(pieceColor)) {
                        if (board.checkMoreMoves(currPiece)) {
                            // Check if there is valid west pair
                            if (board.getPieceToNEWS()[3] >= 0) {
                                stack.push(board.getPieceToNEWS()[3]);
                                destMoveList.add(board.getPieceToNEWS()[3]);
                                manipulatedPositions.add(board.getPieceToNEWS()[3]);
                                fillWithCurrentPiece(board.getPieceToNEWS()[3], pieceColor);

//                                System.out.println("visited west");
                            }
                            // Check if there is valid south pair
                            if (board.getPieceToNEWS()[2] >= 0) {
                                stack.push(board.getPieceToNEWS()[2]);
                                destMoveList.add(board.getPieceToNEWS()[2]);
                                manipulatedPositions.add(board.getPieceToNEWS()[2]);
                                fillWithCurrentPiece(board.getPieceToNEWS()[2], pieceColor);
//                                System.out.println("visited south");
                            }
                            // Check if there is valid east pair
                            if (board.getPieceToNEWS()[1] >= 0) {
                                stack.push(board.getPieceToNEWS()[1]);
                                destMoveList.add(board.getPieceToNEWS()[1]);
                                manipulatedPositions.add(board.getPieceToNEWS()[1]);
                                fillWithCurrentPiece(board.getPieceToNEWS()[1], pieceColor);
//                                System.out.println("visited east");
                            }
                            // Check if there is valid north pair
                            if (board.getPieceToNEWS()[0] >= 0) {
                                stack.push(board.getPieceToNEWS()[0]);
                                destMoveList.add(board.getPieceToNEWS()[0]);
                                manipulatedPositions.add(board.getPieceToNEWS()[0]);
                                fillWithCurrentPiece(board.getPieceToNEWS()[0], pieceColor);
//                                System.out.println("visited north");
                            }
                        }
                    }
                }
            }

            fillWithEmptyPiece(manipulatedPositions);
            // add to our list of possible moves if there are possible destination pieces
            if (!destMoveList.isEmpty()) {
                moves = new Pair<>(parentPosition, destMoveList);

//                for (Integer dest: destMoveList) {
//                    System.out.println("parent:" + parentPosition +  "dest " + dest);
//                }

                allPossibleMoves.add(moves);
            }
        }
        return allPossibleMoves;
    }

    // Get the possible moves for a piece using Breadth-First Search
    // Receives: pieceColor ->  the color of the piece of which we are populating the
    //                          possible moves
    // Returns: an array list of pairs of all possible moves for a given piece color
    public List<Pair<Integer, List<Integer>>> searchByBreadthFS (String pieceColor) {
        // create a list of all the possible moves
        List<Pair<Integer, List<Integer>>> allPossibleMoves = new ArrayList<>();
        // get the first position in the grid where the piece color matches
        int startPosition = board.firstPiecePosition(pieceColor)[2];

        for (int parentPosition = startPosition; parentPosition < board.getBoardSize(); parentPosition++) {
            List<Integer> manipulatedPositions = new ArrayList<>();

            // Initialize all the nodes as not visited
            Boolean[] visited = new Boolean[board.getBoardSize()];
            for (int j = 0; j < board.getBoardSize(); j++) {
                visited[j]= false;
            }
            // Create a queue for BFS
            LinkedList<Integer> queue = new LinkedList<>();
            // Mark the current position as visited and enqueue it
            visited[parentPosition] = true;
            queue.add(parentPosition);

            // reset our moves list
            Pair<Integer, List<Integer>> moves;
            List<Integer> destMoveList = new ArrayList<>();

            while (!queue.isEmpty()) {
                // Dequeue a board piece from the queue
                int currPiecePos = queue.poll();

                // Get the row and column values for the current piece
                int     currPieceRow = board.get2DCoordinates(currPiecePos)[0],
                        currPieceColumn = board.get2DCoordinates(currPiecePos)[1];

                // Check moves for pieces that have the corresponding color of the current player
                if (board.getBoard()[currPieceRow][currPieceColumn].equals(pieceColor)) {
                    if (board.checkMoreMoves(currPiecePos)) {
                        // Check if there is valid north pair
                        if (board.getPieceToNEWS()[0] >= 0 && !visited[board.getPieceToNEWS()[0]]) {
                            visited[board.getPieceToNEWS()[0]] = true;
                            queue.add(board.getPieceToNEWS()[0]);
                            destMoveList.add(board.getPieceToNEWS()[0]);
                            manipulatedPositions.add(board.getPieceToNEWS()[0]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[0], pieceColor);
                        }
                        // Check if there is valid east pair
                        if (board.getPieceToNEWS()[1] >= 0 && !visited[board.getPieceToNEWS()[1]]) {
                            visited[board.getPieceToNEWS()[1]] = true;
                            queue.add(board.getPieceToNEWS()[1]);
                            destMoveList.add(board.getPieceToNEWS()[1]);
                            manipulatedPositions.add(board.getPieceToNEWS()[1]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[1], pieceColor);
                        }
                        // Check if there is valid south pair
                        if (board.getPieceToNEWS()[2] >= 0 && !visited[board.getPieceToNEWS()[2]]) {
                            visited[board.getPieceToNEWS()[2]] = true;
                            queue.add(board.getPieceToNEWS()[2]);
                            destMoveList.add(board.getPieceToNEWS()[2]);
                            manipulatedPositions.add(board.getPieceToNEWS()[2]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[2], pieceColor);
                        }
                        // Check if there is valid west pair
                        if (board.getPieceToNEWS()[3] >= 0 && !visited[board.getPieceToNEWS()[3]]) {
                            visited[board.getPieceToNEWS()[3]] = true;
                            queue.add(board.getPieceToNEWS()[3]);
                            destMoveList.add(board.getPieceToNEWS()[3]);
                            manipulatedPositions.add(board.getPieceToNEWS()[3]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[3], pieceColor);
                        }
                    }
                }
            }
            fillWithEmptyPiece(manipulatedPositions);
            // add to our list of possible moves if there are possible destination pieces
            if (!destMoveList.isEmpty()) {
                moves = new Pair<>(parentPosition, destMoveList);
                allPossibleMoves.add(moves);
            }
        }
        return allPossibleMoves;
    }


    // Set the heuristic values for moves in the board
    // Receives: pieceColor ->  the color of the piece of which we are populating the
    //                          possible moves
    // Returns: A HashMap of parentNode and its best heuristic value
    public HashMap<Integer,Integer> setHeuristicValues (String pieceColor) {
        // Create a HashMap that maps parent node with its highest heuristic value based on
        // the heuristic values of its children
        HashMap<Integer, Integer> parentBestWeight = new HashMap<>();

        // get the first position in the grid where the piece color matches
        int startPosition = board.firstPiecePosition(pieceColor)[2];
        // loop through the list of pieces in the board
        for (int parentPosition = startPosition; parentPosition < board.getBoardSize(); parentPosition++) {
            List<Integer> manipulatedPositions = new ArrayList<>();
            HashMap<Pair<Integer, Integer>,Integer> moveWeight = new HashMap<>();

            // Initialize all the nodes as not visited
            Boolean[] visited = new Boolean[board.getBoardSize()];
            for (int j = 0; j <board.getBoardSize(); j++) {
                visited[j]= false;
            }
            // Create a queue for Breadth First Search
            LinkedList<Integer> queue = new LinkedList<>();
            // Mark the current position as visited and enqueue it
            visited[parentPosition] = true;
            queue.add(parentPosition);

            // Set the parent's distance from root to 0
            moveWeight.put(new Pair<>(parentPosition,parentPosition), 0);

            // reset our moves list
            List<Integer> destMoveList = new ArrayList<>();

            while (!queue.isEmpty()) {
                // Dequeue a board piece from the queue
                int currPiecePos = queue.poll();

                // Get the row and column values for the current piece
                int     currPieceRow = board.get2DCoordinates(currPiecePos)[0],
                        currPieceColumn = board.get2DCoordinates(currPiecePos)[1];

                // Check moves for pieces that have the corresponding color of the current player
                if (board.getBoard()[currPieceRow][currPieceColumn].equals(pieceColor)) {
                    if (board.checkMoreMoves(currPiecePos)) {
                        // Check if there is valid north pair
                        if (board.getPieceToNEWS()[0] >= 0 && !visited[board.getPieceToNEWS()[0]]) {
                            visited[board.getPieceToNEWS()[0]] = true;
                            queue.add(board.getPieceToNEWS()[0]);
                            destMoveList.add(board.getPieceToNEWS()[0]);
                            manipulatedPositions.add(board.getPieceToNEWS()[0]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[0], pieceColor);
                            moveWeight.put(new Pair<>(parentPosition, board.getPieceToNEWS()[0]),
                                    moveWeight.get(new Pair<>(parentPosition, currPiecePos)) + 1);
                        }
                        // Check if there is valid east pair
                        if (board.getPieceToNEWS()[1] >= 0 && !visited[board.getPieceToNEWS()[1]]) {
                            visited[board.getPieceToNEWS()[1]] = true;
                            queue.add(board.getPieceToNEWS()[1]);
                            destMoveList.add(board.getPieceToNEWS()[1]);
                            manipulatedPositions.add(board.getPieceToNEWS()[1]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[1], pieceColor);
                            moveWeight.put(new Pair<>(parentPosition, board.getPieceToNEWS()[1]),
                                    moveWeight.get(new Pair<>(parentPosition, currPiecePos)) + 1);
                        }
                        // Check if there is valid south pair
                        if (board.getPieceToNEWS()[2] >= 0 && !visited[board.getPieceToNEWS()[2]]) {
                            visited[board.getPieceToNEWS()[2]] = true;
                            queue.add(board.getPieceToNEWS()[2]);
                            destMoveList.add(board.getPieceToNEWS()[2]);
                            manipulatedPositions.add(board.getPieceToNEWS()[2]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[2], pieceColor);
                            moveWeight.put(new Pair<>(parentPosition, board.getPieceToNEWS()[2]),
                                    moveWeight.get(new Pair<>(parentPosition, currPiecePos)) + 1);
                        }
                        // Check if there is valid west pair
                        if (board.getPieceToNEWS()[3] >= 0 && !visited[board.getPieceToNEWS()[3]]) {
                            visited[board.getPieceToNEWS()[3]] = true;
                            queue.add(board.getPieceToNEWS()[3]);
                            destMoveList.add(board.getPieceToNEWS()[3]);
                            manipulatedPositions.add(board.getPieceToNEWS()[3]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[3], pieceColor);
                            moveWeight.put(new Pair<>(parentPosition, board.getPieceToNEWS()[3]),
                                    moveWeight.get(new Pair<>(parentPosition, currPiecePos)) + 1);
                        }
                    }
                }
            }
            fillWithEmptyPiece(manipulatedPositions);
            // add to our list of possible moves if there are possible destination pieces
            if (!destMoveList.isEmpty()) {
                // get the maximum value from the weight map
                int maxValueInMap = (Collections.max(moveWeight.values()));
                for (Map.Entry<Pair<Integer, Integer>,Integer> entry: moveWeight.entrySet()) {
                    // search for the maximum value in the map and then put in a new map where
                    // the parent node gets mapped to the highest heuristic value
                    if (entry.getValue() == maxValueInMap) {
                        parentBestWeight.put(entry.getKey().first, maxValueInMap);
                    }
                }
            }
        }
        return parentBestWeight;
    }

    // Get the possible moves for a piece using Best-First Search
    // Receives: pieceColor ->  the color of the piece of which we are populating the
    //                          possible moves
    // Returns: an array list of pairs of all possible moves for a given piece color
    public List<Pair<Integer, List<Integer>>> searchByBestFS (String pieceColor) {
        // create a list of all the possible moves
        List<Pair<Integer, List<Integer>>> allPossibleMoves = new ArrayList<>();

        // get a map of parent nodes and their heuristic values
        HashMap<Integer, Integer> parentBestWeight = setHeuristicValues(pieceColor);

        // Convert the parentBestWeight HashMap into a stream, and then use comparator Combinators
        // from Map.Entry to sort the HashMap in descending order based on the hash values
        // Our sorted map will then have the node with the best heuristic value at the top
        // After using the stream library from Java 8, convert the stream into a HashMap
        HashMap<Integer, Integer> sortedParentByWeight = parentBestWeight.entrySet()
                .stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).
                                collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1,e2) -> e1,
                                LinkedHashMap::new));

        // Loop through the nodes in descending order of their heuristic values
        // This way, we will have the node with the most promising move at the top
        for (Integer parentPosition: sortedParentByWeight.keySet()) {
            List<Integer> manipulatedPositions = new ArrayList<>();

            // Initialize all the nodes as not visited
            Boolean[] visited = new Boolean[board.getBoardSize()];
            for (int j = 0; j < board.getBoardSize(); j++) {
                visited[j]= false;
            }
            // Create a queue for Best First Search
            LinkedList<Integer> queue = new LinkedList<>();
            // Mark the current position as visited and enqueue it
            visited[parentPosition] = true;
            queue.add(parentPosition);

            // reset our moves list
            Pair<Integer, List<Integer>> moves;
            List<Integer> destMoveList = new ArrayList<>();

            while (!queue.isEmpty()) {
                // Dequeue a board piece from the queue
                int currPiecePos = queue.poll();

                // Get the row and column values for the current piece
                int     currPieceRow = board.get2DCoordinates(currPiecePos)[0],
                        currPieceColumn = board.get2DCoordinates(currPiecePos)[1];

                // Check moves for pieces that have the corresponding color of the current player
                if (board.getBoard()[currPieceRow][currPieceColumn].equals(pieceColor)) {
                    if (board.checkMoreMoves(currPiecePos)) {
                        // Check if there is valid north pair
                        if (board.getPieceToNEWS()[0] >= 0 && !visited[board.getPieceToNEWS()[0]]) {
                            visited[board.getPieceToNEWS()[0]] = true;
                            queue.add(board.getPieceToNEWS()[0]);
                            destMoveList.add(board.getPieceToNEWS()[0]);
                            manipulatedPositions.add(board.getPieceToNEWS()[0]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[0], pieceColor);
                        }
                        // Check if there is valid east pair
                        if (board.getPieceToNEWS()[1] >= 0 && !visited[board.getPieceToNEWS()[1]]) {
                            visited[board.getPieceToNEWS()[1]] = true;
                            queue.add(board.getPieceToNEWS()[1]);
                            destMoveList.add(board.getPieceToNEWS()[1]);
                            manipulatedPositions.add(board.getPieceToNEWS()[1]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[1], pieceColor);
                        }
                        // Check if there is valid south pair
                        if (board.getPieceToNEWS()[2] >= 0 && !visited[board.getPieceToNEWS()[2]]) {
                            visited[board.getPieceToNEWS()[2]] = true;
                            queue.add(board.getPieceToNEWS()[2]);
                            destMoveList.add(board.getPieceToNEWS()[2]);
                            manipulatedPositions.add(board.getPieceToNEWS()[2]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[2], pieceColor);
                        }
                        // Check if there is valid west pair
                        if (board.getPieceToNEWS()[3] >= 0 && !visited[board.getPieceToNEWS()[3]]) {
                            visited[board.getPieceToNEWS()[3]] = true;
                            queue.add(board.getPieceToNEWS()[3]);
                            destMoveList.add(board.getPieceToNEWS()[3]);
                            manipulatedPositions.add(board.getPieceToNEWS()[3]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[3], pieceColor);
                        }
                    }
                }
            }
            fillWithEmptyPiece(manipulatedPositions);
            // add to our list of possible moves if there are possible destination pieces
            if (!destMoveList.isEmpty()) {
                moves = new Pair<>(parentPosition, destMoveList);
                allPossibleMoves.add(moves);
            }
        }
        return allPossibleMoves;
    }

    // Get the possible moves for a piece using Best-First Search
    // Receives: pieceColor ->  the color of the piece of which we are populating the
    //                          possible moves
    // Returns: an array list of pairs of all possible moves for a given piece color
    public List<Pair<Integer, List<Integer>>> searchByBranchAndBound (String pieceColor, String
            depth) {
        // get the integer value for the depth that we are looking for
        int depthLook = convertDepthToInt(depth);

        // create a list of all the possible moves
        List<Pair<Integer, List<Integer>>> allPossibleMoves = new ArrayList<>();
        // get a map of parent nodes and their heuristic values
        HashMap<Integer, Integer> parentBestWeight = setHeuristicValues(pieceColor);

        // Convert the parentBestWeight HashMap into a stream, and then use comparator Combinators
        // from Map.Entry to filter the keys that have heuristic values less than or equal to the
        // depth that the user wants to search for. Then we sort the result with the highest
        // heuristic value at the top as we want the best move that will maximize the result
        // After using the stream library from Java 8, convert the stream into a HashMap
        LinkedHashMap<Integer, Integer> parentByDepth = parentBestWeight.entrySet()
                .stream().filter(p->p.getValue() <= depthLook).
                        sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).
                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1,e2) -> e1, LinkedHashMap::new));

        // Get the maximum heuristic value of the sorted HashMap
        int maxValueInMap = (Collections.max(parentBestWeight.values()));
        // Get the parent with the highest heuristic
        int parentPosition = parentByDepth.keySet().stream().findFirst().get();
        // initialize the depth counter that we are traversing in the branch and bound algorithm
        int depthCounter = 0;

        List<Integer> manipulatedPositions = new ArrayList<>();
        // initialize all the board places to not visited
        Boolean[] visited = new Boolean[board.getBoardSize()];
        for (int j = 0; j < board.getBoardSize(); j++) {
            visited[j] = false;
        }

        // Create a stack and push root node into the stack
        Stack<Integer> stack = new Stack<>();
        stack.push(parentPosition);

        // reset our moves list
        Pair<Integer, List<Integer>> moves;
        List<Integer> destMoveList = new ArrayList<>();

        while (!stack.empty()) {
            int currPiece = stack.peek();
            stack.pop();
            // Get the row and column values for the current piece
            int     currPieceRow = board.get2DCoordinates(currPiece)[0],
                    currPieceColumn = board.get2DCoordinates(currPiece)[1];

            // Traverse only through unvisited nodes in the graph
            if (!visited[currPiece]) {
                // set the flag as visited
                visited[currPiece] = true;

                //we are traversing based on the piece color
                if (board.getBoard()[currPieceRow][currPieceColumn].equals(pieceColor)) {
                    if (board.checkMoreMoves(currPiece)) {
                        // Check if there is valid west pair
                        if (board.getPieceToNEWS()[3] >= 0) {
                            stack.push(board.getPieceToNEWS()[3]);
                            destMoveList.add(board.getPieceToNEWS()[3]);
                            manipulatedPositions.add(board.getPieceToNEWS()[3]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[3], pieceColor);
                        }
                        // Check if there is valid south pair
                        if (board.getPieceToNEWS()[2] >= 0) {
                            stack.push(board.getPieceToNEWS()[2]);
                            destMoveList.add(board.getPieceToNEWS()[2]);
                            manipulatedPositions.add(board.getPieceToNEWS()[2]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[2], pieceColor);
                        }
                        // Check if there is valid east pair
                        if (board.getPieceToNEWS()[1] >= 0) {
                            stack.push(board.getPieceToNEWS()[1]);
                            destMoveList.add(board.getPieceToNEWS()[1]);
                            manipulatedPositions.add(board.getPieceToNEWS()[1]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[1], pieceColor);
                        }
                        // Check if there is valid north pair
                        if (board.getPieceToNEWS()[0] >= 0) {
                            stack.push(board.getPieceToNEWS()[0]);
                            destMoveList.add(board.getPieceToNEWS()[0]);
                            manipulatedPositions.add(board.getPieceToNEWS()[0]);
                            fillWithCurrentPiece(board.getPieceToNEWS()[0], pieceColor);
                        }
                        // if we have any moves to north, east, south and west of the current
                        // piece, increase the depth counter
                        if (board.getPieceToNEWS()[3] >= 0 || board.getPieceToNEWS()[2] >= 0
                                || board.getPieceToNEWS()[1] >= 0 || board.getPieceToNEWS()
                                [0] >= 0) {
                            depthCounter++;
                        }
                    }
                }
            }
            // break from the loop if our depth counter is equal or greater than the maximum
            // heuristic value of the HashMap
            if (depthCounter >= maxValueInMap) {
                break;
            }
        }

        fillWithEmptyPiece(manipulatedPositions);
        // add to our list of possible moves if there are possible destination pieces
        if (!destMoveList.isEmpty()) {
            moves = new Pair<>(parentPosition, destMoveList);
            allPossibleMoves.add(moves);
            // the maximum heuristic value is the best possible score
            setBestPossibleScore(maxValueInMap);
        }
        return allPossibleMoves;
    }

    // Initialize saved game state
    // Receives: boardState -> the saved board state
    // Returns: null
    public void resumeGame(String[][]boardState, int boardRow) {
        board.setBoardSize(boardRow, boardRow);
        board.makeBoard();
        board.setBoard(boardState);
        setBoardState(board.getBoard());
    }

    // Return the array of scores
    // Receives: none
    // Returns: scores of player as an array
    public int[] playerScores() {
        int[]temp = new int[2];
        temp[0] = player1.getPlayerScore();
        temp[1] = player2.getPlayerScore();
        return temp;
    }

    // Get the board when the player makes the move
    // Receives: source -> the moving piece
    //           destination -> the destination for the moving piece
    // Returns: the 2D representation of the board after the move is made
    public String[][] makeMove(int source, int destination) {
        String[][] temp = (board.makeMove(source, destination));

        if (board.isMoveSuccessful()) {
            setMoveSuccess(true);
        }else {
            setMoveSuccess(false);
        }
        return temp;
    }

    // Initiates the game play
    // Receives: source -> the moving piece
    //           destination -> the destination for the moving piece
    // Returns: null
    public void play(int source, int destination) {

        // player 1's turn to play
        if (player1.isCurrentPlay()){
            // set the new board state after the move is made
            setBoardState(makeMove(source, destination));

            if (isMoveSuccess()) {
                setBoardState(board.updatedBoard(getBoardState()));
                player1.setSuccessfulMove(true);
                player1.setIllegalMove(false);
                player1.setPlayerScore(Board.getMoveScore());

                player1.setCurrentPlay(false);
                player2.setCurrentPlay(true);
            } else {
                player1.setSuccessfulMove(false);
            }
        }

        // player 2's turn to play
        if (player2.isCurrentPlay()){

            // set the new board state after the move is made
            setBoardState(makeMove(source, destination));

            if (isMoveSuccess()) {
                setBoardState(board.updatedBoard(getBoardState()));
                player2.setSuccessfulMove(true);
                player2.setIllegalMove(false);
                player2.setPlayerScore(Board.getMoveScore());

                player1.setCurrentPlay(true);
                player2.setCurrentPlay(false);
            }else {
                player2.setSuccessfulMove(false);
            }
        }
    }

    // Getter function for the boardState
    // Receives: none
    // Returns: 2d representation of the board
    public String[][] getBoardState() {
        return boardState;
    }

    // Setter function for the boardState
    // Receives: boardState -> 2D representation of the board
    // Returns: null
    public void setBoardState(String[][] boardState) {
        this.boardState = boardState;
    }

    // Getter function for successful move
    // Receives: none
    // Returns: whether the move was successful or not
    public boolean isMoveSuccess() {
        return moveSuccess;
    }

    // Setter function for successful move
    // Receives: move_success -> the true/false value for the move
    // Returns: null
    public void setMoveSuccess(boolean move_success) {
        this.moveSuccess = move_success;
    }

    // Getter function for best possible score in branch and bound algorithm
    // Receives: null
    // Returns: the best possible score for a given depth in branch and bound
    public int getBestPossibleScore() {
        return bestPossibleScore;
    }

    // Setter function to set best possible score in branch and bound algorithm
    // Receives: bestPossibleScore -> the best possible score at a given depth
    // Returns: null
    public void setBestPossibleScore(int bestPossibleScore) {
        this.bestPossibleScore = bestPossibleScore;
    }

    // Temporarily fill an empty position with the current piece
    // Receives: position -> the position in the grid
    //           pieceColor -> the current player's piece
    // Returns: null
    public void fillWithCurrentPiece (int position, String pieceColor) {
        // replace empty pieces that were part of successful destination
        // moves with current piece color
        board.getBoard()[board.get2DCoordinates(position)[0]][board.get2DCoordinates(position)[1]] = pieceColor;
    }

    // Reverse filling of manipulated pieces to empty pieces
    // Receives: list of positions that we manipulated
    // Returns: null
    public void fillWithEmptyPiece (List<Integer> positionArray) {
        // Loop through the list and put empty pieces in the board
        for (int temp: positionArray){
            board.getBoard()[board.get2DCoordinates(temp)[0]][board.get2DCoordinates(temp)[1]] = "E";
        }
    }

    // Convert the depth value received as string into an integer value
    // Receives: depth -> the string value of the depth that the user wants to traverse in Branch
    //                    and Bound
    // Returns: the integer value of the depth that we are bound to search for
    public int convertDepthToInt (String depth) {
        int depthInt;

        // if the user selected entire tree for branch and bound, then
        // assign depth value to maximum integer value
        if (depth.equals("Entire Tree")) {
            // assign depth value the maximum possible value
            depthInt = Integer.MAX_VALUE;
        }
        else {
            depthInt = Integer.parseInt(depth);
        }
        return depthInt;
    }
}