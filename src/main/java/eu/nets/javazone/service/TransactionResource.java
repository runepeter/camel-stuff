package eu.nets.javazone.service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import eu.nets.javazone.domain.Transaction;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Writer;
import java.util.List;

@Controller
@RequestMapping("transaction/")
@Transactional
public class TransactionResource {

        @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String getTransactions() {
        StringBuilder builder = new StringBuilder("[");
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Transaction.class);

        List<Transaction> list = criteria.list();

        for (Transaction transaction : list) {
            String json = toJson(transaction);
            builder.append(json);

        }
        builder.append("]");
        return builder.toString();

    }


    private String toJson(Transaction transaction) {
        XStream xstream = new XStream(new JsonHierarchicalStreamDriver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer writer) {
                return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
            }
        });

        xstream.setMode(XStream.ID_REFERENCES);
        return xstream.toXML(transaction);
    }
}
