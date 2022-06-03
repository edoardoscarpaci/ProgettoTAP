package progetto.TAP;

import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable{

private String steps;
private String name;
private List<String> ingredients;
private String link; 
public Recipe(String steps,String name,List<String> ingredients,String link) { 
	super();
	this.steps = steps;
	this.name = name;
	this.ingredients = ingredients;
	this.link = link;
}

public String getName(){return this.name;}
public String getSteps(){return this.steps;}
public List<String> getIngredients(){return this.ingredients;}
public String getLink(){return this.link;}


public void setName(String name){this.name = name;}
public void setSteps(String steps){this.steps = steps;}
public void setIngredients(List<String> ingredients){this.ingredients = ingredients;}
public void setLink(String link){this.link = link;}

}
