/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Drinkki;

/**
 *
 * @author hceetu
 */
public class DrinkkiDao implements Dao{
    
    private Database database;

    public DrinkkiDao(Database database){
        this.database = database;
    }
    @Override
    public Object findOne(Object key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Drinkki WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        
        Drinkki drinkki = new Drinkki(rs.getInt("id"), rs.getString("nimi"), rs.getString("ohje"));
        rs.close();
        stmt.close();
        connection.close();
        
        return drinkki;
    }

    @Override
    public List findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Drinkki");

        ResultSet rs = stmt.executeQuery();
        List<Drinkki> drinkit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String ohje = rs.getString("ohje");

            drinkit.add(new Drinkki(id, nimi, ohje));
        }

        rs.close();
        stmt.close();
        connection.close();

        return drinkit;
    }

    @Override
    public void delete(Object key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Drinkki WHERE id = (?)");
        PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM DrinkkiAines WHERE annos_id = (?)");
        stmt.setObject(1, key);
        stmt2.setObject(1, key);

        stmt.executeQuery();
        stmt2.executeQuery();
        
        stmt.close();
        stmt2.close();
        connection.close();
    }
    
}
