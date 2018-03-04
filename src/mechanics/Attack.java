package mechanics;

import java.util.Arrays;

public class Attack {
	
	private String name;
	private int range;
	private int[] atkBonus;
	private String atkDmg, extraDmg;
	private int critChance;
	private int critMult;
	
	public Attack(String name, int range, int[] atkBonus, String atkDmg, String extraDmg, int critChance, int critMult) {
		super();
		this.name = name;
		this.range = range;
		this.atkBonus = atkBonus;
		this.atkDmg = atkDmg;
		this.extraDmg = extraDmg;
		this.critChance = critChance;
		this.critMult = critMult;
	}

	
	public void printAttack(){
		System.out.println("Name: " + this.name + "\n" +
				"Range: " + this.range + "\n" +
				"Atk Bonus: " + Arrays.toString(atkBonus) + "\n" +
				"Atk Dmg: " + this.atkDmg + "\n" +
				"Crit Chance: " + this.critChance + "\n" +
				"Crit Mult: " + this.critMult + "\n" +
				"Extra Dmg: " + this.extraDmg + "\n");
	}

	public String getName() {
		return name;
	}

	public int getRange() {
		return range;
	}

	public int[] getAtkBonus() {
		return atkBonus;
	}

	public String getAtkDmg() {
		return atkDmg;
	}

	public String getExtraDmg() {
		return extraDmg;
	}

	public int getCritChance() {
		return critChance;
	}

	public int getCritMult() {
		return critMult;
	}

	public int mean() {
		//Separar os números do ataque.
		int typeOfDice, numberOfDice, modifier;
		int i=0;
		String[] parser = new String[3];
		parser = this.getAtkDmg().split("d");
		numberOfDice = Integer.parseInt(parser[0]);
		parser = parser[1].split("\\+");
		typeOfDice = Integer.parseInt(parser[0]);
		modifier = Integer.parseInt(parser[1]);

		int maxValue = typeOfDice*numberOfDice;
		int mean = (numberOfDice + maxValue)/2 + modifier;
		return mean;

	}

}
