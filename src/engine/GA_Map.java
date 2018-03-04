package engine;

import java.util.ArrayList;
import java.util.Random;

import characters.Character;
import characters.MDps;

public class GA_Map {
	//the cromossome fitness is 0 if it have a configuration tha locks a character away from others.
	private static int avaliation(int[][]map){
		int result=0;
		if(isBubble(map)){
			return 0;
		}
		for(int i=0;i<9;i++){
			for(int j=0;j<5;j++){
				if(map[j][i] == 0){
					result++;
				}
			}
		}
		if(result>22){
			result=44-result;
		}
		return result;
	}
	
	//A vacant space in the grid is chosen, if there are adjacent vacant spaces that can't be reached from it, they won't have their value changed.
	private static boolean isBubble(int[][] map) {
									
		int[][]bubbleMap=new int[][]{{999,999,999,999,999,999,999,999,999},
									{999,999,999,999,999,999,999,999,999},
									{999,999,999,999,999,999,999,999,999},
									{999,999,999,999,999,999,999,999,999},
									{999,999,999,999,999,999,999,999,999}};
		int x=0;
		int y=0;
		for(int i=0;i<5;i++){
			for(int j=0;j<9;j++){
				if(map[i][j]==1){
					x=i;
					y=j;
				}
			}
		}
		mapTraverse(x,y,map,bubbleMap);
		for(int i=0;i<5;i++){
			for(int j=0;j<9;j++){
				if(map[i][j]==1){
					if(bubbleMap[i][j]!=0){
						return true;
					}
				}
			}
		}
		return false;
		
		/*int x=-1;
		int y=-1;
		
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<9;j++)
			{
				if(map[i][j]!=0)
				{
					if(x==-1)
					{
						x=i;
						y=j;
					}
				}
			}
		}
		Character star = new MDps("star",2,1,1,1,'s',1,1,1,1,null,null);
		star.setInitialPosition(x, y);
		ArrayList<Character> teamA = new ArrayList<Character>();
		ArrayList<Character> teamB = new ArrayList<Character>();
		teamA.add(star);
		Grid evalGrid = new Grid(teamA , teamB, 5, 9, map);
		int [][]starGrid = star.aStarGA(x, y,evalGrid);
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<9;j++)
			{
				if(starGrid[i][j]==0)
				{
					if((i!=x)&&(j!=y))
					{
						return true;
					}
				}
			}
		}
		return false;*/
	}
	
	private static void mapTraverse(int x, int y, int[][] map, int[][] bubbleMap) {
		// TODO Auto-generated method stub
		if(bubbleMap[x][y]==0)
		{
			return;
		}
		bubbleMap[x][y]=0;
		//8 posições
		if(y>0){
			if(map[x][y-1]==1){
				if(bubbleMap[x][y-1]!=0){
					mapTraverse(x,y-1,map,bubbleMap);
				}
			}
		}
		if(y<8){
			if(map[x][y+1]==1){
				if(bubbleMap[x][y+1]!=0){
					mapTraverse(x,y+1,map,bubbleMap);
				}
			}
		}
		if(x>0){
			if(map[x-1][y]==1){
				if(bubbleMap[x-1][y]!=0){
					mapTraverse(x-1,y,map,bubbleMap);
				}
			}
			if(y>0){
				if(map[x-1][y-1]==1){
					if(bubbleMap[x-1][y-1]!=0){
						mapTraverse(x-1,y-1,map,bubbleMap);
					}
				}
			}
			if(y<8){
				if(map[x-1][y+1]==1){
					if(bubbleMap[x-1][y+1]!=0){
						mapTraverse(x-1,y+1,map,bubbleMap);
					}
				}
			}
		}
		if(x<4){
			if(map[x+1][y]==1){
				if(bubbleMap[x+1][y]!=0){
					mapTraverse(x+1,y,map,bubbleMap);
				}
			}
			if(y>0){
				if(map[x+1][y-1]==1){
					if(bubbleMap[x+1][y-1]!=0){
						mapTraverse(x+1,y-1,map,bubbleMap);
					}
				}
			}
			if(y<8){
				if(map[x+1][y+1]==1){
					if(bubbleMap[x+1][y+1]!=0){
						mapTraverse(x+1,y+1,map,bubbleMap);
					}
				}
			}
		}
	}

	public static double sumAvaliations(ArrayList<int[][]> population)
	{
		double soma = 0;
		for(int i =0;i<population.size();i++){
			soma = soma+avaliation(population.get(i));
		}
		return soma;
		
	}
	
	private static int roulette(ArrayList<int[][]> population) {
		int i;
		double aux =0;
		Random random = new Random();
		double sum = sumAvaliations(population);
		double limite = random.nextDouble()*sum;
		for(i=0;((i<population.size()) && (aux<limite));i++){
			aux = aux + avaliation(population.get(i));
		}
		i--;
		return i;
	}

	 public static int[][] geneticMap()
	{
		ArrayList<int[][]> population = new ArrayList<int[][]>();
		ArrayList<int[][]> descendants = new ArrayList<int[][]>();
		generatePopulation(population);
		for(int i=0;i<2;i++){
			for(int j=0;j<45;j++){
				int [][] father, mother, child;
				int indexfather, indexmother;
				indexfather= roulette(population);
				indexmother = roulette(population);
				while(indexfather == indexmother){
					indexmother = roulette(population);
				}
				father = population.get(indexfather);
				mother = population.get(indexmother);
				child = crossover(father,mother);
				descendants.add(child);
			}
			population = descendants;
		}
		int [][] best = population.get(0);
		for(int i=1;i<population.size();i++){
			if(avaliation(population.get(i))>avaliation(best))
			{
				best = population.get(i);
			}
		}
		return best;
		
	}

	private static int[][] crossover(int[][] father, int[][] mother) {
		int[][] child = new int[][]{{1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1}};
		for(int i = 0;i<5;i++){
			for(int j = 0;j<9;j++){
				if(father[i][j]==0){
					child[i][j]=0;
				}
				if(mother[i][j]==0){
					child[i][j]=0;
				}
			}
		}
		Random random = new Random();
		boolean mutationChance = random.nextBoolean();
		if(mutationChance){
			mutate(child);
		}
		
		return child;
	}

	private static void mutate(int[][] child) {
		Random random = new Random();
		int lineNumber = 0;
		int columnNumber = 0;
		int mutateNumber = random.nextInt(45);
		while(mutateNumber>8){
			mutateNumber=mutateNumber-9;
			lineNumber++;
		}
		columnNumber=mutateNumber;
		if(child[lineNumber][columnNumber]==0){
			child[lineNumber][columnNumber]=1;
		}
		else{
			child[lineNumber][columnNumber]=0;
		}
	}

	private static void generatePopulation(ArrayList<int[][]> population) {
		int[][] t1 = new int[][]{{0,1,1,1,1,1,1,1,1},
								{1,1,1,1,1,1,1,1,1},
								{1,1,1,1,1,1,1,1,1},
								{1,1,1,1,1,1,1,1,1},
								{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,0,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,0,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,0,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,0,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,0,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,0,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,0,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,0},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{0,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,0,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,0,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,0,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,0,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,0,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,0,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,0,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,0},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{0,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,0,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,0,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,0,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,0,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,0,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,0,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,0,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,0}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{0,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,0,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,0,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,0,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,0,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,0,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,0,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,0,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,0},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{0,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,0,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,0,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,0,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,0,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,0,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,0,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,0,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,0},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
	}
}
