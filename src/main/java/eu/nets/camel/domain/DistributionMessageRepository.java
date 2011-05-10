package eu.nets.camel.domain;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class DistributionMessageRepository
{
    private final HibernateTemplate hibernate;

    @Autowired
    public DistributionMessageRepository(final SessionFactory sessionFactory)
    {
        this.hibernate = new HibernateTemplate(sessionFactory);
    }

    public DistributionMessageRepository()
    {
        this.hibernate = null;
    }

    public long save(final DistributionMessage message) {
        hibernate.saveOrUpdate(message);
        return message.getId();
    }

    public List<DistributionMessage> getAll() {
        return hibernate.find("from DistributionMessage");
    }

}
