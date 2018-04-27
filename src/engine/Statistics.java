package engine;

public class Statistics {
	
	private String name;
	public int totalDamage = 0;
  	public int totalHeal = 0;
  	
  	public Statistics(String name){
  		this.name = name;
  	}
  	
  	public String getName(){
  		return this.name;
  	}
  	
  	public int getTotalDamage(){
  		return this.totalDamage;
  	}
  	
  	public int getTotalHeal(){
  		return this.totalHeal;
  	}
  	
  	public int getAvgDamage(){
  		return (this.totalDamage/engine.Simulator.simulations);
  	}
  	
  	public int getAvgHeal(){
  		return (this.totalHeal/engine.Simulator.simulations);
  	}
  	
  	public void addTotalDamage(int damage){
  		this.totalDamage += damage;
  	}
  	
  	public void addTotalHeal(int heal){
  		this.totalHeal += heal;
  	}
}
