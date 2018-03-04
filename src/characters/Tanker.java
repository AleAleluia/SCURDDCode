package characters;

import java.util.ArrayList;

import engine.Grid;
import engine.Letter;
import engine.Square;
import mechanics.Attack;
import mechanics.Spell;
import static engine.Simulator.grid;

public class Tanker extends Character {

	public Tanker(String name, int role, int hp, int ac, int init, char size, int speed, int fort, int ref, int will,
			ArrayList<Attack> attacks, ArrayList<Spell> spells) {
		super(name, role, hp, ac, init, size, speed, fort, ref, will, attacks, spells);

	}

	public void offensiveMove(ArrayList<Character> enemies) {
		int dist = 9999;
		int a[][] = this.aStar(this.getPositionX(), this.getPositionY());// matriz com todas as distância, tendo como origem o tanker
		Character enemySelected = enemies.get(0);

		for (int i = 0; i < enemies.size(); i++) {
			if (dist > a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()]) {

				dist = a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()];
				enemySelected = enemies.get(i);
			} else if (dist == a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()]) {

				if (enemySelected.getCondition() > enemies.get(i).getCondition()) {
					enemySelected = enemies.get(i);
				
				} else if (enemySelected.getCondition() == enemies.get(i).getCondition()) {
					
					if (enemySelected.getRole() < enemies.get(i).getRole()) {
						enemySelected = enemies.get(i);
					}
				}
			}
		}
		
		moveTo(enemySelected);

	}
	
	public boolean needToDefender(ArrayList<Character> allies){
		for(int i = 0; i < allies.size(); i++){
			if(allies.get(i).getRole() == 4 && allies.get(i).getCondition() >= 2 && allies.get(i).getCondition() != 4){
				return true;
			}else if((allies.get(i).getRole() == 2 || allies.get(i).getRole() == 3 ) && allies.get(i).getCondition() == 2){
				return true;
			}
		}
		return false;
	}
	
	public void defensiveMove(ArrayList<Character> allies, ArrayList<Character> enemies){
		Character alliesBounded = null;
		
		for(int i = 0; i < allies.size(); i++){
			if(allies.get(i).getRole() == 4 && allies.get(i).getCondition() >= 2 && allies.get(i).getCondition() != 4){
				alliesBounded = allies.get(i);
			}
		}
		
		if(alliesBounded != null){
			int a[][] = this.aStar(alliesBounded.getPositionX(), alliesBounded.getPositionY());
			Character currentenemy = null;
			int enemyNearest = 30000;// distância ao inimigo mais próximo
			
			for(int i = 0; i < enemies.size(); i++){
				if(a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()] < enemyNearest){
					enemyNearest = a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()];
					currentenemy = enemies.get(i);
				}
			}
			
			a = this.aStar(this.getPositionX(), this.getPositionY());
			
			if(a[currentenemy.getPositionX()][currentenemy.getPositionY()] <= this.getSpeed()){
				this.newPosition(a, currentenemy.getPositionX(), currentenemy.getPositionY(), this.getSpeed());
				
				Attack attack = this.getAttacks().get(0);
				int meanAttack = attack.mean();
				
				for(int i = 1; i < this.getAttacks().size(); i++){
					if(meanAttack < this.getAttacks().get(i).mean()){
						meanAttack = this.getAttacks().get(i).mean();
						attack = this.getAttacks().get(i);
					}
				}
				
				Character.attackAction(grid.getCurrentCharacter(), currentenemy, attack);
			}else{
				this.maxDist(a, currentenemy.getPositionX(), currentenemy.getPositionY(), this.getSpeed());
			}
			
		}else{
			
			for(int i = 0; i < allies.size(); i++){
				if(allies.get(i).getRole() == 3 && allies.get(i).getCondition() == 2){
					alliesBounded = allies.get(i);
					break;
				}else if(allies.get(i).getRole() == 2 && allies.get(i).getCondition() == 2){
					alliesBounded = allies.get(i);
				}
			}
			
			int a[][] = this.aStar(alliesBounded.getPositionX(), alliesBounded.getPositionY());
			Character currentenemy = null;
			int enemyNearest = 30000;// distância ao inimigo mais próximo
			
			for(int i = 0; i < enemies.size(); i++){
				if(a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()] < enemyNearest){
					enemyNearest = a[enemies.get(i).getPositionX()][enemies.get(i).getPositionY()];
					currentenemy = enemies.get(i);
				}
			}
			
			a = this.aStar(this.getPositionX(), this.getPositionY());
			
			if(a[currentenemy.getPositionX()][currentenemy.getPositionY()] <= this.getSpeed()){
				moveTo(currentenemy);
				
				Attack attack = this.getAttacks().get(0);
				int meanAttack = attack.mean();
				
				for(int i = 1; i < this.getAttacks().size(); i++){
					if(meanAttack < this.getAttacks().get(i).mean()){
						meanAttack = this.getAttacks().get(i).mean();
						attack = this.getAttacks().get(i);
					}
				}
				
				Character.attackAction(grid.getCurrentCharacter(), currentenemy, attack);
			}else{
				this.moveTo(currentenemy);
			}
			
		}
		
		
	}
	
	public Letter checkMailBox(){
		if(this.getTeam() == 'a'){
			for(int i = 0; i < grid.mailBoxA.size(); i++){
				if(grid.mailBoxA.get(i).getCode() == 2){
					return grid.mailBoxA.get(i);
				}
			}
		}else if(this.getTeam() == 'b'){
			for(int i = 0; i < grid.mailBoxB.size(); i++){
				if(grid.mailBoxB.get(i).getCode() == 2){
					return grid.mailBoxB.get(i);
				}
			}
		}
		return null;
	}
	
	public void guidedAction(Character target){
		
		this.moveTo(target);
		
		int mean=0;
		Attack chosenAttack = null;
		for(int j = 0; j< this.attacks.size(); j++){
			if(attacks.get(j).mean() > mean)
				chosenAttack = attacks.get(j);
		}
		Character.attackAction(this, target, chosenAttack);
		if(target.getCondition() == 3){
			if(this.getTeam() == 'a'){
				grid.mailBoxA.add(new Letter(this.getName(), 2, target.getName()));
			}else{
				grid.mailBoxB.add(new Letter(this.getName(), 2, target.getName()));
			}
		}
	}
	
	

	public void play(){
		ArrayList<Character> allies = null, enemies = null;
		Character enemy = null;
		
		if(this.getTeam() == 'a'){
			allies = grid.getTeamA();
			enemies = grid.getTeamB();
			
			
			if(this.getCondition() == 3){
				grid.getMailBoxA().add(new Letter(this.getName(), 1, this.getName()));
			}
			
		}else{
			allies = grid.getTeamB();
			enemies = grid.getTeamA();
			
			if(this.getCondition() == 3){
				grid.getMailBoxB().add(new Letter(this.getName(), 1, this.getName()));
			}
		}
		
		if(this.getCondition() == 3){
			grid.getMailBoxB().add(new Letter(this.getName(), 1, this.getName()));
		}
		
		if(checkMailBox() != null){
			for(int i = 0; i < enemies.size(); i++){
				if(enemies.get(i).getName().equals(checkMailBox().getFocus())){
					enemy = enemies.get(i);
					break;
				}
			}
			guidedAction(enemy);
		}else{
		
			if(needToDefender(allies)){
				defensiveMove(allies, enemies);
				
			}else{
				offensiveMove(enemies);
			}
		}
	}
}
