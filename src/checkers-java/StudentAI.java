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
        
        Vector<Vector<Move>> moves = board.getAllPossibleMoves(player); // possible moves I can mak
        
/***************************	TO PRINT ALL POSSIBLE MOVES		***************************************/        

////      Print all the possible moves
////      i: ith row
////      j: j number of possible moves in that ith row
//        System.out.println("STUDENT AI");
//        for (int i = 0; i < moves.size(); ++i) {
//            System.out.print(i + ": [");
//            for (int j = 0; j < moves.get(i).size(); ++j) {
//                System.out.print(((j != 0) ? ", " : "") + j + ": " + moves.get(i).get(j).toString());
//            }
//            System.out.println("]");
//        }
//        
/******************************************************************************************************/
        
        int turn = player;
 
        int[] result = minimax(turn, 0);

        int index, innerIndex;
    	index = result[0];
    	innerIndex = result[1];
	    Move resMove = moves.get(index).get(innerIndex);
        board.makeMove(resMove, player);
        return resMove;
    }
    
    private int[] minimax(int turn, int depth) throws InvalidMoveError
    {
    	Vector<Vector<Move>> nextMoves = board.getAllPossibleMoves(turn); // possible moves I can make
    	int index=-1, innerIndex=-1 , score=0;
    	int max = -999, min = 999;
    	
    	int bestIndex = -1, bestInnerIndex = -1;
    	if(depth==4 || nextMoves.isEmpty())
    	{
    		if(turn == player)
    			score = evaluate(turn);
    		else
    			score = -evaluate(turn);
    		
    		return new int[] {bestIndex, bestInnerIndex, score};
    	}
    	
    	for(int i = 0; i<nextMoves.size(); i++)
    	{
    		for (int j = 0; j < nextMoves.get(i).size(); ++j)
    		{    			
    			Move currMove = nextMoves.get(i).get(j);
    			board.makeMove(currMove, turn);
    		
    			int[] temp = minimax((turn==1) ? 2:1, depth+1);
    			
    			// My AI's turn
    			if(turn == player)
    			{
    				max = Math.max(temp[2], max);
    				  				
    				if(temp[2] == max)
    				{
    					score = max;
    					if(depth == 0)
    					{
    						bestIndex = i;
    						bestInnerIndex = j;
    					}
    				}
    			}
    			
    			// Opp's turn
    			else
    			{
    				min = Math.min(temp[2], min);
    				
    				if(min==temp[2])
    				{
    					score = min;
    					if(depth == 0)
    					{
    						bestIndex = i;
    						bestInnerIndex = j;
    					}
    				}
    			}
    			
    			board.Undo();
    		}
    	}
    	
    	return new int[] {bestIndex, bestInnerIndex, score};
    }
        
    private int evaluate(int turn)
    {
    	int[] disks = countBW();
    	int bCount = disks[0];
    	int bKCount = disks[1];
    	int wCount = disks[2];
    	int wKCount = disks[3];
    	
    	if(turn==1)
    		return (bCount+2*bKCount)-(wCount+2*wKCount);
    	
    	return (wCount+2*wKCount)-(bCount+2*bKCount);
    }
    
    private int[] countBW()
    {
    	int bCount = 0, bKingCount = 0;
    	int wCount = 0, wKingCount = 0;
    	for (int i = 0; i < this.board.row; i++) {
            for (int j = 0; j < this.board.col; j++) {
                Checker checker = this.board.board.get(i).get(j);
                if (checker.color == "W")
                    if(checker.isKing == true)
                    	wKingCount++;
                    else
                    	wCount++;
                else if (checker.color == "B")
                	if(checker.isKing == true)
                    	bKingCount++;
                	else
                		bCount++;
            }
        }
    	return new int[] {bCount, bKingCount, wCount, wKingCount};
    }
}
