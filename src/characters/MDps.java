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
			if ( (Math.abs(this.getPositionX() - team.get(i).getPositionX()) < distanceX) && ( Math.abs(this.getPositionY() - team.get(i).getPositionY()) < distanceY) ){
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
			//normally();
			maxMaxNormally();
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
			//normally();
			maxMaxNormally();
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
	
	public void maxMaxNormally(){
		ArrayList<Character> ownTeam = new ArrayList<Character>();
		ArrayList<Character> enemyTeam = new ArrayList<Character>();
		// Valores a serem utilizados na escolha das estrategias
		int moveToAlly = 0; // Depende do hp atual e da cura dos supps
		int moveToEnemy = 0; // Sempre 0
		int attack = 0; // Maior dano medio de ataques
		// Valores das poss�veis estrategias do inimigo
		int enemyMove = 0; // Sempre 0
		int enemyAttack = 0; // Maior dano medio de ataques do inimigo
		int enemyHeal = 0; // Maior cura media de magias do inimigo
		
		int mean = 0;
		
		// Determinacao dos times (inimigo e aliado)
		if(this.getTeam() == 'a'){
			ownTeam = grid.getTeamA();
			enemyTeam = grid.getTeamB();
		}else{
			ownTeam = grid.getTeamB();
			enemyTeam = grid.getTeamA();			
		}
		
		Character enemy = VerifyPriorityTarget(enemyTeam); // Escolhe o alvo
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////// Atribuicao dos valores das estrategias PESSOAIS
		// MOVE TO ALLY
		Character allySup = null;
		if(this.getCondition() == 3){ // Se estiver gravemente ferido o valor de moveToAlly = melhor cura de suportes
			for(int i=0; i< ownTeam.size(); i++){
				if(ownTeam.get(i).getRole() == 4){ // Se o time tiver algum suporte vivo
					allySup = ownTeam.get(i);
					mean = 0;
					Spell heal = null;
					// Escolha da melhor cura do suporte selecionado
					for(int j = 0; j< allySup.spells.size(); j++){
						if(allySup.spells.get(j).getType() == 1 && allySup.spells.get(j).mean() > mean) // Se o spell for do tipo cura (1) e tiver media >
							heal = spells.get(j);
							mean = heal.mean();
					}
					moveToAlly = heal.mean(); // Recebe o valor da maior cura media dos supps
				}
			}
		} // Caso nao esteja gravemente ferido ou nao tenha supps vivos no time, moveToAlly permanece 0.
		
		// ATTACK
		mean=0;
		int biggestRange = 0;
		Attack biggestRangedAtk = null; // Usado em caso de nenhum estar em range (Move to Enemy)
		Attack chosenAttack = null;
		for(int i = 0; i< this.attacks.size(); i++){
			if (((Math.abs(this.getPositionX() - enemy.getPositionX())) <= attacks.get(i).getRange()) && (Math.abs(this.getPositionY() - enemy.getPositionY()) <= attacks.get(i).getRange())){
				System.out.println(this.getPositionX() - enemy.getPositionX() + "RANGE: " + attacks.get(i).getRange());
				if(attacks.get(i).mean() > mean){
					chosenAttack = attacks.get(i);
					mean = chosenAttack.mean();
					attack = mean; // Recebe o valor do maior ataque medio
				}
			}
			// Usado em caso de nenhum estar em range (Move to Enemy)
			if(attacks.get(i).getRange() > biggestRange){
				biggestRangedAtk = attacks.get(i);
				biggestRange = attacks.get(i).getRange();
			}
		}
		
		// MOVE TO ENEMY
		if(attack == 0){ // Se nao estiver na range de nenhum inimigo
			moveToEnemy = biggestRangedAtk.mean();
		}
		
		/////////////////// Atribuicao dos valores das possiveis estrategias do INIMIGO
		
		// ENEMY ATTACK
		mean=0;
		Attack enemyChosenAttack = null;
		for(int i = 0; i< enemy.attacks.size(); i++){
			if(enemy.attacks.get(i).mean() > mean)
				enemyChosenAttack = enemy.attacks.get(i);
				mean = enemyChosenAttack.mean();
		}
		enemyAttack = enemyChosenAttack.mean(); // Recebe o valor do maior ataque medio do inimigo
		
		// ENEMY HEAL
		mean = 0;
		if(enemy.spells.size() > 0){
			Spell heal = enemy.spells.get(0);
			// Escolha da melhor cura do inimigo selecionado
			for(int j = 0; j < enemy.spells.size(); j++){
				if(enemy.spells.get(j).getType() == 1 && enemy.spells.get(j).mean() > mean) // Se o spell for do tipo cura (1) e tiver media >
					heal = enemy.spells.get(j);
					mean = heal.mean();
			}
			enemyHeal = heal.mean(); // Recebe o valor da maior cura media do inimigo
		}
		
		// Fim da atribuicao de valores
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Comparar estrategias proprias e do inimigo e escolher a melhor (maxima diferenca)
		int myStrategy; // Variaveis que guardarao o valor de cada estrategia nas iteracoes
		int enemyStrategy;
		int earnDifference = 0; // Diferenca dos ganhos das estrategias (my - enemy)
		int bestStrategy = 0; // Variavel para segurar o valor da melhor estrategia
		// Variavel que vai dizer que acao deve ser feita pelo jogador (0 = moveToAlly | 1 = moveToEnemy | 2 = Attack)
		int choosenStrategy = 0;
		
		
		for(int i = 0; i < 3; i++){
			// Estrategias proprias
			if(i == 0){
				myStrategy = moveToAlly;
			}else if(i == 1){
				myStrategy = moveToEnemy;
			}else{
				myStrategy = attack;
			}
			
			for(int j = 0; j < 3; j++){
				// Estrategias do inimigo
				if(j == 0){
					enemyStrategy = enemyMove;
				}else if(j == 1){
					enemyStrategy = enemyAttack;
				}else{
					enemyStrategy = enemyHeal;
				}
				
				earnDifference = myStrategy - enemyStrategy; // Calculando "vantagem" da estrategia pessoal sobre a inimiga
				if(earnDifference > bestStrategy){ // Se a nova diferenca maior que a ultima melhor estrategia
					bestStrategy = earnDifference; // Recebe nova melhor estrategia
					if(i == 0){
						choosenStrategy = 0; // moveToAlly
					}else if(i == 1){
						choosenStrategy = 1; // moveToEnemy
					}else{
						choosenStrategy = 2; // attack
					}
				}
			}
		}
		// Para saber o valor de cada estrategia ao fim dos calculos de cada rodada (APAGAR)
		/*System.out.println("////////\n" +
						   moveToAlly + "\n" +
						   moveToEnemy + "\n" +
						   attack + "\n" +
						   "////////\n" +
						   enemyMove + "\n" +
						   enemyHeal + "\n" +
						   enemyAttack);*/
		
		switch(choosenStrategy){
		default: // moveToAlly
			this.moveTo(allySup);
			break;
		case 1: // moveToEnemy
			this.moveTo(enemy);
			break;
		case 2: // Attack
			Character.attackAction(this, enemy, chosenAttack);
			break;
		}
		
		if(this.getCondition() == 3){ // Se estiver gravemente ferido
			// Envio da mensagem de pedido de cura
			if(this.getTeam() == 'a'){
				grid.mailBoxA.add(new Letter(this.getName(), 1, null));
			}else{
				grid.mailBoxB.add(new Letter(this.getName(), 1, null));
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
				if(ownTeam.get(i).getRole() == 4){ // Se o time tem suporte
					if( this.distanceTo(ownTeam.get(i).getPositionX(),ownTeam.get(i).getPositionY()) == 1 ){
						//Esta colado no suporte, apenas ataca
						if( this.distanceTo(enemy.getPositionX(), enemy.getPositionY()) == 1 ){
							int mean=0;
							Attack chosenAttack = null;
							for(int j = 0; j< this.attacks.size(); j++){
								if(attacks.get(j).mean() > mean)
									chosenAttack = attacks.get(j);
									mean = chosenAttack.mean();
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
				//INSERIR FUN��O DE ATAQUE AT� A MORTE AQUI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			}
		}
		else{
			//Ataca
			this.moveTo(enemy);
			int mean=0;
			Attack chosenAttack = null;
			for(int j = 0; j< this.attacks.size(); j++){
				if(attacks.get(j).mean() > mean)
					chosenAttack = attacks.get(j);
					mean = chosenAttack.mean();
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
