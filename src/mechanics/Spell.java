package mechanics;

public class Spell {
	private String name;
	private int range;
	private String castTime; //full-round, standard ou swift
	private int type; //0 - dmg ou 1 - heal
	private String effect;
	private char save; //f - fortitude, r - reflex, w - will, n - none
	private int saveDC;
	
	public Spell(String name, int range, String castTime, int type, String effect, char save, int saveDC) {
		super();
		this.name = name;
		this.range = range;
		this.castTime = castTime;
		this.type = type;
		this.effect = effect;
		this.save = save;
		this.saveDC = saveDC;
	}

	public void printSpell(){
		System.out.println("Name: " + this.name + "\n" +
				"Range: " + this.range + "\n" +
				"Cast Time: " + this.castTime + "\n" +
				"Type: " + this.type + "\n" +
				"Effect: " + this.effect + "\n" +
				"Save: " + this.save + "\n" +
				"Save DC: " + this.saveDC + "\n");
	}

	public String getName() {return name;}

	public int getRange() {return range;}

	public String getCastTime() {return castTime;}
	
	public int getCastTimeInt()
	{
		if(castTime == "full-round")
		{
			return 1;
		}
		if(castTime == "standart")
		{
			return 2;
		}
		else
		{
			return 3;
		}
	}

	public int getType() {return type;}

	public String getEffect() {return effect;}

	public char getSave() {return save;}

	public int getSaveDC() {return saveDC;}

	public int mean() {
		//Separar os números do ataque.
		int typeOfDice, numberOfDice, modifier;
		int i=0;
		String[] parser = new String[3];
		parser = this.getEffect().split("d");
		numberOfDice = Integer.parseInt(parser[0]);
		parser = parser[1].split("\\+");
		typeOfDice = Integer.parseInt(parser[0]);
		modifier = Integer.parseInt(parser[1]);

		int maxValue = typeOfDice*numberOfDice;
		int mean = (numberOfDice + maxValue)/2 +modifier;
		return mean;

	}
	
}
