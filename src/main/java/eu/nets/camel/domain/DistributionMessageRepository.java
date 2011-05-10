package eu.nets.camel.domain;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DistributionMessageRepository
{
    private final HibernateTemplate hibernate;

    @Autowired
    public DistributionMessageRepository(final SessionFactory sessionFactory)
    {
        this.hibernate = new HibernateTemplate(sessionFactory);
    }

    public long save(final DistributionMessage message) {
        hibernate.saveOrUpdate(message);
        hibernate.flush();
        hibernate.clear();
        return message.getId();
    }

    public List<DistributionMessage> getAll() {
        return hibernate.find("from DistributionMessage");
    }

}
