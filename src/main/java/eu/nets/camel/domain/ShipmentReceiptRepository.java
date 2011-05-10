package eu.nets.camel.domain;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ShipmentReceiptRepository
{
    private final HibernateTemplate hibernate;

    @Autowired
    public ShipmentReceiptRepository(final SessionFactory sessionFactory)
    {
        this.hibernate = new HibernateTemplate(sessionFactory);
    }

    public ShipmentReceiptRepository()
    {
        this.hibernate = null;
    }

    public String save(ShipmentReceipt receipt)
    {
        System.err.println("Storing receipt for file '" + receipt.getFilename() + "'.");
        hibernate.save(receipt);
        return receipt.getFilename();
    }

    public ShipmentReceipt get(final String filename)
    {
        return hibernate.get(ShipmentReceipt.class, filename);
    }

}
