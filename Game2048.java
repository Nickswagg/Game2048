package com.codegym.games.game2048;

import com.codegym.engine.cell.*;
import java.util.*;


public class Game2048 extends Game {
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score = 0;
    
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }
    
    private void createGame() {
        gameField = new int[4][4];
        createNewNumber();
        createNewNumber();
    }
    
    private void drawScene() {
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                setCellColoredNumber(y,x, gameField[x][y]);
            }
        }
    }
    
    private void createNewNumber() {
        int cellValue = 1;
        int x = 0;
        int y = 0;
        while (cellValue != 0) {
            x = getRandomNumber(SIDE);
            y = getRandomNumber(SIDE); 
            if (gameField[x][y] == 0) {
                cellValue = 0;
            }
        }   
        int newRandomNum = getRandomNumber(10);
            if (newRandomNum == 9) {
                gameField[x][y] = 4;
            } else {
                gameField[x][y] = 2;
            }
        
        if(getMaxTileValue() == 2048){
          win();  
        }
        
    }
    
    private Color getColorByValue(int value) {
        Color color = null;
        switch (value) {
            case 0:
                color = Color.WHITE;
                break;
            case 2:
                color = Color.BLUE;
                break;
            case 4:
                color = Color.RED;
                break;
            case 8:
                color = Color.CYAN;
                break;
            case 16:
                color = Color.GREEN;
                break;
            case 32:
                color = Color.YELLOW;
                break;
            case 64:
                color = Color.ORANGE;
                break;
            case 128:
                color = Color.PINK;
                break;
            case 256:
                color = Color.MAGENTA;
                break;
            case 512:
                color = Color.BLACK;
                break;
            case 1024:
                color = Color.PURPLE;
                break;
            case 2048:
                color = Color.GRAY;
                break;
        }    
            return color;    
    }
    
    
    
    private void setCellColoredNumber(int y, int x, int value) {
        Color color = getColorByValue(value);
        if (value != 0) {
            setCellValueEx(y, x, color, Integer.toString(value));
        } else{
            setCellValueEx(y,x, color, "");
        }
    }
    
    //Private boolean compressRow(int[row])
    private boolean compressRow(int[] row){
        int temp = 0;
        int[] rowtemp = row.clone(); 
        boolean isChanged = false;
        for(int i = 0; i < row.length; i++) {
            for(int j=0; j < row.length-i-1; j++){
               if(row[j] == 0) {
                  temp = row[j];
                  row[j] = row[j+1];
                  row[j+1] = temp;
               }
            }
        }
        if(!Arrays.equals(row,rowtemp)){
            isChanged = true;
        }
        return isChanged;
    }
    //private boolean mergeRow(int[] row)
    private boolean mergeRow(int[] row){
    boolean moved = false;
    for (int i=0; i< row.length-1;i++)
        if ((row[i] == row[i+1])&&(row[i]!=0)){
            row[i] = 2*row[i];
            row[i+1] = 0;
            score += (row[i] + row[i+1]);
            setScore(score);
            moved = true;
        }
    return moved;
    }
    
    private void moveLeft(){
         //boolean counter = false;
        int move = 0;
        for(int i= 0; i<SIDE; i++) {
            
            boolean compressed = compressRow(gameField[i]); /* row is int[] so we are going for each row matrix,
            first we compress and then we merge, probably we are going to use the values we return*/
            boolean merged = mergeRow(gameField[i]);
            boolean compresses = compressRow(gameField[i]);
            
            if((merged || compressed || compresses)) 
                move++;
            }
            if(move != 0){
                createNewNumber();
            }
    }
    private void moveRight(){
       rotateClockwise();
       rotateClockwise();
       moveLeft();
       rotateClockwise();
       rotateClockwise();
    }
    private void moveUp(){
        
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
     
    }
    private void moveDown(){
       rotateClockwise();
       moveLeft();
       rotateClockwise();
       rotateClockwise();
       rotateClockwise();
       
    }
    
    public void onKeyPress(Key key){
        if(isGameStopped){
            
            if(key == Key.SPACE){
                isGameStopped = false;
                score = 0;
                setScore(score);
                createGame();
                drawScene();
            }
        }
        else if( canUserMove() ){
        
            if (key == Key.LEFT){
            moveLeft();
            }
            else if (key == Key.RIGHT){
            moveRight();
            }
            else if (key == Key.UP){
                moveUp();
            }
            else if (key == Key.DOWN){
            moveDown();
            }
            drawScene();
            }
        else{
            gameOver(); }
    }

    
    private void rotateClockwise() {
        // Traverse each cycle
        for (int i = 0; i < SIDE / 2; i++)
        {
            for (int j = i; j < SIDE - i - 1; j++)
            {
                // Swap elements of each cycle in clockwise direction
                int temp = gameField[i][j];
                gameField[i][j] = gameField[SIDE - 1 - j][i];
                gameField[SIDE - 1 - j][i] = gameField[SIDE - 1 - i][SIDE - 1 - j];
                gameField[SIDE - 1 - i][SIDE - 1 - j] = gameField[j][SIDE - 1 - i];
                gameField[j][SIDE - 1 - i] = temp;
            }
        }
    }
    
    private int getMaxTileValue(){
        int maxValue = Integer.MIN_VALUE;
        for(int i=0; i<gameField.length; i++){
            for(int j=0; j<gameField[i].length; j++){
                if(gameField[i][j] > maxValue){
                    maxValue = gameField[i][j];
                } 
            }
        }
        return maxValue;
    }
    
    private void win(){
        isGameStopped = true;
        showMessageDialog(getColorByValue(2048), "Congratulations", Color.BLUE, 14);
    }
    
    private boolean canUserMove(){
       boolean movesLeft = false;
       //comparison loop, checking for empty cells and if all are full checking for  any duplicates when all spaces are taken.
       for (int i = 0 ; i < SIDE; i++)
           for (int j = 0 ; j<SIDE; j++)
           {
               if (gameField[i][j] == 0)
               {
                   movesLeft = true;
               }
               //this if statement checks if there is an above cell has it got the same value in it.
               if((i-1) > 0 && (gameField[i][j] == gameField[i-1][j]))
               {
                   movesLeft = true;
               }
               //this checks for DOWN
               if ((i+1) < SIDE && (gameField[i][j] == gameField[i+1][j]))
               {
                   movesLeft = true;
               }
               //this checks for RIGHT
               if ((j+1) < SIDE && (gameField[i][j] == gameField[i][j+1]))
               {
                   movesLeft = true;
               }
               //this checks for LEFT
               if ((j-1)>0 && (gameField[i][j] == gameField[i][j-1]))
               {
                   movesLeft = true;
               }
        }
        return movesLeft;
        
    }
    
    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(getColorByValue(2048), "Game Over", Color.RED, 14);
    }
    
    
}