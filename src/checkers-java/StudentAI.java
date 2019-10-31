import java.util.Random;
import java.util.Vector;

// The following part should be completed by students.
// Students can modify anything except the class name and exisiting functions and varibles.

public class StudentAI extends AI {
    public StudentAI(int col, int row, int k) throws InvalidParameterError {
        super(col, row, k);

        this.board = new Board(col, row, k);
        this.board.initializeGame();
        this.player = 2;
    }

    public Move GetMove(Move move) throws InvalidMoveError {
        if (!move.seq.isEmpty())
        	board.makeMove(move, (player == 1) ? 2 : 1);
        else // for the first time player
        	player = 1;
        
/***********************		DELETE THIS IN THE FINAL VERSION	***********************************/        

////        System.out.println("Count, B: " + bCount + "  W:" + wCount);
//        
        Vector<Vector<Move>> moves = board.getAllPossibleMoves(player); // possible moves I can make
        
//      Print all the possible moves
//      i: ith row
//      j: j number of possible moves in that ith row
        System.out.println("STUDENT AI");
        for (int i = 0; i < moves.size(); ++i) {
            System.out.print(i + ": [");
            for (int j = 0; j < moves.get(i).size(); ++j) {
                System.out.print(((j != 0) ? ", " : "") + j + ": " + moves.get(i).get(j).toString());
            }
            System.out.println("]");
        }
        
/******************************************************************************************************/
        
//        Vector<Vector<Move>> moves = board.getAllPossibleMoves(player);
        
//      If there are more than 1 moves available,
//      USE Alpha-Beta Pruning!
        int turn = player;
        
//      result[0] = index, result[1] = innerIndex, result[2] = score
        int[] result = minimax(-1000, 1000, turn, 0);
//     
        System.out.println(result[2]);
        int index, innerIndex;
        if(result[2] <= 0)
        {
        	Random randGen = new Random();
    	    index = randGen.nextInt(moves.size());
    	    innerIndex = randGen.nextInt(moves.get(index).size());
        }
       
        else
        {
        	index = result[0];
        	innerIndex = result[1];
        	System.out.println("Index: "+index+" Inner: "+innerIndex);
        }
//        
//      Move based on the decision
	    Move resMove = moves.get(index).get(innerIndex);
        board.makeMove(resMove, player);
        return resMove;
    }
    
    private int[] minimax(int alpha, int beta, int turn, int depth) throws InvalidMoveError
    {   
    	Vector<Vector<Move>> nextMoves = board.getAllPossibleMoves(turn); // possible moves I can make
    	int bestIndex = 0, bestInnerIndex = 0;
    	int score=0;
    	
    	if(depth==4 || nextMoves.isEmpty())
    	{
    		// evaluate score
    		score = evaluate(turn);
//    		System.out.println("score: "+score+"\t");
    		return new int[] {bestIndex, bestInnerIndex, score};
    	}
    	
    	for(int i = 0; i<nextMoves.size(); i++)
    	{
    		for (int j = 0; j < nextMoves.get(i).size(); ++j)
    		{
    			Move tempMove = nextMoves.get(i).get(j);
    	        board.makeMove(tempMove, turn);

    			// AI's turn (Find max and store it in alpha)
    	    	if(turn==player)
    	    	{
    	    		int[] temp = minimax(alpha, beta, (turn==1) ? 2:1, depth+1);
    	    		if(temp[2]>alpha)
    	    		{
    	    			alpha = temp[2];
    	    			score += alpha;
    	    			bestIndex = i;
    	    			bestInnerIndex = j;
    	    		}
    	    	}
    	    	
    	    	// Opponent's turn (Find min and store it in beta)
    	    	else
    	    	{
    	    		int[] temp = minimax(alpha, beta, (turn==1) ? 2:1, depth+1);
    	    		if(temp[2]<beta)
    	    		{
    	    			beta = temp[2];
    	    			score += beta;
    	    			bestIndex = i;
    	    			bestInnerIndex = j;
    	    		}
    	    	}
    	    	
    	        board.Undo();
    	    	
    	    	if(alpha >= beta) break;
    	    
	    	}
    	}

    	return new int[] {bestIndex, bestInnerIndex, score};
    }
    
    // return 0 if tie, negative if my disk < opp disk, positive if my disk > opp disk
    private int evaluate(int turn)
    {
    	int[] disks = countBW();
    	int bCount = disks[0];
    	int wCount = disks[1];
    	
    	if(turn==player)
    	{
    		if(turn==1)
    			return bCount-wCount;
    		else
    			return wCount-bCount;
    	}
    	else
    	{
    		if(turn==2)
    			return bCount-wCount;
    		else
    			return wCount-bCount;
    	}
    }
    
    private int[] countBW()
    {
    	int bCount = 0;
    	int wCount = 0;
    	for (int i = 0; i < this.board.row; i++) {
            for (int j = 0; j < this.board.col; j++) {
                Checker checker = this.board.board.get(i).get(j);
                if (checker.color == "W")
                    wCount++;
                else if (checker.color == "B")
                    bCount++;
            }
        }
    	return new int[] {bCount, wCount};
    }
}
