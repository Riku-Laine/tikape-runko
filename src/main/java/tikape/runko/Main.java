

package tikape.runko;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import sun.security.util.Cache;
import tikape.runko.database.Database;
import tikape.runko.domain.Aines;
import tikape.runko.database.AinesDao;
import tikape.runko.database.DrinkkiDao;
import tikape.runko.domain.Drinkki;


public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:database.db");

        AinesDao ainesDao = new AinesDao(database);
        DrinkkiDao drinkkiDao = new DrinkkiDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/ainekset", (req, res) -> {
            HashMap map = new HashMap<>();
            List<Aines> aineoliot = ainesDao.findAll();
            List<String> esiintymiskerrat = ainesDao.getNumberOfOccurrences();
            for (Aines raakaAine : aineoliot) {
                System.out.println(raakaAine.toString());
                System.out.println(esiintymiskerrat);
            }
            map.put("ainekset", ainesDao.findAll());
            map.put("kerrat", esiintymiskerrat);

            return new ModelAndView(map, "ainekset");
        }, new ThymeleafTemplateEngine());

        get("/drinkit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", drinkkiDao.findAll());

            return new ModelAndView(map, "drinkit");
        }, new ThymeleafTemplateEngine());

        get("/drinkit/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT Aines.id, Aines.nimi, Drinkki.ohje, DrinkkiAines.määrä FROM Aines, DrinkkiAines, Drinkki WHERE Drinkki_id = (?) AND Aines_id = Aines.id AND Drinkki_id=Drinkki.id");
            stmt.setInt(1, Integer.parseInt(req.params(":id")));
            ResultSet rs = stmt.executeQuery();

            ArrayList<Aines> ainekset = new ArrayList<>();
            while (rs.next()) {
                ainekset.add(new Aines(rs.getInt("id"), rs.getString("nimi"), rs.getString("määrä")));
            }
            
            // Lisätään "lähtevään pakettiin" mukaan kaikki raaka-aineet, jotta
            // ne saadaan mukaan valikkoon.
            List<Aines> kaikkiAineet = ainesDao.findAll();
            
            map.put("ainekset", ainekset);
            map.put("drinkki", drinkkiDao.findOne(Integer.parseInt(req.params(":id"))));
            map.put("kaikkiAinekset", kaikkiAineet);

            return new ModelAndView(map, "DrinkkiOhje");
        }, new ThymeleafTemplateEngine());

        post("/ainekset", (req, res) -> {
            System.out.println("!!");
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Aines (nimi) VALUES (?)");
            stmt.setString(1, req.queryParams("nimi"));
            
            if(req.queryParams("nimi").length() != 0) {
            stmt.executeUpdate();
            }

            stmt.close();
            conn.close();

            res.redirect("/ainekset");
            return "";
        });

        post("/drinkit", (req, res) -> {
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Drinkki (nimi) VALUES (?)");
            stmt.setString(1, req.queryParams("nimi"));
            if(req.queryParams("nimi").length() != 0) {
            stmt.executeUpdate();
            }

            stmt.close();
            conn.close();

            res.redirect("/drinkit");
            return " ";
        });

        post("/drinkit/:id/ohje", (req, res) -> {
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE Drinkki SET ohje= (?) WHERE Drinkki.id= (?) ");
            stmt.setString(1, req.queryParams("ohje"));
            stmt.setString(2, req.params(":id"));

            stmt.executeUpdate();
            stmt.close();
            conn.close();
            res.redirect("/drinkit/" + req.params(":id"));
            return "";

        });

        post("/drinkit/:id", (req, res) -> {
            //toimii
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO DrinkkiAines (Drinkki_id, Aines_id, määrä) VALUES (?, ?, ?)");
            stmt.setInt(1, Integer.parseInt(req.params(":id")));
            stmt.setInt(2, Integer.parseInt(req.queryParams("id")));
            stmt.setString(3, req.queryParams("maara"));
            
            if(!req.queryParams("maara").isEmpty())
                stmt.executeUpdate();

            stmt.close();
            conn.close();
            
            res.redirect("/drinkit/" + req.params(":id"));
            
            return "";
        });

    }
}

