package eu.nets.camel.domain;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShipmentReceiptRepository
{
    private final HibernateTemplate hibernate;

    @Autowired
    public ShipmentReceiptRepository(final SessionFactory sessionFactory)
    {
        this.hibernate = new HibernateTemplate(sessionFactory);
    }

    public String save(ShipmentReceipt receipt)
    {
        hibernate.save(receipt);
        hibernate.flush();
        hibernate.clear();
        return receipt.getFilename();
    }

    public ShipmentReceipt get(final String filename)
    {
        return hibernate.get(ShipmentReceipt.class, filename);
    }

}
