package characters;

import mechanics.Attack;
import mechanics.Spell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import engine.Grid;
import engine.Square;
import java.util.Scanner;

import static engine.Grid.diceRoller;
import static engine.Simulator.grid;

public abstract class Character {
	
	private String name;
	private int role, hp, ac, init, positionX, positionY, hpMax, condition = 1; //Role: 1 - Tanker, 2 - M. DPS, 3 - R. DPS, 4 - Support
	private char size;									//Conditions: 1 - healthy, 2 - wounded, 3 - badly wounded, 4 - DEAD
	private char team;
	private int speed, fort, ref, will;
	private int[] availableActions = new int[3];
	
	
	//Lista de ataques
	protected ArrayList<Attack> attacks;
	//Lista de spells
	protected ArrayList<Spell> spells;

	public Character(String name, int role, int hp, int ac, int init, char size, int speed, int fort, int ref, int will,
                     ArrayList<Attack> attacks, ArrayList<Spell> spells) {
		this.name = name;
		this.role = role;
		this.hp = hp;
		this.hpMax = hp;
		this.ac = ac;
		this.init = init;
		this.size = size;
		this.speed = speed;
		this.fort = fort;
		this.ref = ref;
		this.will = will;
		this.attacks = attacks;
		this.spells = spells;
		
		this.availableActions[0] = 1; //uma acao full-round
		this.availableActions[1] = 2; //duas standard
		this.availableActions[2] = 1; //uma swift
	}
	
	public char getTeam() {
		return team;
	}

	public void setTeam(char team) {
		this.team = team;
	}

	

	public void printCharacterSheet(){
		System.out.println("Name: " + this.name + "\n" +
				 		   "Role: " + this.role + "\n" +
				           "HP: " + this.hp + "\n" +
						   "AC: " + this.ac + "\n" +
					       "Init: " + this.init + "\n" +
						   "Size: " + this.size + "\n" +
						   "Speed: " + this.speed + "\n\n" +
						   "Fort: " + this.fort + "\n" +
						   "Refl: " + this.ref + "\n" +
						   "Will: " + this.will + "\n");

		System.out.println("Attacks: ");
		for(int i = 0; i < attacks.size(); i++){
			attacks.get(i).printAttack();
		}
		System.out.println("Spells: ");
		for(int i = 0; i < spells.size(); i++){
			spells.get(i).printSpell();
		}
	}
	
	public abstract void play();

	public String getName(){return this.name;}
	public int getHpMax() {return hpMax;}
	public int getPositionX() {return positionX;}
	public int getPositionY() {return positionY;}
	public int getCondition() {return condition;}

	public void setInitialPosition(int x, int y){
		this.positionX = x;
		this.positionY = y;
	}

	public String characterRole(){
		switch (this.role){
			case 1:
				return "Tanker";
			case 2:
				return "Melee DPS";
			case 3:
				return "Ranged DPS";
			case 4:
				return "Support";
		}

		return "Error";
	}

	public void setCondition(){
		if(this.hp >= this.hpMax*0.7){
			this.condition = 1;
		}else if(this.hpMax*0.7 > this.hp && this.hp >= this.hpMax*0.4){
			this.condition = 2;
		}else if(this.hpMax*0.4 > this.hp && this.hp > 0){
			this.condition = 3;
		}else{
			this.condition = 4;
		}
	}

	public int getHp() {return hp;}

	public void setHp(int hp) {this.hp = hp;}

	public int getInit() {return init;}

	public int getFort() {return fort;}

	public int getRef() {return ref;}

	public int getWill() {return will;}

	public int getRole(){return this.role;}
	
	public int[][] aStar(int xInitial, int yInitial){
		int xCurrent, yCurrent;
		Square[][] square = grid.getGridSquares();
		int[][] a;
		a = new int[square.length][square[0].length];
		ArrayList<Character> allies = grid.getTeamA();
		ArrayList<Character> enemies = grid.getTeamB();
		Scanner scanner = new Scanner(System.in);
		
		for(int i = 0; i < square.length; i++){
			for(int j = 0; j < square[0].length; j++){
				if(square[i][j].getMapFlag() == 1){
					a[i][j] = 0;
				}else{
					a[i][j] = -1;
				}
			}
		}
		
		for(int i = 0; i < grid.getTeamA().size(); i++){
			if(grid.getTeamA().get(i).getName().equals(grid.getCurrentCharacter().getName())){
				
				allies = grid.getTeamA();
				enemies = grid.getTeamB();
				break;
			}
		}
		
		for(int i = 0; i < enemies.size(); i++){
			a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()] = -1;
		}
		
		for(int i = 0; i < allies.size(); i++){
			a[allies.get(i).getPositionX()][allies.get(i).getPositionY()] = -1;
		}
		
		xCurrent = xInitial;
		yCurrent = yInitial;
		
		Queue<Square> queue = new LinkedList<Square>();
		queue.add(new Square(xInitial, yInitial,1));
		
		while(!queue.isEmpty()){
			
			Square aux = queue.poll();
			xCurrent = aux.getX();
			yCurrent = aux.getY();
			a[xInitial][yInitial] = 0;
			
			if(((xCurrent - 1) > -1) && (yCurrent + 1) < a[0].length){
				
				if(a[xCurrent - 1][yCurrent + 1] == 0){
					
					a[xCurrent -1][yCurrent + 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent - 1, yCurrent + 1, 1));
					
				}
			}if(((xCurrent - 1) > -1) && (yCurrent - 1) > -1){
				if(a[xCurrent - 1][yCurrent - 1] == 0){
					
					a[xCurrent - 1][yCurrent - 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent - 1, yCurrent - 1, 1));
					
					
				}
				
			}if(((xCurrent - 1) > -1)){
				if(a[xCurrent - 1][yCurrent] == 0){
					
					a[xCurrent -1][yCurrent] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent - 1, yCurrent, 1));
				
					
				}
				
			}if(((xCurrent + 1) < a.length) && ((yCurrent + 1) < a[0].length)){
				if(a[xCurrent + 1][yCurrent + 1] == 0){
					
					a[xCurrent + 1][yCurrent + 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent + 1, yCurrent + 1, 1));
					
				}
			}if(((xCurrent + 1) < a.length) && ((yCurrent - 1) > -1)){
				if(a[xCurrent + 1][yCurrent - 1] == 0){
					
					a[xCurrent +1][yCurrent - 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent + 1, yCurrent - 1, 1));
					
				}
			}if(((xCurrent + 1) < a.length)){
				if(a[xCurrent + 1][yCurrent] == 0){
					
					a[xCurrent + 1][yCurrent] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent + 1, yCurrent, 1));
					
				}
			}if((yCurrent - 1) > -1){
				if(a[xCurrent][yCurrent - 1] == 0){
					
					a[xCurrent][yCurrent - 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent, yCurrent - 1, 1));
					
				}
			}if((yCurrent + 1) < a[0].length){
				if(a[xCurrent][yCurrent + 1] == 0){
					
					a[xCurrent][yCurrent + 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent, yCurrent + 1, 1));
					
				}
			}
			
			a[xInitial][yInitial] = 0;
			
		}
		
		return a;
	}
	
	public int[][] aStarGA(int xInitial, int yInitial,Grid testGrid){
		int xCurrent, yCurrent;
		Square[][] square = testGrid.getGridSquares();
		int[][] a;
		a = new int[square.length][square[0].length];
		ArrayList<Character> allies = testGrid.getTeamA();
		ArrayList<Character> enemies = testGrid.getTeamB();
		Scanner scanner = new Scanner(System.in);
		
		for(int i = 0; i < square.length; i++){
			for(int j = 0; j < square[0].length; j++){
				if(square[i][j].getMapFlag() == 1){
					a[i][j] = 0;
				}else{
					a[i][j] = -1;
				}
			}
		}
		
		
		allies = testGrid.getTeamA();
		enemies = testGrid.getTeamB();
		
		for(int i = 0; i < enemies.size(); i++){
			a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()] = -1;
		}
		
		for(int i = 0; i < allies.size(); i++){
			a[allies.get(i).getPositionX()][allies.get(i).getPositionY()] = -1;
		}
		
		xCurrent = xInitial;
		yCurrent = yInitial;
		
		Queue<Square> queue = new LinkedList<Square>();
		queue.add(new Square(xInitial, yInitial,1));
		
		while(!queue.isEmpty()){
			
			Square aux = queue.poll();
			xCurrent = aux.getX();
			yCurrent = aux.getY();
			a[xInitial][yInitial] = 0;
			
			if(((xCurrent - 1) > -1) && (yCurrent + 1) < a[0].length){
				
				if(a[xCurrent - 1][yCurrent + 1] == 0){
					
					a[xCurrent -1][yCurrent + 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent - 1, yCurrent + 1, 1));
					
				}
			}if(((xCurrent - 1) > -1) && (yCurrent - 1) > -1){
				if(a[xCurrent - 1][yCurrent - 1] == 0){
					
					a[xCurrent - 1][yCurrent - 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent - 1, yCurrent - 1, 1));
					
					
				}
				
			}if(((xCurrent - 1) > -1)){
				if(a[xCurrent - 1][yCurrent] == 0){
					
					a[xCurrent -1][yCurrent] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent - 1, yCurrent, 1));
				
					
				}
				
			}if(((xCurrent + 1) < a.length) && ((yCurrent + 1) < a[0].length)){
				if(a[xCurrent + 1][yCurrent + 1] == 0){
					
					a[xCurrent + 1][yCurrent + 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent + 1, yCurrent + 1, 1));
					
				}
			}if(((xCurrent + 1) < a.length) && ((yCurrent - 1) > -1)){
				if(a[xCurrent + 1][yCurrent - 1] == 0){
					
					a[xCurrent +1][yCurrent - 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent + 1, yCurrent - 1, 1));
					
				}
			}if(((xCurrent + 1) < a.length)){
				if(a[xCurrent + 1][yCurrent] == 0){
					
					a[xCurrent + 1][yCurrent] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent + 1, yCurrent, 1));
					
				}
			}if((yCurrent - 1) > -1){
				if(a[xCurrent][yCurrent - 1] == 0){
					
					a[xCurrent][yCurrent - 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent, yCurrent - 1, 1));
					
				}
			}if((yCurrent + 1) < a[0].length){
				if(a[xCurrent][yCurrent + 1] == 0){
					
					a[xCurrent][yCurrent + 1] = a[xCurrent][yCurrent] + 1;
					queue.add(new Square(xCurrent, yCurrent + 1, 1));
					
				}
			}
			
			a[xInitial][yInitial] = 0;
			
		}
		
		return a;
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public Square maxDist(int a[][], int xEnd, int yEnd, int speed){
		int dist = 30000;//infinito
		int xAux = 0, yAux = 0;
		
		
		if(((xEnd - 1) > -1) && (yEnd + 1) < a[0].length){
			
			if(a[xEnd - 1][yEnd + 1] > 0 && a[xEnd - 1][yEnd + 1] < dist){
				dist = a[xEnd - 1][yEnd + 1];
				xAux = xEnd - 1;
				yAux = yEnd + 1;
				
			}
		}if(((xEnd - 1) > -1) && (yEnd - 1) > -1){
			if(a[xEnd - 1][yEnd - 1] > 0 && a[xEnd - 1][yEnd - 1] < dist){
				dist = a[xEnd - 1][yEnd - 1];
				xAux = xEnd - 1;
				yAux = yEnd - 1;
			}
			
		}if(((xEnd - 1) > -1)){
			if(a[xEnd - 1][yEnd] > 0 && a[xEnd - 1][yEnd] < dist){
				dist = a[xEnd - 1][yEnd];
				xAux = xEnd - 1;
				yAux = yEnd;
			}
			
		}if(((xEnd + 1) < a.length) && ((yEnd + 1) < a[0].length)){
			if(a[xEnd + 1][yEnd + 1] > 0 && a[xEnd + 1][yEnd + 1] < dist){
				dist = a[xEnd + 1][yEnd + 1];
				xAux = xEnd + 1;
				yAux = yEnd + 1;
			}
		}if(((xEnd + 1) < a.length) && ((yEnd - 1) > -1)){
			if(a[xEnd + 1][yEnd - 1] > 0 && a[xEnd + 1][yEnd - 1] < dist){
				dist = a[xEnd + 1][yEnd - 1];
				xAux = xEnd + 1;
				yAux = yEnd - 1;
			}
		}if(((xEnd + 1) < a.length)){
			if(a[xEnd + 1][yEnd] > 0 && a[xEnd + 1][yEnd] < dist){
				dist = a[xEnd + 1][yEnd];
				xAux = xEnd + 1;
				yAux = yEnd;
			}
		}if((yEnd - 1) > -1){
			if(a[xEnd][yEnd - 1] > 0 && a[xEnd][yEnd - 1] < dist){
				dist = a[xEnd][yEnd - 1];
				xAux = xEnd;
				yAux = yEnd - 1;
			}
		}if((yEnd + 1) < a[0].length){
			if(a[xEnd][yEnd + 1] > 0 && a[xEnd][yEnd + 1] < dist){
				dist = a[xEnd][yEnd + 1];
				xAux = xEnd;
				yAux = yEnd + 1;
			}
		}
		
			
		
		if(dist == speed){
			return new Square(xAux, yAux, 1);
		}else{
			return maxDist(a, xAux, yAux, speed);
		}
		
		
	}
	
	public Square newPosition(int a[][], int xEnd, int yEnd, int speed){
		int dist = 30000;//infinito
		int xAux = 0, yAux = 0;
		
		
		if(((xEnd - 1) > -1) && (yEnd + 1) < a[0].length){
			
			if(a[xEnd - 1][yEnd + 1] > 0 && a[xEnd - 1][yEnd + 1] < dist){
				dist = a[xEnd - 1][yEnd + 1];
				xAux = xEnd - 1;
				yAux = yEnd + 1;
				
			}
		}if(((xEnd - 1) > -1) && (yEnd - 1) > -1){
			if(a[xEnd - 1][yEnd - 1] > 0 && a[xEnd - 1][yEnd - 1] < dist){
				dist = a[xEnd - 1][yEnd - 1];
				xAux = xEnd - 1;
				yAux = yEnd - 1;
			}
			
		}if(((xEnd - 1) > -1)){
			if(a[xEnd - 1][yEnd] > 0 && a[xEnd - 1][yEnd] < dist){
				dist = a[xEnd - 1][yEnd];
				xAux = xEnd - 1;
				yAux = yEnd;
			}
			
		}if(((xEnd + 1) < a.length) && ((yEnd + 1) < a[0].length)){
			if(a[xEnd + 1][yEnd + 1] > 0 && a[xEnd + 1][yEnd + 1] < dist){
				dist = a[xEnd + 1][yEnd + 1];
				xAux = xEnd + 1;
				yAux = yEnd + 1;
			}
		}if(((xEnd + 1) < a.length) && ((yEnd - 1) > -1)){
			if(a[xEnd + 1][yEnd - 1] > 0 && a[xEnd + 1][yEnd - 1] < dist){
				dist = a[xEnd + 1][yEnd - 1];
				xAux = xEnd + 1;
				yAux = yEnd - 1;
			}
		}if(((xEnd + 1) < a.length)){
			if(a[xEnd + 1][yEnd] > 0 && a[xEnd + 1][yEnd] < dist){
				dist = a[xEnd + 1][yEnd];
				xAux = xEnd + 1;
				yAux = yEnd;
			}
		}if((yEnd - 1) > -1){
			if(a[xEnd][yEnd - 1] > 0 && a[xEnd][yEnd - 1] < dist){
				dist = a[xEnd][yEnd - 1];
				xAux = xEnd;
				yAux = yEnd - 1;
			}
		}if((yEnd + 1) < a[0].length){
			if(a[xEnd][yEnd + 1] > 0 && a[xEnd][yEnd + 1] < dist){
				dist = a[xEnd][yEnd + 1];
				xAux = xEnd;
				yAux = yEnd + 1;
			}
		}
		
		Square square =  new Square(xAux, yAux, 1);
		
		return square;
	}
	
public int distanceTo( int xEnd, int yEnd){
		
		int[][] a = this.aStar(this.getPositionX(), this.getPositionY());
		int dist = 30000;//infinito
		
		//return a[xEnd][yEnd];
		if(((xEnd - 1) > -1) && (yEnd + 1) < a[0].length){
			if(a[xEnd - 1][yEnd + 1] > 0 && a[xEnd - 1][yEnd + 1] < dist){
				dist = a[xEnd - 1][yEnd + 1];
			
			}
		}if(((xEnd - 1) > -1) && (yEnd - 1) > -1){
			if(a[xEnd - 1][yEnd - 1] > 0 && a[xEnd - 1][yEnd - 1] < dist){
				dist = a[xEnd - 1][yEnd - 1];
			}
			
		}if(((xEnd - 1) > -1)){
			if(a[xEnd - 1][yEnd] > 0 && a[xEnd - 1][yEnd] < dist){
				dist = a[xEnd - 1][yEnd];
			}
			
		}if(((xEnd + 1) < a.length) && ((yEnd + 1) < a[0].length)){
			if(a[xEnd + 1][yEnd + 1] > 0 && a[xEnd + 1][yEnd + 1] < dist){
				dist = a[xEnd + 1][yEnd + 1];
			}
		}if(((xEnd + 1) < a.length) && ((yEnd - 1) > -1)){
			if(a[xEnd + 1][yEnd - 1] > 0 && a[xEnd + 1][yEnd - 1] < dist){
				dist = a[xEnd + 1][yEnd - 1];
			}
		}if(((xEnd + 1) < a.length)){
			if(a[xEnd + 1][yEnd] > 0 && a[xEnd + 1][yEnd] < dist){
				dist = a[xEnd + 1][yEnd];
			}
		}if((yEnd - 1) > -1){
			if(a[xEnd][yEnd - 1] > 0 && a[xEnd][yEnd - 1] < dist){
				dist = a[xEnd][yEnd - 1];
			}
		}if((yEnd + 1) < a[0].length){
			if(a[xEnd][yEnd + 1] > 0 && a[xEnd][yEnd + 1] < dist){
				dist = a[xEnd][yEnd + 1];
			}
		}
		return dist;
	}

	public boolean allowedAction(int type){
		if(type == 1 && this.availableActions[0]==1 ) { //aÃ§Ã£o do tipo full-round
			this.availableActions[0] = 0;
			this.availableActions[1] = 0;
			this.availableActions[2] = 0;
			return true;
		}
		else if(type == 2 && this.availableActions[1]>0){ //aÃ§Ã£o do tipo standard
			this.availableActions[0] = 0;
			this.availableActions[1] = this.availableActions[1] - 1;
			return true;
		}
		else if(type == 3 && this.availableActions[2]>0){ //aÃ§Ã£o do tipo swift
			this.availableActions[0] = 0;
			this.availableActions[2] = 0;
			return true;
		}
		return false;
	}
	
	public void moveTo(Character destinationCharacter){
		int[][] a = this.aStar(this.getPositionX(), this.getPositionY());
		int dist = this.distanceTo( destinationCharacter.getPositionX(), destinationCharacter.getPositionY());
		
		if(dist <= this.getSpeed()){
			Square square = this.newPosition(a,destinationCharacter.getPositionX(), destinationCharacter.getPositionY(), this.getSpeed());
			
			square.setCharacter(this);
			square.setMapFlag(0);
			
			grid.getGridSquares()[square.getX()][square.getY()] = square;
			grid.getGridSquares()[this.getPositionX()][this.getPositionY()].setCharacter(null);
			grid.getGridSquares()[this.getPositionX()][this.getPositionY()].setMapFlag(1);
			
			this.setInitialPosition(square.getX(), square.getY());
			
		}else{
			Square square = this.maxDist(a,destinationCharacter.getPositionX(), destinationCharacter.getPositionY(), this.getSpeed());

			square.setCharacter(this);
			square.setMapFlag(0);
			
			grid.getGridSquares()[square.getX()][square.getY()] = square;
			grid.getGridSquares()[this.getPositionX()][this.getPositionY()].setCharacter(null);
			grid.getGridSquares()[this.getPositionX()][this.getPositionY()].setMapFlag(1);
			this.setInitialPosition(square.getX(), square.getY());
		
		}
	}
	
	public void resetActions(){
		this.availableActions[0] = 1;
		this.availableActions[1] = 2;
		this.availableActions[2] = 1;		
	}
	
	public static int rollToHit(Character actor, Character target, Attack atk)
	{
		int roll = diceRoller(1,20,0);
		return roll;
	}
	public static void attackAction(Character actor, Character target, Attack atk)
	{
		int roll = rollToHit(actor,target,atk);
		boolean crit = false;
		if(roll>=atk.getCritChance())
		{
			crit =true;
		}
		
		String[] parser = new String[3];
		int[] atkbonus = atk.getAtkBonus();
		int hitbonus = atkbonus[0];
		roll = roll + hitbonus;
		if(roll>=target.ac)
		{
			
			//Separar os nÃºmeros do ataque.
			int typeOfDice, numberOfDice, modifier;
			int i=0;
			
			parser = atk.getAtkDmg().split("d");
			numberOfDice = Integer.parseInt(parser[0]);
			parser = parser[1].split("\\+");
			typeOfDice = Integer.parseInt(parser[0]);
			modifier = Integer.parseInt(parser[1]);

			//rolar os dados
			int damage = diceRoller(numberOfDice,typeOfDice,modifier);
			if(crit)
			{
				damage = damage*atk.getCritMult();
			}
			//modificar HP do alvo e atualiza condiÃ§Ã£o
			target.setHp(target.getHp()-damage);
			target.setCondition();
			//imprimir
			 System.out.println(target.getName()+" sofreu "+damage+" de dano para o "+atk.getName()+" de "+actor.getName());
		}
		
		else
		{
			System.out.println(target.getName()+" nao foi acertado por "+actor.getName());
		}
		
	}

	public static void magicAction(Character actor, Character target, Spell spell)
	{
		
		if(spell==null)
		{
			return;
		}
		
		//Separar os nÃºmeros do ataque.
		int typeOfDice, numberOfDice, modifier;
		int i=0;
		
		String parser[] = new String[3];
		parser = spell.getEffect().split("d");
		numberOfDice = Integer.parseInt(parser[0]);
		
		parser = parser[1].split("\\+");
		typeOfDice = Integer.parseInt(parser[0]);
		modifier = Integer.parseInt(parser[1]);

		//rolar o teste de resistÃªncia.
		boolean save = rollSave(spell,target);
		//rolar os dados
		int damage = diceRoller(numberOfDice,typeOfDice,modifier);
		//caso tenha sucesso no save, diminui o dano pela metade.
		if(save)
		{
			damage = damage/2;
		}

		//diminui o hp se for magia de dano, aumenta vida.
		if(spell.getType()== '0')
		{
			target.setHp(target.getHp()-damage);
			System.out.println(target.getName()+" sofreu "+damage+" de dano para o "+spell.getName()+" de "+actor.getName());
		}
		else
		{
			target.setHp(target.getHp()+damage);
			System.out.println(target.getName()+" curou "+damage+" de dano com o "+spell.getName()+" de "+actor.getName());
		}
		target.setCondition();
	}

	public static boolean rollSave(Spell spell, Character target)
	{
		if(spell.getSave()=='n')
		{
			return false;
		}
		else
		{
			if(spell.getSave()=='f')
			{
				int roll = diceRoller(1,20,target.getFort());
				if(roll>=spell.getSaveDC())
				{
					return true;
				}
			}
			if(spell.getSave()=='r')
			{
				int roll = diceRoller(1,20,target.getRef());
				if(roll>=spell.getSaveDC())
				{
					return true;
				}
			}
			else
			{
				int roll = diceRoller(1,20,target.getWill());
				if(roll>=spell.getSaveDC())
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<Attack> getAttacks() {
		return this.attacks;
	}
}