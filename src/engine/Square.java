package engine;

import characters.Character;

/**
 * Created by Scariottis on 21/03/2017.
 */
public class Square {
    private int x, y;
    private Character c = null;
    private int mapFlag; //0 for walls, 1 for passage
    private boolean visible = true;

    public Square(int x, int y, int mapFlag){
        this.x = x;
        this.y = y;
        this.mapFlag = mapFlag;
    }

    public Character getC() {return this.c;}

    public int getMapFlag() {return mapFlag;}

    public int getX() {return x;}

    public int getY() {return y;}

    public void setCharacter(Character c) {this.c = c;
    										}

    public void leaveSquare(){this.c = null;}

    public void setVisible(boolean vision) {this.visible = vision;}

    public boolean getVisible(){return this.visible;}
    
    public void setMapFlag(int mapFlag) {
		this.mapFlag = mapFlag;
	}
}
