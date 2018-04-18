package characters;

import mechanics.Attack;
import mechanics.Spell;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import engine.Grid;
import engine.Letter;

import static engine.Simulator.grid;

public class Support extends Character{

	public Support(String name, int role, int hp, int ac, int init, char size, int speed, int fort, int ref, int will,
				   ArrayList<Attack> attacks, ArrayList<Spell> spells) {
		super(name, role, hp, ac, init, size, speed, fort, ref, will, attacks, spells);
		// TODO Auto-generated constructor stub
	}
	
	public Character VerifyPriorityHealingTarget(ArrayList<Character> allies){
		

		if(this.getCondition() == 3)
			return this;

		ArrayList<Character> temp = new ArrayList<Character>();
		
		temp = CheckHealth(3, allies);
						
		if(temp.size() > 1)
			temp = CheckRole(4, temp);
		
		if(temp.size() > 1)
			temp = CheckDistance(allies);
		if(temp.size() > 0){
			Character target = temp.get(0);
			return target;
		}else{
			System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEETA PLEEEEEEEEEEEEEURA");
			return null;
		}
	}
	
	
	private ArrayList<Character> CheckDistance(ArrayList<Character> team) {
		// TODO Auto-generated method stub
		ArrayList<Character> temp = new ArrayList<Character>();
		
		//Análise do inimigo mais próximo
		
	
		int dist = 9999;
		int a[][] = this.aStar(this.getPositionX(), this.getPositionY());
		for (int i = 0; i < team.size(); i++) {
			if (dist > a[team.get(i).getPositionX()][team.get(i).getPositionY()]) {

				dist = a[team.get(i).getPositionX()][team.get(i).getPositionY()];
				temp.add(team.get(i));
			}
		}
		
		return temp;
	}

	public ArrayList<Character> CheckHealth(int cond, List<Character> allies){
		if(cond < 1){
			return null;
		}
		ArrayList<Character> temp = new ArrayList<Character>();
		
		for (Character ally : allies) {
			if(ally.getCondition() == cond){
				temp.add(ally);
			}
		}
		
		if(temp.isEmpty()){
			ArrayList<Character> aux = new ArrayList<Character>(); 
			aux = CheckHealth(cond-1, allies);
			if(aux != null){
				temp = aux;
			}
		}
		return temp;
	}
	
	public ArrayList<Character> CheckRole(int role, List<Character> allies){
		if(role < 1)
			return null;
		
		ArrayList<Character> temp = new ArrayList<Character>();
	
		for (Character ally : allies)
			if(ally.getRole() == role)
				temp.add(ally);
		
		if(temp.isEmpty()){
			ArrayList<Character> aux = new ArrayList<Character>(); 
			aux = CheckHealth(role-1, allies);
			if(aux != null){
				temp = aux;
			}
		}
		return temp;
	}

	public Character VerifyPriorityTarget(ArrayList<Character> enemies){
		
		ArrayList<Character> temp = new ArrayList<Character>();
		Character target;
		int cond = 1;
		int role = 1;

		temp = CheckDistance(enemies);
		
		
		if(temp.size() > 1)
			temp = CheckHealth(3, enemies);
		if(temp.size() > 1)
			temp = CheckRole(role, temp);
		
		target = temp.get(0);
		
		return target;	
	}
	
	public Letter check(){
		if(this.getTeam() == 'a'){
			if(grid.getMailBoxA().size()!=0){
				Letter pickedLetter = grid.getMailBoxA().get(0);
				if(pickedLetter.getCode()==1)
				{
					return pickedLetter;
				}
				else
				{
					for(int i=1; i<grid.getMailBoxA().size(); i++){
						
						if(grid.getMailBoxA().get(i).getCode() == 1)
						{
							pickedLetter = grid.getMailBoxA().get(i);
							return pickedLetter;
						}
					}
				}
			}
			return null;
		} else{
			if(grid.getMailBoxB().size()!=0){
				Letter pickedLetter = grid.getMailBoxB().get(0);
				if(pickedLetter.getCode()==1)
				{
					return pickedLetter;
				}
				else
				{
					for(int i=1; i<grid.getMailBoxB().size(); i++){
						
						if(grid.getMailBoxB().get(i).getCode() == 1)
						{
							pickedLetter = grid.getMailBoxB().get(i);
							return pickedLetter;
						}
					}
					return pickedLetter;
				}
			}
			else
			{
				return null;
			}
		}
	}
	
	public void solve(Letter letter){
		Character target = null;
		
		if(this.getTeam() == 'a'){
			if(letter.getCode()==1){
				ArrayList<Character> allyTeam = grid.getTeamA();
				for(int i = 0; i < allyTeam.size(); i++){
					if(allyTeam.get(i).getName().equals(letter.getSender())){
						target = allyTeam.get(i);
						break;
					}
				}
			}
			else{
				ArrayList<Character> enemyTeam = grid.getTeamB();
				for(int i = 0; i < enemyTeam.size(); i++){
					if(enemyTeam.get(i).getName().equals(letter.getFocus())){
						target = enemyTeam.get(i);
						break;
					}
				}
			}
		}else{
			if(letter.getCode()==1)
			{
				ArrayList<Character> allyTeam = grid.getTeamB();
				for(int i = 0; i < allyTeam.size(); i++){
					if(allyTeam.get(i).getName().equals(letter.getSender())){
						target = allyTeam.get(i);
						break;
					}
				}	
			}
			else
			{
				ArrayList<Character> enemyTeam = grid.getTeamA();
				for(int i = 0; i < enemyTeam.size(); i++){
					if(enemyTeam.get(i).getName().equals(letter.getFocus())){
						target = enemyTeam.get(i);
						break;
					}
				}	
			}
		}
		
		if(letter.getCode()==1)
		{
			if(target == null){
				System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEETA PLEEEEEEEEEEEEEURA2");
			}
			if(this.distanceTo(target.getPositionX(), target.getPositionY()) <= this.getSpeed()){
				this.guidedActionHelp(target);
				
				if(this.getTeam() == 'a'){
					grid.mailBoxA.remove(letter);
				}else{
					grid.mailBoxB.remove(letter);
				}
			}else{
				normally();
			}
		}
		else
		{
			if(this.distanceTo(target.getPositionX(), target.getPositionY()) <= this.getSpeed()){
				this.guidedActionFocus(target);
				
				if(this.getTeam() == 'a'){
					grid.mailBoxA.remove(letter);
				}else{
					grid.mailBoxB.remove(letter);
				}
			}else{
				normally();
			}
		}
	}

	private void guidedActionFocus(Character target) {
		// TODO Auto-generated method stub
		attack(target);
	}

	private void guidedActionHelp(Character ally) {
		// TODO Auto-generated method stub

		ArrayList<Spell> bestHeals = chooseSpells(ally);
		//alvo muito longe
		if(bestHeals.isEmpty())
		{
			this.moveTo(ally);
			bestHeals = chooseSpells(ally);
			//alvo ainda longe
			if(bestHeals.isEmpty())
			{
				//mover segunda vez.
				this.moveTo(ally);
			}
			else
			{
				Spell chosenHeal = bestOfSpells(bestHeals);
				magicAction(this,ally,chosenHeal);
			}
		}
		else
		{
			Spell chosenHeal = bestOfSpells(bestHeals);
			if(chosenHeal!=null)
			{
				magicAction(this,ally,chosenHeal);
			}
		}
	
	}

	public void play(){

		/*prioridade do suporte
		 * - curar a si mesmo, se estiver ferido
		 * -curar aliados, se estiverem feridos
		 * -atacar*/

		//está ferido
		Letter problem = check();
		if(problem != null){
			solve(problem);
			
		} else{ //Personagem age normalmente, n�o h� mensagens para ele
			normally();
		}
	}


	private void normally() {
		Character ally, enemy;
		if(this.getTeam()=='a')
		{
			ally = VerifyPriorityHealingTarget(grid.getTeamA());
			enemy = VerifyPriorityTarget(grid.getTeamB());
		}
		else
		{
			ally = VerifyPriorityHealingTarget(grid.getTeamB());
			enemy = VerifyPriorityTarget(grid.getTeamA());
		}
		
		//Valores default das estrategias do jogador
		Attack bestAttack = bestAttack();
		Spell bestHeal = bestHeal();
		int moveToAlly =0;
		int moveToEnemy = 0;
		int attack =0;
		int moveAndAttack =0;
		int healAlly =0;
		int moveAndHeal =0;
		int healSelf =0;
		
		//Estrategias do inimigo
		int enemyMove =0;
		int enemyAttack=0;
		int enemyHeal=0;
		
		//checagem de condicoes
		if(this.getCondition()==3)
		{
			healSelf = bestHeal.mean();//se o personagem esta ferido, a acao de se curar e considerada
		}
		if(Grid.distance(this,enemy)>bestAttack.getRange())//verifica a distancia do inimigo alvo
		{
			attack =-1000; //infinito negativo;
		}
		else
		{
			attack = bestAttack.mean();
		}
		if(Grid.distance(this,enemy)>bestAttack.getRange()+this.getSpeed())//verifica a distancia do inimigo apos uma acao de movimento
		{
			moveAndAttack =-1000;
		}
		else
		{
			moveAndAttack = bestAttack.mean();
		}
		if(ally != null){
			if(ally.getCondition()==3)//verifica se o aliado esta ferido e depois verifica sua distancia
			{
				if(Grid.distance(this,ally)>bestHeal.getRange())
				{
					healAlly =-1000; //infinito negativo;
				}
				else
				{
					healAlly = bestHeal.mean();
				}
				if(Grid.distance(this,ally)>bestHeal.getRange()+this.getSpeed())
				{
					moveAndHeal =-1000;
				}
				else
				{
					moveAndHeal = bestHeal.mean();
				}
			}
		}
		
		int bestValue = 0;
		int bestStrategy = 0; /*0-atacar
		 					  1-andar e atacar
		 					  2-andar até inimigo
		 					  3-Curar aliado
		 					  4 - andar até aliado e curar
		 					  5-andar até aliado
		 					  6-Curar a si mesmo*/
		if(enemy.getRole()==2)//inimigo mdps
		{
			//inicializar valores do inimigo
			enemyAttack = bestDamageValue(enemy);
			//compara todas as estrategias do suporte com as estrategias do inimigo e mantem a estretegia mais vantajosa.
			if(attack-enemyMove>bestValue)
			{
				bestValue = attack-enemyMove;
				bestStrategy = 0;
			}
			if(attack-enemyAttack>bestValue)
			{
				bestValue = attack-enemyAttack;
				bestStrategy = 0;
			}
			
			if(moveAndAttack-enemyMove>bestValue)
			{
				bestValue = moveAndAttack-enemyMove;
				bestStrategy = 1;
			}
			if(moveAndAttack-enemyAttack>bestValue)
			{
				bestValue = moveAndAttack-enemyAttack;
				bestStrategy = 1;
			}
			
			if(moveToEnemy-enemyMove>bestValue)
			{
				bestValue = moveToEnemy-enemyMove;
				bestStrategy = 2;
			}
			if(moveToEnemy-enemyAttack>bestValue)
			{
				bestValue = moveToEnemy-enemyAttack;
				bestStrategy = 2;
			}
			
			if(healAlly-enemyMove>bestValue)
			{
				bestValue = healAlly-enemyMove;
				bestStrategy = 3;
			}
			if(healAlly-enemyAttack>bestValue)
			{
				bestValue = healAlly-enemyAttack;
				bestStrategy = 3;
			}
			
			if(moveAndHeal-enemyMove>bestValue)
			{
				bestValue = moveAndHeal-enemyMove;
				bestStrategy = 4;
			}
			if(moveAndHeal-enemyAttack>bestValue)
			{
				bestValue = moveAndHeal-enemyAttack;
				bestStrategy = 4;
			}
			
			if(moveToAlly-enemyMove>bestValue)
			{
				bestValue = moveToAlly-enemyMove;
				bestStrategy = 5;
			}
			if(moveToAlly-enemyAttack>bestValue)
			{
				bestValue = moveToAlly-enemyAttack;
				bestStrategy = 5;
			}
			if(healSelf-enemyMove>bestValue)
			{
				bestValue = healSelf-enemyMove;
				bestStrategy = 6;
			}
			if(healSelf-enemyAttack>bestValue)
			{
				bestValue = healSelf-enemyAttack;
				bestStrategy = 6;
			}
		}
		if(enemy.getRole()==4)//inimigo suporte
		{
			enemyAttack = bestDamageValue(enemy);
			enemyHeal = bestHealValue(enemy);
			
			if(attack-enemyMove>bestValue)
			{
				bestValue = attack-enemyMove;
				bestStrategy = 0;
			}
			if(attack-enemyAttack>bestValue)
			{
				bestValue = attack-enemyAttack;
				bestStrategy = 0;
			}
			if(attack-enemyHeal>bestValue)
			{
				bestValue= attack-enemyHeal;
				bestStrategy=0;
			}
			
			if(moveAndAttack-enemyMove>bestValue)
			{
				bestValue = moveAndAttack-enemyMove;
				bestStrategy = 1;
			}
			if(moveAndAttack-enemyAttack>bestValue)
			{
				bestValue = moveAndAttack-enemyAttack;
				bestStrategy = 1;
			}
			if(moveAndAttack-enemyHeal>bestValue)
			{
				bestValue= moveAndAttack-enemyHeal;
				bestStrategy=1;
			}
			
			if(moveToEnemy-enemyMove>bestValue)
			{
				bestValue = moveToEnemy-enemyMove;
				bestStrategy = 2;
			}
			if(moveToEnemy-enemyAttack>bestValue)
			{
				bestValue = moveToEnemy-enemyAttack;
				bestStrategy = 2;
			}
			if(moveToEnemy-enemyHeal>bestValue)
			{
				bestValue= moveToEnemy-enemyHeal;
				bestStrategy=2;
			}
			
			if(healAlly-enemyMove>bestValue)
			{
				bestValue = healAlly-enemyMove;
				bestStrategy = 3;
			}
			if(healAlly-enemyAttack>bestValue)
			{
				bestValue = healAlly-enemyAttack;
				bestStrategy = 3;
			}
			if(healAlly-enemyHeal>bestValue)
			{
				bestValue= healAlly-enemyHeal;
				bestStrategy=3;
			}
			
			if(moveAndHeal-enemyMove>bestValue)
			{
				bestValue = moveAndHeal-enemyMove;
				bestStrategy = 4;
			}
			if(moveAndHeal-enemyAttack>bestValue)
			{
				bestValue = moveAndHeal-enemyAttack;
				bestStrategy = 4;
			}
			if(moveAndHeal-enemyHeal>bestValue)
			{
				bestValue= moveAndHeal-enemyHeal;
				bestStrategy=4;
			}
			
			if(moveToAlly-enemyMove>bestValue)
			{
				bestValue = moveToAlly-enemyMove;
				bestStrategy = 5;
			}
			if(moveToAlly-enemyAttack>bestValue)
			{
				bestValue = moveToAlly-enemyAttack;
				bestStrategy = 5;
			}
			if(moveToAlly-enemyHeal>bestValue)
			{
				bestValue= moveToAlly-enemyHeal;
				bestStrategy=5;
			}
			
			if(healSelf-enemyMove>bestValue)
			{
				bestValue = healSelf-enemyMove;
				bestStrategy = 6;
			}
			if(healSelf-enemyAttack>bestValue)
			{
				bestValue = healSelf-enemyAttack;
				bestStrategy = 6;
			}
			if(healSelf-enemyHeal>bestValue)
			{
				bestValue= healSelf-enemyHeal;
				bestStrategy=6;
			}
		}
		//System.out.println("estrategia de fred:"+bestStrategy);
		switch(bestStrategy)
		{
		case 0:
			attack(enemy);
			break;
		case 1:
			moveTo(enemy);
			attack(enemy);
			break;
		case 2:
			moveTo(enemy);
			moveTo(enemy);
			break;
		case 3:
			heal(ally);
			break;
		case 4:
			moveTo(ally);
			heal(ally);
			break;
		case 5:
			moveTo(ally);
			moveTo(ally);
			break;
		case 6:
			healSelf();
			break;
		}
	}	


	private Spell bestHeal() {
		// TODO Auto-generated method stub
		ArrayList<Spell> spells = this.spells;
		int mean=0;
		Spell bestHeal=spells.get(0);
		for(int i=0;i<spells.size();i++)
		{
			if((spells.get(i).getType()==1)&&(spells.get(i).mean()>mean))
			{
				bestHeal = spells.get(i);
				mean = spells.get(i).mean();
			}
		}
		return bestHeal;
	}

	private Attack bestAttack() {
		// TODO Auto-generated method stub
		ArrayList<Attack> attacks = this.getAttacks();
		int mean=0;
		Attack bestAttack=attacks.get(0);
		for(int i=0;i<attacks.size();i++)
		{
			if(attacks.get(i).mean()>mean)
			{
				bestAttack = attacks.get(i);
				mean = attacks.get(i).mean();
			}
		}
		return bestAttack;
	}

	private int bestHealValue(Character player) {
		int mean =0;
		ArrayList<Spell> heals = player.spells;
		for(int i=0;i<heals.size();i++)
		{
			if(heals.get(i).mean()>mean)
			{
				mean = heals.get(i).mean();
			}
		}
		return mean;
	}

	private int bestDamageValue(Character player) {
		int mean =0;
		ArrayList<Attack> attacks = player.getAttacks();
		for(int i=0;i<attacks.size();i++)
		{
			if(attacks.get(i).mean()>mean)
			{
				mean = attacks.get(i).mean();
			}
		}
		return mean;
	}

	//antigo normally
	/*private void normally() {
		// TODO Auto-generated method stub
		
		Character ally, enemy;
		if(this.getTeam()=='a')
		{
			ally = VerifyPriorityHealingTarget(grid.getTeamA());
			enemy = VerifyPriorityHealingTarget(grid.getTeamB());
		}
		else
		{
			ally = VerifyPriorityHealingTarget(grid.getTeamB());
			enemy = VerifyPriorityHealingTarget(grid.getTeamA());
		}
		
		
		if(this.getCondition()==2 || this.getCondition()==3)
		{
			healSelf();
			if(this.getCondition()==2 || this.getCondition()==3)
			{
				if(this.getTeam() == 'a'){
					grid.mailBoxA.add(new Letter(this.getName(), 1, null));
				}else{
					grid.mailBoxB.add(new Letter(this.getName(), 1, null));
				}
			}
		}
		//está normal
		else
		{
			//aliado ferido
			if(ally.getCondition()==2 || ally.getCondition()==3)
			{
				ArrayList<Spell> bestHeals = chooseSpells(ally);
				//alvo muito longe
				if(bestHeals.isEmpty())
				{
					this.moveTo(ally);
					bestHeals = chooseSpells(ally);
					//alvo ainda longe
					if(bestHeals.isEmpty())
					{
						//mover segunda vez.
						this.moveTo(ally);
					}
					else
					{
						Spell chosenHeal = bestOfSpells(bestHeals);
						magicAction(this,ally,chosenHeal);
					}
				}
				else
				{
					Spell chosenHeal = bestOfSpells(bestHeals);
					if(chosenHeal!=null)
					{
						magicAction(this,ally,chosenHeal);
					}
				}
			}
			else
			{
				attack(enemy);
			}

		}
		
	}*/

	private void heal(Character ally)
	{
		Spell bestHeal= null;
		int numberSpells = super.spells.size();
		for(int i=0;i<numberSpells;i++)
		{
			Spell thisSpell = spells.get(i);
			if(thisSpell.getType()==1)
			{
				if(bestHeal==null)
				{
					bestHeal = thisSpell;
				}
				else
				{
					if(thisSpell.mean()>bestHeal.mean())
					{
						bestHeal = thisSpell;
					}
				}
			}
		}
		magicAction(this,ally,bestHeal);
	}

	private void healSelf() {
		Spell bestHeal= null;
		int numberSpells = super.spells.size();
		for(int i=0;i<numberSpells;i++)
		{
			Spell thisSpell = spells.get(i);
			if(thisSpell.getType()==1)
			{
				if(bestHeal==null)
				{
					bestHeal = thisSpell;
				}
				else
				{
					if(thisSpell.mean()>bestHeal.mean())
					{
						bestHeal = thisSpell;
					}
				}
			}
		}
		magicAction(this,this,bestHeal);
	}
	
	private ArrayList<Spell> chooseSpells(Character target) {
		ArrayList<Spell> bestSpells = new ArrayList<Spell>();
		for(int i=0;i<super.spells.size();i++)
		{
			if(super.spells.get(i).getType()==1)
			{
				if(grid.isInRange(this, target, super.spells.get(i)))
				{
					bestSpells.add(super.spells.get(i));
				}
			}
		}
		return bestSpells;
	}
	
	private ArrayList<Attack> chooseAttacks(Character target) {
		ArrayList<Attack> bestAttacks = new ArrayList<Attack>();
		for(int i=0;i<super.attacks.size();i++)
		{
			if(grid.isInRange(this, target, super.attacks.get(i)))
			{
				bestAttacks.add(super.attacks.get(i));
			}
		}
		return bestAttacks;
	}

	private void attack(Character target)
	{
		ArrayList<Spell> dmgSpells = chooseSpells(target);
		ArrayList<Attack> attacks = chooseAttacks(target);
		if(dmgSpells.isEmpty() && attacks.isEmpty())
		{
			this.moveTo(target);
			dmgSpells = chooseSpells(target);
			attacks = chooseAttacks(target);
			if(!(dmgSpells.isEmpty() && attacks.isEmpty()))
			{
				Spell bestSpell = null;
				Attack bestAttack = null;
				if(dmgSpells!=null)
				{
					bestSpell = bestOfSpells(dmgSpells);
				}
				if(attacks!=null)
				{
					bestAttack = bestOfAttacks(attacks);
				}
				if(bestSpell.mean()== bestAttack.mean())
				{
					if(bestSpell.getSave()!='n')
					{
						attackAction(this, target, bestAttack);
					}
					else
					{
						magicAction(this,target,bestSpell);
					}
				}
				if(bestSpell.mean()>bestAttack.mean())
				{
					magicAction(this,target,bestSpell);
				}
				else
				{
					attackAction(this, target, bestAttack);
					if(target.getCondition() == 3){
						if(this.getTeam() == 'a'){
							grid.mailBoxA.add(new Letter(this.getName(), 2, target.getName()));
						}else{
							grid.mailBoxB.add(new Letter(this.getName(), 2, target.getName()));
						}
					}
				}
			}
		}
		else
		{
			dmgSpells = chooseSpells(target);
			attacks = chooseAttacks(target);
			Spell bestSpell = null;
			Attack bestAttack = null;
			if(dmgSpells!=null)
			{
				bestSpell = bestOfSpells(dmgSpells);
			}
			if(attacks!=null)
			{
				bestAttack = bestOfAttacks(attacks);
			}
			
			boolean spl = true,atk = true;
			if(bestSpell==null)
			{
				spl=false;
			}
			if(bestAttack==null)
			{
				atk = false;
			}
			if(spl)
			{
				if(atk)
				{
					if(bestSpell.mean()== bestAttack.mean())
					{
						if(bestSpell.getSave()!='n')
						{
							attackAction(this, target, bestAttack);
						}
						else
						{
							magicAction(this,target,bestSpell);
						}
					}
					if(bestSpell.mean()>bestAttack.mean())
					{
						magicAction(this,target,bestSpell);
					}
					else
					{
						attackAction(this, target, bestAttack);
					}
				}
				else{
				magicAction(this,target,bestSpell);
				}
			}
			else
			{
				if(atk)
				{
					attackAction(this, target, bestAttack);
				}
			}
			
		}
	}

	private Attack bestOfAttacks(ArrayList<Attack> attacks) {
		if(attacks.size()<=0)
		{
			return null;
		}
		Attack chosen = attacks.get(0);
		for(int i=1;i<attacks.size();i++)
		{
			if(chosen.mean()<attacks.get(i).mean())
			{
				chosen = attacks.get(i);
			}
		}
		return chosen;
	}

	private Spell bestOfSpells(ArrayList<Spell> bestSpells) {
		if(bestSpells.size()<=0)
		{
			return null;
		}
		Spell chosen = bestSpells.get(0);
		for(int i=1;i<bestSpells.size();i++)
		{
			if(chosen.mean()<bestSpells.get(i).mean())
			{
				chosen = bestSpells.get(i);
			}
		}
		if(allowedAction(chosen.getCastTimeInt()))
		{
			return chosen;
		}
		else
		{
			bestSpells.remove(chosen);
			return bestOfSpells(bestSpells);
		}
	}
	
}
