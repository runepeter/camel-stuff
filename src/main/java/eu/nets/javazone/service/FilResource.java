package eu.nets.javazone.service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import eu.nets.javazone.domain.Fil;
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
@RequestMapping("fil/")
@Transactional
public class FilResource {

     @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String getFiler() {
        StringBuilder builder = new StringBuilder("[");
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Fil.class);

        List list = criteria.list();

        for (int i = 0; i < list.size(); i++) {
            Fil fil = (Fil) list.get(i);
            String json = toJson(fil);
            builder.append(json);

        }
        builder.append("]");
        return builder.toString();

    }


    private String toJson(Fil fil) {
        XStream xstream = new XStream(new JsonHierarchicalStreamDriver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer writer) {
                return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
            }
        });

        xstream.setMode(XStream.ID_REFERENCES);
        return xstream.toXML(fil);
    }

}
