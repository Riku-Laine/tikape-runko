
package tikape.runko.domain;


public class RaakaAine {
    
    private String nimi;
    private int id;
    
    public RaakaAine(int id, String nimi){
        this.id = id;
        this.nimi = nimi;
    }
    public String getNimi(){
        return this.nimi;
    }
    public int getID(){
        return this.id;
    }
    public void setID(int id){
        this.id = id;
    }
    public void setNimi(String nimi){
        this.nimi = nimi;
    }
    
}
