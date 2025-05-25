# JavaChess
Chess game created through the java GUI system

I created the project and ran this code through the eclipse IDE and it works fine.
You can download eclipse through this site: https://www.eclipse.org/ide/
Then, you should clone this repository into eclipse and also select a directory location.

Alternatively, you could also use visual studio code as an IDE by cloning the repository and copy and pasting the link there, you may have to install a couple configurations so follow the ide's instructions on what to install from there. Download VS code through here: https://code.visualstudio.com/

I tried to run it on terminal as well but it doesn't work for some reason. It might be due to the file placement, since it gives me errors that it can't find certain files that I coded in other files of the project.

Once you get the files up in your ide, simply run the program with no command line arguments and a new popup window should appear with the java symbol, that is the main chess board.

The output console will also show what a standardized chessboard should look like in key characters and also be calculating legal moves.

On the top of the window, there is a menu bar which has different functions.

The file menu should display Load PNG, which should put in a saved board (This is still being made).
It should also display exit, which simply closes the game and window.

In preferences, it shows flip board, which just flips the game board
Flip board after each move, which flips the board after each move, making it easier for two humans to play on the same computer.
And highlight legal moves, which would let the user know through green dots where a selected piece can move and also red borders around enemy pieces, which shows what pieces the selected piece can attack.

Finally, in the options menu, there are three choices:
1. New game: this simply creates a new game to be played and resets the board back to the standardized board.
2. Undo last move: this would just make the board go back to what it looked like one move before, in case a player did a mistake and would like to undo his mistake.
3. Set up game: This brings up another small window and this is the function that brings in the AI portion of the chess board, you simply would have to select which player is human and which player is a computer, or you could have it set up as player vs player or computer vs computer. There is a scroll bar called search depth, which means that the higher the depth you set, the harder the difficulty is set into the computer players. If you were to set the depth at 3, for example, the computer is essentially thinking 3 moves ahead in the game. HOWEVER, it is strongly recommended that you keep the search depth below 4 because as the game progresses, it takes the AI much longer to process and do a move due to the amount of mobility and deep moves it has to check.
Also, if you do try to make it computer vs computer, don't expect the game to go so far, because eventually, both AI will just do the same move over and over again.

That is it for the menu bar on top. Moving on, on the right side of the board, you will notice that it gives you the game history in algebraic notation. On the left side is the taken pieces panel, which shows what pieces have been taken throughout the game.

That is essentially all to talk about, have fun playing, whether with the computer or with someone else!
