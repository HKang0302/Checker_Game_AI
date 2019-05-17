import copy
import re
from Move import Move
class InvalidMoveError(Exception):
    pass

class InvalidParameterError(Exception):
    pass

import Checker

class Board:
    opponent = {"W": "B", "B": "W"}
    def __init__(self, row, col, k):
        self.row = row
        self.col = col
        self.k = k
        self.board = []
        for row in range(self.row):
            self.board.append([])
            for col in range(self.col):
                self.board[row].append(Checker.Checker(".", [row, col]))

        self.black_count = 0
        self.white_count = 0

    def initialize_game(self):
        self.check_initial_variable()
        for i in range(0, self.k):
            for j in range(0, self.col):
                if i%2 != 0:
                    if j%2 == 0:
                        # self.board[i][j] = "B"
                        self.board[i][j] = Checker.Checker("B", [i, j])
                        self.black_count += 1
                        self.board[self.row - self.k + i][j] = "W"
                        self.board[self.row - self.k + i][j] = Checker.Checker("W", [self.row - self.k + i, j])
                        self.white_count += 1
                elif i%2 == 0:
                    if j%2 !=0:
                        # self.board[i][j] = "B"
                        self.board[i][j] = Checker.Checker("B", [i, j])
                        self.black_count += 1
                        self.board[self.row - self.k + i][j] = "W"
                        self.board[self.row - self.k + i][j] = Checker.Checker("W", [self.row - self.k + i, j])
                        self.white_count += 1




    def make_move(self, move, turn):
        """

        :param move: Move object
        :param turn: str or int
        :return:
        """
        if type(turn) is int:
            if turn == 1:
                turn = 'B'
            elif turn == 2:
                turn = 'W'
            else:
                raise InvalidMoveError
        move_list = move.seq
        move_to_check = []
        ultimate_start = move_list[0]
        ultimate_end = move_list[-1]
        past_positions = [ultimate_start]
        capture_positions = []
        for i in range(len(move_list)-1):
            move_to_check.append((move_list[i],move_list[i + 1]))
        # e.g move = Move((0,0)-(2,2)-(0,4))
        #     move_to_check = [((0,0),(2,2)),((2,2),(0,4))]
        if_capture = False
        for t in range(len(move_to_check)):
            start = move_to_check[t][0] # e.g. (0,0)
            target= move_to_check[t][1] # e.g. (2,2)
            if self.is_valid_move(start[0],start[1],target[0],target[1],turn) or (if_capture and abs(start[0]-target[0]) == 1):
                # invailid move or attempting to make a single move after capture
                self.board[start[0]][start[1]].color = "."
                self.board[target[0]][target[1]].color = turn
                self.board[target[0]][target[1]].is_king = self.board[start[0]][start[1]].is_king
                self.board[start[0]][start[1]].become_man()
                past_positions.append(target)
                if abs(start[0]-target[0]) == 2:
                    # capture happened
                    if_capture = True
                    capture_position = ((start[0] + (target[0]-start[0])//2), (start[1] + (target[1]-start[1])//2))
                    # calculate capture position
                    capture_positions.append(capture_position)
                    # record capture position
                    self.board[capture_position[0]][capture_position[1]] = Checker.Checker(".", [capture_position[0], capture_position[1]])
                    # capture
                if (turn == 'B' and target[0] == self.row - 1):
                    self.board[target[0]][target[1]].become_king()
                elif (turn == 'W' and target[0] == 0):
                    self.board[target[0]][target[1]].become_king()

            else:
                for failed_capture in capture_positions:
                    # recover failed captures
                    self.board[failed_capture[0]][failed_capture[1]] = Checker.Checker(self.opponent[turn],[failed_capture[0],failed_capture[1]])
                for failed_position in past_positions:
                    # recover failed moves
                    self.board[failed_position[0]][failed_position[1]] = Checker.Checker(".", [failed_position[0],failed_position[1]])
                self.board[ultimate_start[0]][ultimate_start[1]] = Checker.Checker(turn, [ultimate_start[0],ultimate_start[1]])
                print(move,turn)
                raise InvalidMoveError


    def is_in_board(self,pos_x,pos_y):
        return pos_x >= 0 and pos_x < self.row and pos_y >= 0 and pos_y < self.col

    def is_valid_move(self, chess_row, chess_col, target_row, target_col, turn):

        if target_row < 0 or target_row >= self.row or target_col < 0 or target_col >= self.col:
        # move out of the board
            return False
        if not self.board[target_row][target_col].color == ".":
        # target position is not empty
            return False
        if not self.board[chess_row][chess_col].color == turn:
        # there must be a chessman at that position
            return False
        diff_col = target_col - chess_col # difference in col number
        diff_row = target_row - chess_row # difference in row number
        if abs(diff_col) != abs(diff_row):
        # each move has to be 1,1 or 2,2
            return False
        chess_being_moved = self.board[chess_row][chess_col]
        if diff_row == 1 and diff_col == 1 : # down, right: king, black
            return chess_being_moved.is_king or chess_being_moved.color == "B"
        if diff_row == 1 and diff_col == -1 : # down, left: king, black
            return chess_being_moved.is_king or chess_being_moved.color == "B"
        if diff_row == -1 and diff_col == 1 : # up, right: king, red
            return chess_being_moved.is_king or chess_being_moved.color == "W"
        if diff_row == -1 and diff_col == -1 : # up, left: king, red
            return chess_being_moved.is_king or chess_being_moved.color == "W"
        if diff_row == 2 and diff_col == 2: # down, right, capture: king, black
            return (chess_being_moved.is_king or chess_being_moved.color == "B") and self.board[chess_row + 1][chess_col + 1].color != turn and self.board[chess_row + 1][chess_col + 1].color != "."
        if diff_row == 2 and diff_col == -2: # down, left, capture: king, black
            return (chess_being_moved.is_king or chess_being_moved.color == "B") and self.board[chess_row + 1][chess_col - 1].color != turn and self.board[chess_row + 1][chess_col - 1].color != "."
        if diff_row == -2 and diff_col == 2:  # up, right, capture: king, red
            return (chess_being_moved.is_king or chess_being_moved.color == "W") and self.board[chess_row - 1][chess_col + 1].color != turn and self.board[chess_row - 1][chess_col + 1].color != "."
        if diff_row == -2 and diff_col == -2:  # up, left, capture: king, red
            return (chess_being_moved.is_king or chess_being_moved.color == "W") and self.board[chess_row - 1][chess_col - 1].color != turn and self.board[chess_row - 1][chess_col - 1].color != "."
        return False




    def get_all_possible_moves(self,color):
        result = []
        if type(color) is int:
            if color == 1:
                color = 'B'
            elif color == 2:
                color = 'W'
        is_capture = False
        temp = 0
        for row in range(self.row):
            for col in range(self.col):
                checker = self.board[row][col]
                if checker.color == color:
                    moves,is_capture = checker.get_possible_moves(self)
                    if temp == 0 and not is_capture:
                        if moves:
                            result.append(moves)
                    elif temp == 0 and is_capture:
                        result = []
                        temp = 1
                        if moves:
                            result.append(moves)
                    elif temp == 1 and is_capture:
                        if moves:
                            result.append(moves)
        # if is_capture:
        #     for moves in result:
        #         for move in moves:
        #             if len(move.seq) <= 2:
        #                 moves.remove(move)


        return result

    def is_win(self):
        W = True
        B = True
        for row in range(self.row):
            for col in range(self.col):
                checker = self.board[row][col]
                if checker.color == 'W':
                    W = False
                elif checker.color == 'B':
                    B = False
                if not W and not B:
                    return 0
        if W:
            return 1
        elif B:
            return 2
        else:
            return 0

    def show_board(self,fh=None):
        print("   ",end="",file=fh)
        print(*range(0,self.col),sep="  ",file=fh)
        # index for debugging easily
        for i, row in enumerate(self.board):
            print(i, end="")
            # index for debugging easily
            for j, col in enumerate(row):
                king = self.board[i][j].is_king
                if king:
                    print("%3s" % str(self.board[i][j].get_color()).upper(), end="",file=fh)
                else:
                    print("%3s" % str(self.board[i][j].get_color()).lower(), end = "",file=fh)
                # print("%3s" % str(self.board[i][j]), end="")
            print()
        print('----------------------',file=fh)

    def check_initial_variable(self):
        # Recently changed: return false is changed to raising exceptions
        # Q > 0
        if self.row - 2 * self.k <= 0:
            raise  InvalidParameterError("Q <= 0")
        # M = 2P + Q
        elif self.row != 2 * self.k + (self.row - 2 * self.k):
            raise InvalidParameterError("M != 2P + Q")
        # N*P is even
        elif self.col * self.k % 2 != 0:
            raise InvalidParameterError("N*P is odd")

    def save_board(self): #print board to file. Maybe can be implemented in show_board
        pass




if __name__ == "__main__":


    # some simple tests
    print(Move.from_str('(0,0)-(2,2)-(0,4)'))
    b=Board(10,10,2)
    b.board[4][3] = Checker.Checker("W", [4, 3])
    b.board[4][5] = Checker.Checker("W", [4, 5])
    b.board[6][3] = Checker.Checker("W", [6, 3])
    b.board[6][5] = Checker.Checker("W", [6, 5])
    b.board[5][4] = Checker.Checker("B", [5, 4])
    b.board[2][7] = Checker.Checker("W", [2, 7])
    b.board[2][5] = Checker.Checker("W", [2, 5])
    b.board[2][3] = Checker.Checker("W", [2, 3])

    b.board[5][4].become_king()
    b.show_board()
    print(b.get_all_possible_moves("B"))