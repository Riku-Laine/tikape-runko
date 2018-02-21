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
import org.thymeleaf.context.IWebContext;
import tikape.runko.database.Database;
import tikape.runko.domain.RaakaAine;
import tikape.runko.domain.RaakaAineDao;
import tikape.runko.database.DrinkkiDao;
import tikape.runko.domain.Drinkki;


public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:drinkkilista.db");

        RaakaAineDao raakaAineDao = new RaakaAineDao(database);
        DrinkkiDao drinkkiDao = new DrinkkiDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());    
        
        get("/ainekset", (req, res) -> {
            HashMap map = new HashMap<>();
            List<RaakaAine> aineoliot = raakaAineDao.findAll();
            List<String> esiintymiskerrat = raakaAineDao.getNumberOfOccurrences();
            for (RaakaAine raakaAine : aineoliot) {
                System.out.println(raakaAine.toString());
                System.out.println(esiintymiskerrat);
            }
            map.put("ainekset", raakaAineDao.findAll());
            map.put("kerrat", esiintymiskerrat);

            return new ModelAndView(map, "ainekset");
        }, new ThymeleafTemplateEngine());

        get("/drinkit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", drinkkiDao.findAll());

            return new ModelAndView(map, "drinkit");
        }, new ThymeleafTemplateEngine());

        get("/drinkki/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT raakaaine.id, raakaaine.nimi FROM raakaaine, AnnosRaakaAine WHERE annos_id = (?) AND raaka_aine_id = raakaaine.id");
            stmt.setInt(1, Integer.parseInt(req.params(":id")));
            ResultSet rs = stmt.executeQuery();

            ArrayList<RaakaAine> raakaaineet = new ArrayList<>();
            while (rs.next()) {
                raakaaineet.add(new RaakaAine(rs.getInt("id"), rs.getString("nimi")));
            }
            
            // Lisätään "lähtevään pakettiin" mukaan kaikki raaka-aineet, jotta
            // ne saadaan mukaan valikkoon.
            List<RaakaAine> kaikkiAineet = raakaAineDao.findAll();
            
            map.put("ainekset", raakaaineet);
            map.put("drinkki", drinkkiDao.findOne(Integer.parseInt(req.params(":id"))));
            map.put("kaikkiAineet", kaikkiAineet);

            return new ModelAndView(map, "DrinkkiOhje");
        }, new ThymeleafTemplateEngine());

        post("/ainekset", (req, res) -> {
            System.out.println("!!");
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO raakaaine (nimi) VALUES (?)");
            stmt.setString(1, req.queryParams("nimi"));
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            res.redirect("/ainekset");
            return "";
        });

        post("/drinkit", (req, res) -> {
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Annos (nimi) VALUES (?)");
            stmt.setString(1, req.queryParams("nimi"));
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            res.redirect("/drinkit");
            return " ";
        });

        post("/drinkki/:id/ohje", (req, res) -> {
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE Annos SET ohje= (?) WHERE annos.id= (?) ");
            stmt.setString(1, req.queryParams("ohje"));
            stmt.setString(2, req.params(":id"));

            stmt.executeUpdate();
            stmt.close();
            conn.close();
            res.redirect("/drinkki/" + req.params(":id"));
            return "";

        });

        post("/drinkki/:id", (req, res) -> {
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO AnnosRaakaAine (annos_id, raaka_aine_id) VALUES (?, ?)");
            stmt.setInt(1, Integer.parseInt(req.params(":id")));
            stmt.setInt(2, Integer.parseInt(req.queryParams("id")));

            stmt.executeUpdate();

            stmt.close();
            conn.close();
            res.redirect("/drinkki/" + req.params(":id"));

//            res.redirect("/drinkki/:id");
            return "";
        });

    }
}
