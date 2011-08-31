package eu.nets.javazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("balance/")
@Transactional
public class BalanceResource {

    @Autowired
    private DataSource dataSource;

    @RequestMapping(value = "saldo/", method = RequestMethod.GET)
    @ResponseBody
    public String getSaldo() {

        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(dataSource);
        List<Map<String,Object>> list = jdbc.queryForList("SELECT saldo FROM BALANCE");
        if (list != null && list.size() > 0) {
            return "" + jdbc.queryForInt("SELECT saldo FROM BALANCE where account = '11111111111'");
        } else {
            return "0";
        }
    }

    @RequestMapping(value = "reserved/", method = RequestMethod.GET)
    @ResponseBody
    public String getReserved() {

        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(dataSource);
        long l = jdbc.queryForLong("select sum(saldo) from reserved where account = '11111111111'");

        return "" + l;
    }

    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String reset() {

        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(dataSource);
        List<Map<String, Object>> list = jdbc.queryForList("select * from balance");
        jdbc.update("delete from balance");
        jdbc.update("insert into balance values ('11111111111', 2000000)");
        jdbc.update("delete from reserved");
        jdbc.update("delete from aggregation");
        jdbc.update("delete from aggregation_completed");
        return "redirect:/admin/";
    }

}
