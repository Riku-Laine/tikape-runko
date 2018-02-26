
package tikape.runko.domain;


public class Aines {
    
    private String nimi;
    private int id;
    private String maara;
    
    
    public Aines(int id, String nimi){
        this.id = id;
        this.nimi = nimi;
    }
    
    public Aines(int id, String nimi, String maara){
        this.id = id;
        this.nimi = nimi;
        this.maara = maara;
    }
    
    public String getNimi(){
        return this.nimi;
    }
    public int getId(){
        return this.id;
    }
    public void setID(int id){
        this.id = id;
    }
    public void setNimi(String nimi){
        this.nimi = nimi;
    }
    
}
