package engine;

import characters.Character;
import mechanics.Attack;
import mechanics.Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Scariottis on 21/03/2017.
 */
public class Grid {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[47m";

    private int turnCounter = 0;

    private ArrayList<Character> turnSequence = new ArrayList<Character>();
    private Character currentCharacter;
    private ArrayList<Character> teamA = new ArrayList<Character>();
    private ArrayList<Character> teamB = new ArrayList<Character>();
    private Square[][] gridSquares;
    
    public ArrayList<Letter> mailBoxA = new ArrayList<Letter>();
    public ArrayList<Letter> mailBoxB = new ArrayList<Letter>();


	public Grid(ArrayList<Character> teamA, ArrayList<Character> teamB, int dimensionX, int dimensionY, int[][] mapArray){
        this.teamA = teamA;
        this.teamB = teamB;
        gridSquares = new Square[dimensionX][dimensionY];

        generateGrid(dimensionX, dimensionY, mapArray);
    }

    public ArrayList<Letter> getMailBoxA() {
		return mailBoxA;
	}

	public ArrayList<Letter> getMailBoxB() {
		return mailBoxB;
	}
	
	public void generateGrid(int dimensionX, int dimensionY, int[][] mapArray){
        Square sq;
        //Generate engine.Grid using dimensions
        for(int i = 0; i < dimensionX; i++){
            for(int j = 0; j < dimensionY; j++){
                if(mapArray[i][j] == 1){ //Has passage
                    sq = new Square(i, j, 1);
                }else{                  // Hasn't passage
                    sq = new Square(i, j, 0);
                }
                gridSquares[i][j] = sq;
            }
        }

        //Set Team A Characters on Squares
        for(int i = 0; i < this.teamA.size(); i++){
            int x = this.teamA.get(i).getPositionX();
            int y = this.teamA.get(i).getPositionY();

            gridSquares[x][y].setCharacter(teamA.get(i));
        }
        //Set Team B Characters on Squares
        for(int i = 0; i < this.teamB.size(); i++){
            int x = this.teamB.get(i).getPositionX();
            int y = this.teamB.get(i).getPositionY();

            gridSquares[x][y].setCharacter(teamB.get(i));
        }
    }


    public void printTeams(){
        System.out.println("Team " + ANSI_BLUE + "A" + ANSI_RESET + ":");
        for(int i = 0; i < this.teamA.size(); i++){
            System.out.println((i+1) + ": " + this.teamA.get(i).getName() + ", " + this.teamA.get(i).characterRole());
        }

        System.out.println("Team " + ANSI_RED + "B" + ANSI_RESET + ":");
        for(int i = 0; i < this.teamB.size(); i++){
            System.out.println((i+1) + ": " + this.teamB.get(i).getName() + ", " + this.teamA.get(i).characterRole());
        }
        System.out.println("");
    }

    public String searchTeam(String charName){
        for(int i = 0; i < this.teamA.size(); i++){
            if(this.teamA.get(i).getName().equals(charName)){
                return ANSI_BLUE + "A" + (i+1) + ANSI_RESET;
            }
        }

        for(int i = 0; i < this.teamB.size(); i++){
            if(this.teamB.get(i).getName().equals(charName)){
                return ANSI_RED + "B" + (i+1) + ANSI_RESET;
            }
        }

        return "Not Found";
    }

    public void printGrid(){
        System.out.println("Turn " + this.turnCounter);
        for(int i = 0; i < this.gridSquares.length; i++){
            for(int j = 0; j < this.gridSquares[0].length; j++){
                if(gridSquares[i][j].getMapFlag() == 1 || (gridSquares[i][j].getMapFlag() == 0 && gridSquares[i][j].getC()!=null)){
                    if(gridSquares[i][j].getC() == null){
                        System.out.print("    |");
                    }else{
                        System.out.print(" " + this.searchTeam(gridSquares[i][j].getC().getName()) + " |");
                    }
                }else{
                    System.out.print(ANSI_BLACK_BACKGROUND + "     " + ANSI_RESET);
                }
            }
            System.out.println("");
            for(int k = 0; k < this.gridSquares[0].length; k++){
                System.out.print("-----");
            }
            System.out.println("");
        }
        this.turnCounter++;
    }

    public boolean verifyVictory(ArrayList<Character> team){
        /*for(int i = 0; i < team.size(); i++){
            if(team.get(i).getHp()>=0){
                return false;
            }
        }*/
    	if (team.isEmpty()){
    		return true;
    	}
        return false;
    }

    public void callTurn(){
    	if(this.currentCharacter.getHp() >= 0){
    		System.out.println(this.currentCharacter.getName()+" ira jogar");
            this.currentCharacter.play();
            int i = this.turnSequence.indexOf(this.currentCharacter);
            if((i+1) >= this.turnSequence.size()){
                this.currentCharacter = this.turnSequence.get(0); //New Round
            }else{
                this.currentCharacter = this.turnSequence.get(i+1);
            }
    	} else{
    		if(this.currentCharacter.getTeam() == 'a'){
    			this.teamA.remove(this.currentCharacter);
    		} else{
    			this.teamB.remove(this.currentCharacter);
    		}
    		this.gridSquares[this.currentCharacter.getPositionX()][this.currentCharacter.getPositionY()].leaveSquare();
    		this.gridSquares[this.currentCharacter.getPositionX()][this.currentCharacter.getPositionY()].setMapFlag(1);
    		this.turnSequence.remove(this.currentCharacter);
    		int i = this.turnSequence.indexOf(this.currentCharacter);
    		if((i+1) >= this.turnSequence.size()){
                this.currentCharacter = this.turnSequence.get(0); //New Round
            }else{
                this.currentCharacter = this.turnSequence.get(i+1);
            }
    	}
    }

    public static int diceRoller(int dicesQuantity, int numberOfFaces, int bonus){
        Random roller = new Random();
        int total = 0;

        for(int i = 0; i < dicesQuantity; i ++){
            total += roller.nextInt(numberOfFaces) + 1; //The method starts at 0, so add 1 to the roll
        }

        return total += bonus;
    }

    public void rollCharactersInitiative(){
        int initRoll = 0;
        int initArray[] = new int[teamA.size()+teamB.size()];
        ArrayList<Character> charsOrder = new ArrayList<Character>();
        for(int i = 0; i < this.teamA.size(); i++){
            initRoll = diceRoller(1, 20, this.teamA.get(i).getInit());
            initArray[i] = initRoll;
            charsOrder.add(teamA.get(i));
        }
        for(int i = 0; i < teamB.size(); i++){
            initRoll = diceRoller(1, 20, this.teamB.get(i).getInit());
            initArray[i+(teamA.size())] = initRoll;
            charsOrder.add(teamB.get(i));
        }

        int aux;

        for(int i = 0; i < initArray.length; i++){
            for(int j = 0; j < initArray.length; j++){
                if(initArray[i] > initArray[j]){
                    aux = initArray[i];
                    initArray[i] = initArray[j];
                    initArray[j] = aux;

                    Collections.swap(charsOrder, i, j);
                }
            }
        }

        System.out.println("Turn Order:");
        this.turnSequence = (ArrayList<Character>)charsOrder.clone();
        for(int i = 0; i < initArray.length; i++){
            System.out.println(turnSequence.get(i).getName() + " (" + initArray[i] + ")");
        }
        System.out.println("");

        this.currentCharacter = this.turnSequence.get(0);
    }

    public static double distance(Character current, Character target)
    {
        return Math.hypot(current.getPositionX()-target.getPositionX(), current.getPositionY()-current.getPositionY());
    }

    private void cleanVision(Character current, Character target) {
        int startX, startY, endX, endY;
        if(current.getPositionX()<=target.getPositionX())
        {
            startX = current.getPositionX();
            endX = target.getPositionX();
        }
        else
        {
            endX = current.getPositionX();
            startX = target.getPositionX();
        }


        if(current.getPositionY()<=target.getPositionY())
        {
            startY = current.getPositionY();
            endY = target.getPositionY();
        }
        else
        {
            endY = current.getPositionY();
            startY = target.getPositionY();
        }

        for(int i=startX;i<=endX;i++)
        {
            for(int j = startY;j<=endY;j++)
            {
                gridSquares[i][j].setVisible(true);
            }
        }

    }

    private void calculateVision(Character current, Character target)
    {
        //determina as dimensões do retangulo que cobre a origem e o destino do ataque.
        int startX, startY, endX, endY;
        if(current.getPositionX()<=target.getPositionX())
        {
            startX = current.getPositionX();
            endX = target.getPositionX();
        }
        else
        {
            endX = current.getPositionX();
            startX = target.getPositionX();
        }


        if(current.getPositionY()<=target.getPositionY())
        {
            startY = current.getPositionY();
            endY = target.getPositionY();
        }
        else
        {
            endY = current.getPositionY();
            startY = target.getPositionY();
        }

        //percorre os quadrados do retangulo e, caso seja uma parede, determina quais quadrados não estão visiveis para o personagem current.
        for(int i=startX;i<=endX;i++)
        {
            for(int j = startY;j<=endY;j++)
            {
                if(gridSquares[i][j].getMapFlag() == 0)
                {
                    calculateCover(gridSquares[i][j],current,startX, startY, endX, endY);
                }
            }
        }
    }

    private void calculateCover(Square wall, Character current, int startX, int startY, int endX, int endY) {
        int x;
        int y;
        //se a parede fizer uma reta horizontal ou vertical com o current,obstruirá a linha/coluna atràs da parede e as linhas/colunas adjacentes.
        if(directLine(wall,current))
        {
            if(wall.getX() == current.getPositionX())
            {
                //parede ao sul do personagem.
                if(wall.getY()<current.getPositionY())
                {
                    x = wall.getX();
                    y = wall.getY()-1;
                    while(y>=startY)
                    {
                        gridSquares[x][y].setVisible(false);
                        if(x-1>=startX)
                        {
                            gridSquares[x-1][y].setVisible(false);
                        }
                        if(x+1<=endX)
                        {
                            gridSquares[x+1][y].setVisible(false);
                        }
                        y--;
                    }
                }
                //parede ao norte do personagem.
                else
                {
                    x = wall.getX();
                    y = wall.getY()+1;
                    while(y<=endY)
                    {
                        gridSquares[x][y].setVisible(false);
                        if(x-1>=startX)
                        {
                            gridSquares[x-1][y].setVisible(false);
                        }
                        if(x+1<=endX)
                        {
                            gridSquares[x+1][y].setVisible(false);
                        }
                        y++;
                    }
                }
            }
            else
            {
                //parede ao oeste do personagem.
                if(wall.getX()<current.getPositionX())
                {
                    x = wall.getX()-1;
                    y = wall.getY();
                    while(x>=startX)
                    {
                        gridSquares[x][y].setVisible(false);
                        if(y-1>=startY)
                        {
                            gridSquares[x][y-1].setVisible(false);
                        }
                        if(y+1<=endY)
                        {
                            gridSquares[x][y+1].setVisible(false);
                        }
                        x--;
                    }
                }
                //parede ao leste do personagem
                else
                {
                    x = wall.getX()+1;
                    y = wall.getY();
                    while(x<=endX)
                    {
                        gridSquares[x][y].setVisible(false);
                        if(y-1>=startY)
                        {
                            gridSquares[x][y-1].setVisible(false);
                        }
                        if(y+1<=endY)
                        {
                            gridSquares[x][y+1].setVisible(false);
                        }
                        x++;
                    }
                }
            }
        }
        //parede está em uma das diagonais do personagem current.
        if(diagonalLine(wall,current))
        {
            if(wall.getX()<current.getPositionX())
            {
                //diagonal noroeste
                if(wall.getY()>current.getPositionY())
                {
                    x=wall.getX()-1;
                    y=wall.getY()+1;
                    while((x>=startX)&&(y<=endY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x][y-1].setVisible(false);
                        gridSquares[x+1][y].setVisible(false);
                        x--;
                        y++;
                    }
                }
                //diagonal sudoeste
                else
                {
                    x=wall.getX()-1;
                    y=wall.getY()-1;
                    while((x>=startX)&&(y>=startY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x][y+1].setVisible(false);
                        gridSquares[x+1][y].setVisible(false);
                        x--;
                        y--;
                    }
                }
            }
            else
            {
                //diagonal nordeste
                if(wall.getY()>current.getPositionY())
                {
                    x=wall.getX()+1;
                    y=wall.getY()+1;
                    while((x<=endX)&&(y<=endY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x][y-1].setVisible(false);
                        gridSquares[x-1][y].setVisible(false);
                        x++;
                        y++;
                    }
                }
                //diagonal sudeste.
                else
                {
                    x=wall.getX()+1;
                    y=wall.getY()-1;
                    while((x<=endX)&&(y>=startY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x][y+1].setVisible(false);
                        gridSquares[x-1][y].setVisible(false);
                        x++;
                        y--;
                    }
                }
            }
        }
        //parede não está na vertical, horizontal, ou diagonal em relação ao current.
        else
        {
            //sudoeste
            if((wall.getX()<current.getPositionX()) && (wall.getY()<current.getPositionY()))
            {
                //abaixo da diagonal de current
                if((current.getPositionX()-wall.getX())<(current.getPositionY()-wall.getY()))
                {
                    x=wall.getX()-1;
                    y=wall.getY()-1;
                    while((x>=startX)&&(y>=startY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x+1][y].setVisible(false);
                        x--;
                        y--;
                    }
                }
                //acima da diagonal de current
                else
                {
                    x=wall.getX()-1;
                    y=wall.getY()-1;
                    while((x>=startX)&&(y>=startY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x][y+1].setVisible(false);
                        x--;
                        y--;
                    }
                }
            }
            //noroeste
            if((wall.getX()<current.getPositionX())&&(wall.getY()>current.getPositionY()))
            {
                //acima da diagonal de current
                if((current.getPositionX()-wall.getX())<(wall.getY()-current.getPositionY()))
                {
                    x=wall.getX()-1;
                    y=wall.getY()+1;
                    while((x>=startX)&&(y<=endY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x+1][y].setVisible(false);
                        x--;
                        y++;
                    }
                }
                //abaixo da diagonal de current
                else
                {
                    x=wall.getX()-1;
                    y=wall.getY()+1;
                    while((x>=startX)&&(y<=endY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x][y-1].setVisible(false);
                        x--;
                        y++;
                    }
                }
            }
            //nordeste
            if((wall.getX()>current.getPositionX())&&(wall.getY()>current.getPositionY()))
            {
                //acima da diagonal de current
                if((wall.getX()-current.getPositionX())<(wall.getY()-current.getPositionY()))
                {
                    x=wall.getX()+1;
                    y=wall.getY()+1;
                    while((x<=endX)&&(y<=endY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x-1][y].setVisible(false);
                        x++;
                        y++;
                    }
                }
                //abaixo da diagonal de current
                else
                {
                    x=wall.getX()+1;
                    y=wall.getY()+1;
                    while((x+1<=endX)&&(y<=endY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x+1][y].setVisible(false);
                        x++;
                        y++;
                    }
                }
            }
            //sudeste
            else
            {
                //acima da diagonal de current
                if((wall.getX()-current.getPositionX())>(current.getPositionY()-wall.getY()))
                {
                    x=wall.getX()+1;
                    y=wall.getY()-1;
                    while((x<=endX)&&(y>=startY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x][y+1].setVisible(false);
                        x++;
                        y--;
                    }
                }
                //abaixo da diagonal de current
                else
                {
                    x=wall.getX()+1;
                    y=wall.getY()-1;
                    while((x<=endX)&&(y>=startY))
                    {
                        gridSquares[x][y].setVisible(false);
                        gridSquares[x-1][y].setVisible(false);
                        x++;
                        y--;
                    }
                }
            }
        }

    }

    //se |Xc-Xw| = |Yc-YW|, wall e current estão em uma diagonal.
    private boolean diagonalLine(Square wall, Character current) {
        if(Math.abs(wall.getX()-current.getPositionX()) == Math.abs(wall.getY()-current.getPositionY()))
        {
            return true;
        }
        else{
            return false;
        }
    }

    //se wall e current possuem o mesmo x ou y.
    private boolean directLine(Square square, Character current) {
        if((square.getX()==current.getPositionX()) || (square.getY()==current.getPositionY()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isInRange(Character current, Character target, Attack atk){
        //se o alvo estiver fora do alcance
        if(distance(current,target) > atk.getRange())
        {
            return false;
        }
        //se o alvo estiver dentro do alcance, verificar se nenhuma parede obstrui a visão.
        else
        {
            this.calculateVision(current,target);

            //se nenhuma parede obstruir a visão, retorne verdadeiro.
            if(this.gridSquares[target.getPositionX()][target.getPositionY()].getVisible())
            {
                //retorna o atributo visibility das casa para true.
                this.cleanVision(current,target);
                return true;
            }
            else
            {
                return false;
            }

        }

    }

    public boolean isInRange(Character current, Character target, Spell mgc){
        //se o alvo estiver fora do alcance
        if(distance(current,target) > mgc.getRange())
        {
            return false;
        }
        //se o alvo estiver dentro do alcance, verificar se nenhuma parede obstrui a visão.
        else
        {
            this.calculateVision(current,target);

            //se nenhuma parede obstruir a visão, retorne verdadeiro.
            if(gridSquares[target.getPositionX()][target.getPositionY()].getVisible())
            {
                //retorna o atributo visibility das casa para true.
                this.cleanVision(current,target);
                return true;
            }
            else
            {
                return false;
            }

        }

    }

	
    public Character getCurrentCharacter() {
		return this.currentCharacter;
	}
    
    public Square[][] getGridSquares() {
		return this.gridSquares;
	}
    
    public ArrayList<Character> getTeamA() {
		return this.teamA;
	}
    
    public ArrayList<Character> getTeamB() {
		return this.teamB;
	}
}
