package tikape.runko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;
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
            List<RaakaAine> aineoliot = new ArrayList<>();
            aineoliot = raakaAineDao.findAll();
            for (RaakaAine raakaAine : aineoliot) {
                System.out.println(raakaAine.toString());
            }
            map.put("ainekset", raakaAineDao.findAll());

            return new ModelAndView(map, "ainekset");
        }, new ThymeleafTemplateEngine());

        get("/drinkit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", drinkkiDao.findAll());

            return new ModelAndView(map, "drinkit");
        }, new ThymeleafTemplateEngine());
    }
}
