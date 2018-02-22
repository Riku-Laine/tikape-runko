
    



package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.database.Database;
import tikape.runko.database.Dao;
import tikape.runko.domain.Aines;

public class AinesDao implements Dao<Aines,Integer> {
    
    private Database database;

    public AinesDao(Database database) {
        this.database = database;
    }

    @Override
    public Aines findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aines WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Aines r = new Aines(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return r;
    }

    @Override
    public  List<Aines> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aines");

        ResultSet rs = stmt.executeQuery();
        List<Aines> ainekset = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            ainekset.add(new Aines(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return ainekset;
    }
    
    
    public List<String> getNumberOfOccurrences() throws SQLException{
        Connection connection = database.getConnection();
        
        // Hae erikseen id ja laske sille sitten tuo countti
        
        
        // Haetaan kaikki raaka-aineet
        List<Aines> raakaAineetListana = findAll();
        List<String> esiintymiskerrat = new ArrayList<>();
        
        // Iteroidaan jokaisen raaka-aineen yli ja lasketaan kaikkien ilmestymiskerrat.
        // Lisätään jokainen kokonaislukuarvo listaan, joka palautetaan käyttäjälle.
        for(int i = 0; i < raakaAineetListana.size(); i++){
            int raakaAineenId = raakaAineetListana.get(i).getId();
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) AS esiintymiskertojenMaara "
                + "FROM DrinkkiAines where Aines_id = ?");
            // Haetaan SQL:n avulla esiintymiskerrat, parsetaan tulos stringiksi
            // ja palautetaan
            stmt.setObject(1, raakaAineenId);
            ResultSet rs = stmt.executeQuery();
            esiintymiskerrat.add(raakaAineetListana.get(i).getNimi() + ", " + rs.getInt("esiintymiskertojenMaara") + " reseptissä");
            rs.close();
            stmt.close();
        }
        
        connection.close();

        return esiintymiskerrat;
    }

    @Override   
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE * FROM Aines WHERE id = ?");
        stmt.setObject(1, key);
        
        
        
        // ei toteutettu
    }

}
    


