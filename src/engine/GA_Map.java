package engine;

import java.util.ArrayList;
import java.util.Random;

import characters.Character;
import characters.MDps;

public class GA_Map {
	private static int avaliation(int[][]map)
	{
		int result=0;
		if(isBubble(map))
		{
			return result;
		}
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<5;j++)
			{
				if(map[j][i] == 0)
				{
					result++;
				}
			}
		}
		if(result>15)
		{
			result=result-15;
		}
		return result;
	}
	
	private static boolean isBubble(int[][] map) {
		int x=-1;
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
		return false;
	}
	
	public static double sumAvaliations(ArrayList<int[][]> population)
	{
		double soma = 0;
		for(int i =0;i<population.size();i++)
		{
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
		for(i=0;((i<population.size()) && (aux<limite));i++)
		{
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
		for(int i=0;i<2;i++)
		{
			for(int j=0;j<9;j++)
			{

				int [][] father, mother, child;
				int indexfather, indexmother;
				indexfather= roulette(population);
				indexmother = roulette(population);
				while(indexfather == indexmother)
				{
					indexmother = roulette(population);
				}
				father = population.get(indexfather);
				mother = population.get(indexmother);
				Random cut = new Random();
				int indexCut = cut.nextInt(9);
				child = crossover(father,mother,indexCut);
				descendants.add(child);
				boolean chooseParent = cut.nextBoolean();
				if(chooseParent)
				{
					descendants.add(father);
				}
				else
				{
					descendants.add(mother);
				}
			}
			population = descendants;
		}
		int [][] best = population.get(0);
		for(int i=1;i<population.size();i++)
		{
			if(avaliation(population.get(i))>avaliation(best))
			{
				best = population.get(i);
			}
		}
		return best;
		
	}

	private static int[][] crossover(int[][] father, int[][] mother, int indexCut) {
		int[][] child = new int[][]{{1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1}};
		for(int i = 0;i<5;i++)
		{
			for(int j = 0;j<9;j++)
			{
				if(father[i][j]==0)
				{
					child[i][j]=0;
				}
				if(mother[i][j]==0)
				{
					child[i][j]=0;
				}
			}
		}
		return child;
	}

	private static void generatePopulation(ArrayList<int[][]> population) {
		int[][] t1 = new int[][]{{0,1,1,1,1,1,1,1,1},
								{0,1,1,1,1,1,1,1,1},
								{1,1,1,1,1,1,1,1,1},
								{1,1,1,1,1,1,1,1,1},
								{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,0,1,1,1,1,1,1,1},
						{1,0,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,0,1,1,1,1,1,1},
						{1,1,0,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,0,1,1,1,1,1},
						{1,1,1,0,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,0,1,1,1,1},
						{1,1,1,1,0,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,0,1,1,1},
						{1,1,1,1,1,0,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,0,1,1},
						{1,1,1,1,1,1,0,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,0,1},
						{1,1,1,1,1,1,1,0,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,0},
						{1,1,1,1,1,1,1,1,0},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{0,1,1,1,1,1,1,1,1},
						{0,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,0,1,1,1,1,1,1,1},
						{1,0,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,0,1,1,1,1,1,1},
						{1,1,0,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,0,1,1,1,1,1},
						{1,1,1,0,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,0,1,1,1,1},
						{1,1,1,1,0,1,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,0,1,1,1},
						{1,1,1,1,1,0,1,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,0,1,1},
						{1,1,1,1,1,1,0,1,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,0,1},
						{1,1,1,1,1,1,1,0,1},
						{1,1,1,1,1,1,1,1,1}};
		population.add(t1);
		t1 = new int[][]{{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,1},
						{1,1,1,1,1,1,1,1,0},
						{1,1,1,1,1,1,1,1,0},
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
	}
}
