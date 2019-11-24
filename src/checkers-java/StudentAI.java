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
		//right now the move being returned is sometimes a backwards move for a non-king piece
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
    			score = mahejaEval(turn);
    		else
    			score = -mahejaEval(turn);
    		
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

	//return whether or not the piece is in the back row. 
	//use this in order to avoid moving pieces in the back row
	private boolean backRow(int r, int max, String c)
	{
		if(c == "W") return (r == max);
		if(c == "B") return (r == 0);
		return false;
	}

	//returns the dimensions of the center of the board
	//if the move is going to land you in the center of the board the move gets a higher score
	private int[] center()
	{
		int startRow = (this.board.row / 2) - 1;
		int startCol = (this.board.col / 2) - 1;
		return new int[] {startRow, startCol, 2 * startRow, 2 * startCol};
	}

	//return the number of enemy pieces in the area
	//where i = row and j = col of the square where the checker will move to 
	//use this to see if there are a lot of enemy pieces in the area of the move
	private int checkArea(int i , int j, int turn)
	{
		int total = 0;
		int rowStart = 0; int rowEnd = this.board.row - 1 ; int colStart = 0; int colEnd = this.board.col - 1;
		
		if(turn == 1){
			//check the number of black pieces in the move area?
			return total;
		}	
		//check the total number of white pieces in the area?
		return total;
	}

	private int mahejaEval(int turn){
		//index 0 = pawns
		//index 1 = kings
		//index 2 = back row
		//index 3 = center of the board
		//index 4 = number of pieces that can be taken
		int[] whiteCount = new int[5];
		int[] blackCount = new int[5];

		//get the number of kings and pawns 
		int[] pawnsAndKings = countBW();
		whiteCount[0] = pawnsAndKings[2];
		whiteCount[1] = pawnsAndKings[3];
		blackCount[0] = pawnsAndKings[0];
		blackCount[1] = pawnsAndKings[1];
	
		int[] dimensions = center();

		for(int i = 0; i < this.board.row; i++){
			for(int j = 0; j < this.board.col; j++){
				//System.out.println("["+i+","+j+"]");
				Checker checker = this.board.board.get(i).get(j);
				String color = checker.color;
				if(color == "B"){
					//check if piece is in the back row
					if(backRow(i, this.board.row - 1, color)) blackCount[2]++;
					//check if piece is in the middle of the board
					if(i >= dimensions[0] && i <= dimensions[2])
						if(j >= dimensions[1] && j <= dimensions[3]) blackCount[3]++;  
					//check the number of pieces that can be taken
					/*if(i >0){
						if(j > 0 && j < this.board.col){
						
							System.out.println((i+1)+" "+(i-1)+"\n"+(j+1)+" "+(j-1));

							Checker lowerleft = this.board.board.get(i-1).get(j-1);
							Checker upperright = this.board.board.get(i+1).get(j+1);
							if(lowerleft != null && lowerleft.color == "W" && upperright == null)  blackCount[4]++;
							Checker lowerright = this.board.board.get(i-1).get(j+1);
							Checker upperleft = this.board.board.get(i+1).get(j-1);
							if(lowerright != null && lowerright.color == "W" && upperleft == null) blackCount[4]++;
						}
					}*/	
				}
				if(color == "W"){
					if(backRow(i, this.board.row -1, color)) whiteCount[2]++;
					if(i >= dimensions[0] && i <= dimensions[2])
						if(j >= dimensions[1] && j <= dimensions[3]) whiteCount[4]++;
					/*if(i < this.board.row - 1){
						if(j > 0 && j < this.board.col){

							System.out.println((i+1)+" "+(i-1)+"\n"+(j+1)+" "+(j-1));

                        	Checker lowerleft = this.board.board.get(i-1).get(j-1);
							Checker upperright = this.board.board.get(i+1).get(j+1);
                        	if(upperright != null && upperright.color == "B" && lowerleft == null)  whiteCount[4]++;
                        	Checker lowerright = this.board.board.get(i-1).get(j+1);
                        	Checker upperleft = this.board.board.get(i+1).get(j-1);
                        	if(upperleft != null && upperleft.color == "B" && lowerright == null) whiteCount[4]++;
                    	}
					}*/
				}
			}
		}
		
		int sumBlack = 0; int sumWhite = 0;
		for(int i = 0; i < blackCount.length; i++){
			sumBlack += blackCount[i];
			sumWhite += whiteCount[i];
		}
		
		if(turn == 1) return sumBlack - sumWhite;
		return sumWhite - sumBlack;
	}

}
