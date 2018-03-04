package characters;

import mechanics.Attack;
import mechanics.Spell;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
		
		Character target = temp.get(0);
		
		return target;
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
			if(letter.getCode()==1)
			{
				ArrayList<Character> allyTeam = grid.getTeamA();
				for(int i = 0; i < allyTeam.size(); i++){
					if(allyTeam.get(i).getName().equals(letter.getSender())){
						target = allyTeam.get(i);
						break;
					}
				}
			}
			else
			{
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
