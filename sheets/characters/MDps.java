package characters;

import mechanics.Attack;
import mechanics.Spell;
import static engine.Simulator.grid;

import java.util.ArrayList;

import engine.Letter;
import engine.Square;

public class MDps extends Character {
	public MDps(String name, int role, int hp, int ac, int init, char size, int speed, int fort, int ref, int will,
				ArrayList<Attack> attacks, ArrayList<Spell> spells){
		super(name, role, hp, ac, init, size, speed, fort, ref, will, attacks, spells);
	}	
	
	Character VerifyPriorityTarget(ArrayList<Character> team){
		/* Ordem de prioridade:
		 * Inimigos mais proximos
		 * Inimigos com pior estado
		 * De acordo com a role (Sup > R.DPS > M. DPS > Tanker)
		 * Em caso de empate, pega o primeiro alvo
		 */
		ArrayList<Character> willAttack = new ArrayList<Character>();
		ArrayList<Character> auxWillAttack = new ArrayList<Character>();

		//Análise do inimigo mais próximo
		int distanceX=999999, distanceY=999999;
		for(int i=0; i< team.size(); i++){
			if ( ((this.getPositionX() - team.get(i).getPositionX()) < distanceX) && ( (this.getPositionY() - team.get(i).getPositionY()) < distanceY) ){
				willAttack.add(team.get(i));
				distanceX = this.getPositionX() - team.get(i).getPositionX();
				distanceY = this.getPositionY() - team.get(i).getPositionY();
			}
		}
		if(willAttack.size() == 1)
			return willAttack.get(0);

		auxWillAttack = (ArrayList<Character>) willAttack.clone();
		willAttack.clear();

		//Análise do inimigo com o pior estado
		willAttack = exist(auxWillAttack, 3, 0); //Se há alguém em Condition(0) Badly Wounded(3)
		if(willAttack.isEmpty()){
			willAttack = exist(auxWillAttack, 2, 0); //Se há alguém em Condition(0) Wounded(2)
			if(willAttack.isEmpty())
				willAttack = (ArrayList<Character>) auxWillAttack.clone(); //Todos estão saudáveis
		}

		if(willAttack.size() == 1)
			return willAttack.get(0);

		auxWillAttack = (ArrayList<Character>) willAttack.clone();
		willAttack.clear();

		//Análise de acordo com a role (Sup > R.DPS > M. DPS > Tanker)
		willAttack = exist(auxWillAttack, 4, 1); //Se há alguma Role(1) Support(4)
		if(willAttack.isEmpty()){
			willAttack = exist(auxWillAttack, 3, 1); //Se há alguma Role(1) R.DPS(3)
			if(willAttack.isEmpty()){
				willAttack = exist(auxWillAttack, 2, 1); //Se há alguma Role(1) M.DPS(2)
				if(willAttack.isEmpty())
					willAttack = (ArrayList<Character>) auxWillAttack.clone(); //Vai escolher o Tanker
			}
		}

		return willAttack.get(0);
	}

	//Função para facilitar a checagem se há na lista um personagem com aquele valor num dado atributo
	public ArrayList<Character> exist(ArrayList<Character> list, int condition, int option){
		ArrayList<Character> founded = new ArrayList<Character>();
		for(int i = 0; i<list.size(); i++){
			//Checar a saúde
			if(option == 0){
				if(list.get(i).getCondition() == condition)
					founded.add(list.get(i));
			}
			else if(option == 1){ //Checar a role
				if(list.get(i).getRole() == condition)
					founded.add(list.get(i));
			}
		}

		return founded;
	}

	public void play(){
		ArrayList<Character> ownTeam = new ArrayList<Character>();
		ArrayList<Character> enemyTeam = new ArrayList<Character>();
		
		if(this.getTeam() == 'a'){
			ownTeam = grid.getTeamA();
			enemyTeam = grid.getTeamB();
		}else{
			ownTeam = grid.getTeamB();
			enemyTeam = grid.getTeamA();			
		}
		Letter problem = check();
		if(problem != null){
			solve(problem);
			
		} else{ //Personagem age normalmente, n�o h� mensagens para ele
			normally();
		}
	}
	
	public Letter check(){
		if(this.getTeam() == 'a'){
			if(grid.getMailBoxA() != null){
				for(int i=0; i<grid.getMailBoxA().size(); i++){
					if(grid.getMailBoxA().get(i).getCode() == 2)
						return grid.getMailBoxA().get(i);
				}
			}
		} else{
			if(grid.getMailBoxB() != null){
				for(int i=0; i<grid.getMailBoxB().size(); i++){
					if(grid.getMailBoxB().get(i).getCode() == 2)
						return grid.getMailBoxB().get(i);
				}
			}
		}
		return null;
	}
	
	public void solve(Letter letter){
		Character target = null;
		
		if(this.getTeam() == 'a'){
			ArrayList<Character> enemyTeam = grid.getTeamB();
			for(int i = 0; i < enemyTeam.size(); i++){
				if(enemyTeam.get(i).getName().equals(letter.getFocus())){
					target = enemyTeam.get(i);
					break;
				}
			}
		}else{
			ArrayList<Character> enemyTeam = grid.getTeamA();
			for(int i = 0; i < enemyTeam.size(); i++){
				if(enemyTeam.get(i).getName().equals(letter.getFocus())){
					target = enemyTeam.get(i);
					break;
				}
			}			
		}
		
		if(this.distanceTo(target.getPositionX(), target.getPositionY()) <= this.getSpeed()){
			this.guidedAction(target);
			
			if(this.getTeam() == 'a'){
				grid.mailBoxA.remove(letter);
			}else{
				grid.mailBoxB.remove(letter);
			}
		}else{
			normally();
		}
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
	
	public void normally(){
		
		ArrayList<Character> ownTeam = new ArrayList<Character>();
		ArrayList<Character> enemyTeam = new ArrayList<Character>();
		
		if(this.getTeam() == 'a'){
			ownTeam = grid.getTeamA();
			enemyTeam = grid.getTeamB();
		}else{
			ownTeam = grid.getTeamB();
			enemyTeam = grid.getTeamA();			
		}
		
		
		Character enemy = VerifyPriorityTarget(enemyTeam);
		if(this.getCondition() == 3){
			if(this.getTeam() == 'a'){
				grid.mailBoxA.add(new Letter(this.getName(), 1, null));
			}else{
				grid.mailBoxB.add(new Letter(this.getName(), 1, null));

			}
			for(int i=0; i< ownTeam.size(); i++){
				if(ownTeam.get(i).getRole() == 4){
					if( this.distanceTo(ownTeam.get(i).getPositionX(),ownTeam.get(i).getPositionY()) == 1 ){
						//Esta colado no suporte, apenas ataca
						if( this.distanceTo(enemy.getPositionX(), enemy.getPositionY()) == 1 ){
							int mean=0;
							Attack chosenAttack = null;
							for(int j = 0; j< this.attacks.size(); j++){
								if(attacks.get(j).mean() > mean)
									chosenAttack = attacks.get(j);
							}
							Character.attackAction(this, enemy, chosenAttack);
							if(enemy.getCondition() == 3){
								if(this.getTeam() == 'a'){
									grid.mailBoxA.add(new Letter(this.getName(), 2, enemy.getName()));
								}else{
									grid.mailBoxB.add(new Letter(this.getName(), 2, enemy.getName()));
								}
							}
						}
					}
					else{
						//Corre para o suporte
						this.moveTo(ownTeam.get(i));
					}
				break;
				}
			}
		}
		else{
			//Ataca ate a morte
			this.moveTo(enemy);
			int mean=0;
			Attack chosenAttack = null;
			for(int j = 0; j< this.attacks.size(); j++){
				if(attacks.get(j).mean() > mean)
					chosenAttack = attacks.get(j);
			}
			Character.attackAction(this, enemy, chosenAttack);
			if(enemy.getCondition() == 3){
				if(this.getTeam() == 'a'){
					grid.mailBoxA.add(new Letter(this.getName(), 2, enemy.getName()));
				}else{
					grid.mailBoxB.add(new Letter(this.getName(), 2, enemy.getName()));
				}
			}
		}
	}

}

