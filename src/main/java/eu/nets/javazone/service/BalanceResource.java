package eu.nets.javazone.service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import eu.nets.javazone.domain.Fil;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.io.Writer;
import java.util.List;

@Controller
@RequestMapping("balance/")
@Transactional
public class BalanceResource {

    @Autowired
    private DataSource dataSource;

    @RequestMapping(value = "antall/", method = RequestMethod.GET)
    @ResponseBody
    public String getAntall() {

        SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(dataSource);
        return "" + jdbc.queryForInt("SELECT COUNT(*) FROM BALANCE");
    }

}
