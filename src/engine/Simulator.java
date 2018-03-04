package engine;

import characters.Character;
import characters.MDps;
import characters.Support;
import characters.Tanker;
import mechanics.Attack;
import mechanics.Spell;
import engine.GA_Map;

import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Scariottis on 27/03/2017.
 */
public class Simulator {

    private File[] CharacterSheets;
    private ArrayList<Character> chars = new ArrayList<>();
    public static Grid grid;

    public Simulator(File[] CSheets) throws IOException {
        this.CharacterSheets = CSheets;
        this.createCharacters();
        this.createGrid();
    }

    public void createCharacters() throws FileNotFoundException, IOException{
        File c = CharacterSheets[0];
        String sCurrentLine;
        BufferedReader br = new BufferedReader(new FileReader(c));

        //mechanics.Attack & Spells List
        ArrayList<Attack> atkList = new ArrayList<>();
        ArrayList<Spell> spellList = new ArrayList<>();

        // General characters.Character Variables
        int role = -1, hp = -1, ac= -1, init= -1, spd= -1, fort= -1, refl= -1, will= -1;
        String name = "";
        char size = 'm';

        //mechanics.Attack & Spells Variables
        int range = 1, critChance = 20, critMult = 2, splType = -1, saveDC;
        String atkName = "a", splName = "", atkDmg = "", extDmg, castTime = "", splEffect = "";
        int[] atkBonus = new int[3]; atkBonus[0] = 0; atkBonus[1] = 0; atkBonus[2] = 0;
        char splSave = 'n';

        //Flags & General Purpose Variables
        boolean generalCharacteristicsFlag = true, attacksFlag = false, spellsFlag = false;
        String s;

        for(int i = 0; i<CharacterSheets.length; i++){
            c = CharacterSheets[i];
            br = new BufferedReader(new FileReader(c));
            while ((sCurrentLine = br.readLine()) != null) {
                //First, get characters.Character general information
                if(generalCharacteristicsFlag){
                    if(sCurrentLine.length() > 9){
                        if(sCurrentLine.substring(0,10).equals("Initiative:")){
                            //Get characters.Character Initiative
                            s = sCurrentLine.substring(12,sCurrentLine.length());
                            init = Integer.parseInt(s);
                        }else if(sCurrentLine.substring(0,10).equals("Fortitude:")){
                            //Get characters.Character Fortitude
                            s = sCurrentLine.substring(11,sCurrentLine.length());
                            fort = Integer.parseInt(s);
                        }else if(sCurrentLine.substring(0,9).equals("Reflexes:")){
                            //Get characters.Character Reflexes
                            s = sCurrentLine.substring(10,sCurrentLine.length());
                            refl = Integer.parseInt(s);
                        }
                    }
                    if(sCurrentLine.length() > 5){
                        if(sCurrentLine.substring(0,5).equals("Name:")){
                            //Get characters.Character Name
                            name = sCurrentLine.substring(6,sCurrentLine.length());
                        }else if(sCurrentLine.substring(0,5).equals("Role:")){
                            //Get characters.Character Role
                            if(sCurrentLine.substring(6,7).equals("1")){
                                role = 1;
                            }else if(sCurrentLine.substring(6,7).equals("2")){
                                role = 2;
                            }else if(sCurrentLine.substring(6,7).equals("3")){
                                role = 3;
                            }else{
                                role = 4;
                            }
                        }else if(sCurrentLine.substring(0,3).equals("HP:")){
                            //Get characters.Character HP
                            s = sCurrentLine.substring(4,sCurrentLine.length());
                            hp = Integer.parseInt(s);
                        }else if(sCurrentLine.substring(0,3).equals("AC:")){
                            //Get characters.Character AC
                            s = sCurrentLine.substring(4,sCurrentLine.length());
                            ac = Integer.parseInt(s);
                        }else if(sCurrentLine.substring(0,5).equals("Size:")){
                            //Get characters.Character Size
                            s = sCurrentLine.substring(6,7);
                            size = s.charAt(0);
                        }else if(sCurrentLine.substring(0,6).equals("Speed:")){
                            //Get characters.Character Speed
                            s = sCurrentLine.substring(7,sCurrentLine.length());
                            spd = Integer.parseInt(s);
                        }else if(sCurrentLine.substring(0,5).equals("Will:")){
                            //Get characters.Character Will
                            s = sCurrentLine.substring(6,sCurrentLine.length());
                            will = Integer.parseInt(s);
                            generalCharacteristicsFlag = false; //Finish to collect the general stuff about the characters.Character
                            attacksFlag = true; //Time to get the attacks
                        }
                    }
                    // Second, get Attacks
                }else if(attacksFlag){
                    if(sCurrentLine.length() > 9){
                        if(sCurrentLine.substring(0,10).equals("Atk Bonus:")) {
                            //Get mechanics.Attack Bonus
                            s = sCurrentLine.substring(11, sCurrentLine.length());
                            String[] atks = s.split(" ");
                            atkBonus[0] = Integer.parseInt(atks[0]);
                            if(atks.length > 1) {
                                atkBonus[1] = Integer.parseInt(atks[1]);
                                if(atks.length > 2) {
                                    atkBonus[2] = Integer.parseInt(atks[2]);
                                }
                            }
                        }else if(sCurrentLine.substring(0,8).equals("Atk Dmg:")){
                            //Get mechanics.Attack Dmg
                            atkDmg = sCurrentLine.substring(9, sCurrentLine.length());
                        }else if(sCurrentLine.substring(0,12).equals("Crit Chance:")){
                            //Get Crit Chance of mechanics.Attack
                            s = sCurrentLine.substring(13, sCurrentLine.length());
                            critChance = Integer.parseInt(s);
                        }else if(sCurrentLine.substring(0,10).equals("Crit Mult:")){
                            //Get Crit Multiplier of mechanics.Attack
                            s = sCurrentLine.substring(11, sCurrentLine.length());
                            critMult = Integer.parseInt(s);
                        }else if (sCurrentLine.substring(0, 5).equals("Name:")) {
                            //Get mechanics.Attack Name
                            atkName = sCurrentLine.substring(6, sCurrentLine.length());
                        }else if(sCurrentLine.substring(0,10).equals("Extra Dmg:")){
                            //Get Extra Damage of mechanics.Attack
                            extDmg = sCurrentLine.substring(11, sCurrentLine.length());

                            //Create mechanics.Attack
                            int[] atkBns = new int[3];
                            System.arraycopy( atkBonus, 0, atkBns, 0, atkBonus.length );

                            Attack a = new Attack(atkName, range, atkBns, atkDmg, extDmg, critChance, critMult);
                            atkList.add(a);
                            atkBonus[0] = 0; atkBonus[1] = 0; atkBonus[2] = 0; //Reset Atk Bonus Array
                        }
                    }else if(sCurrentLine.length() > 5){
                        if(sCurrentLine.substring(0,7).equals("Spells:")){
                            attacksFlag = false; //Finish to collect Attacks
                            spellsFlag = true; //Time to get the spells
                        }else if(sCurrentLine.substring(0,6).equals("Range:")){
                            //Get mechanics.Attack Range
                            s = sCurrentLine.substring(7, sCurrentLine.length());
                            range = Integer.parseInt(s);
                        }
                    }
                }else if(spellsFlag){
                    if(sCurrentLine.length() > 10) {
                        if (sCurrentLine.substring(0, 10).equals("Cast Time:")) {
                            //Get mechanics.Spell Cast Time
                            castTime = sCurrentLine.substring(11, sCurrentLine.length());
                        }
                    }if(sCurrentLine.length() > 7){
                        if(sCurrentLine.substring(0, 8).equals("Save DC:")) {
                            //Get mechanics.Spell Save DC
                            s = sCurrentLine.substring(9, sCurrentLine.length());
                            saveDC = Integer.parseInt(s);

                            //Create mechanics.Spell
                            Spell sp = new Spell(splName, range, castTime, splType, splEffect, splSave, saveDC);
                            spellList.add(sp);
                        }
                    }if(sCurrentLine.length() > 6){
                        if (sCurrentLine.substring(0, 5).equals("Name:")) {
                            //Get mechanics.Spell Name
                            splName = sCurrentLine.substring(6, sCurrentLine.length());
                        }else if (sCurrentLine.substring(0, 6).equals("Range:")) {
                            //Get mechanics.Spell Range
                            s = sCurrentLine.substring(7, sCurrentLine.length());
                            range = Integer.parseInt(s);
                        }else if (sCurrentLine.substring(0, 5).equals("Type:")) {
                            //Get mechanics.Spell Type (Damage[0] or Heal[1])
                            s = sCurrentLine.substring(6, sCurrentLine.length());
                            splType = Integer.parseInt(s);
                        }else if (sCurrentLine.substring(0, 7).equals("Effect:")) {
                            //Get mechanics.Spell Effect
                            splEffect = sCurrentLine.substring(8, sCurrentLine.length());
                        }else if(sCurrentLine.substring(0, 5).equals("Save:")){
                            //Get mechanics.Spell Save
                            s = sCurrentLine.substring(6, sCurrentLine.length());
                            splSave = s.charAt(0);
                        }
                    }
                }
            }
            ArrayList<Attack> attacks = (ArrayList<Attack>)atkList.clone();
            ArrayList<Spell> spells = (ArrayList<Spell>)spellList.clone();
            
            switch(role){
            case 1: Character charTanker = new Tanker(name, role, hp, ac, init, size, spd, fort, refl, will, attacks, spells);
            		chars.add(charTanker);
            		break;
            case 2: Character charMdps = new MDps(name, role, hp, ac, init, size, spd, fort, refl, will, attacks, spells);
            		chars.add(charMdps);
            		break;			
            case 4: Character charSupport = new Support(name, role, hp, ac, init, size, spd, fort, refl, will, attacks, spells);
            		chars.add(charSupport);
            		break;
            }
            
            
            generalCharacteristicsFlag = true; attacksFlag = false; spellsFlag = false; //Resets Flags & Necessary Variables
            atkList.clear(); spellList.clear();
        }
        br.close(); //Close Reading Buffer
    }

    public void createGrid(){
        //Hardcoded test Team & Map Creator
        ArrayList<Character> teamA = new ArrayList<Character>();
        ArrayList<Character> teamB = new ArrayList<Character>();
        
        this.chars.get(0).setTeam('a');
        this.chars.get(1).setTeam('a');
        this.chars.get(2).setTeam('b');
        this.chars.get(3).setTeam('a');
        
        int[][] mapArray = GA_Map.geneticMap();
        /*int[][] mapArray = new int[][]{{1,1,1,1,0,0,0,1,1},
        {1,1,1,1,0,0,0,1,1},
        {0,0,1,1,1,0,1,1,1},
        {0,1,1,1,1,1,1,1,0},
        {0,1,1,1,1,1,1,0,0}};*/
        
        int xA = 0,yA=0;
        int xB=4;
        int yB=8;
        for(int i=0;i<this.chars.size();i++)
        {
        	if(this.chars.get(i).getTeam()=='a')
        	{
        		while(true)
        		{
        			if(mapArray[xA][yA]==1)
        			{
        				this.chars.get(i).setInitialPosition(xA, yA);
        				if(yA==8)
        				{
        					yA=0;
        					xA++;
        				}
        				else
        				{
        					yA++;
        				}
        				break;
        			}
        			else
        			{
        				if(yA==8)
        				{
        					yA=0;
        					xA++;
        				}
        				else
        				{
        					yA++;
        				}
        			}
        		}
        	}
        	else
        	{
        		while(true)
        		{
        			if(mapArray[xB][yB]==1)
        			{
        				this.chars.get(i).setInitialPosition(xB, yB);
        				if(yB==0)
        				{
        					yB=8;
        					xB--;
        				}
        				else
        				{
        					yB--;
        				}
        				break;
        			}
        			else
        			{
        				if(yB==0)
        				{
        					yB=8;
        					xB--;
        				}
        				else
        				{
        					yB--;
        				}
        			}
        		}
        	}
        }

        /*this.chars.get(0).setInitialPosition(0, 1);
        this.chars.get(1).setInitialPosition(1, 0);
        this.chars.get(2).setInitialPosition(1, 8);
        this.chars.get(3).setInitialPosition(0, 0);*/

        teamA.add(this.chars.get(0));
        teamA.add(this.chars.get(1));
        teamB.add(this.chars.get(2));
        teamA.add(this.chars.get(3));



        this.grid = new Grid(teamA, teamB, 5, 9, mapArray);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File f[] = new File[4
                            ];
        File a = new File("sheets/Spirilon Pirilampus.txt");
        File b = new File("sheets/Skarza.txt");
        File c = new File("sheets/Big O'neil.txt");
        File d = new File("sheets/Fred.txt");
        f[0] = a;
        f[1] = b;
        f[2] = c;
        f[3] = d;
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o numero de simulacoes:" );
        int simulations = scanner.nextInt();
     
        //s.grid.printGrid();
        boolean verifyA = false;
        boolean verifyB = false;
        for(int i = 1; i <= simulations; i++){
        	System.out.println("\nSIMULACAO " + i + "\n");
        	Simulator s = new Simulator(f);
        	s.grid.printTeams();
            s.grid.rollCharactersInitiative();
            //s.grid.printGrid();
	        while(true){
	        	verifyA = grid.verifyVictory(grid.getTeamA());
	        	verifyB = grid.verifyVictory(grid.getTeamB());
	        	if((verifyA) || (verifyB)){
	        		break;
	        	}
	        	grid.callTurn();
	        	//s.grid.printGrid();
	            //scanner.nextLine();
	        }
	        if(verifyA){
	        	System.out.println("TeamB ganhou");
	        }else{
	        	System.out.println("TeamA ganhou");
	        }
        }
    }
}
