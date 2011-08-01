package eu.nets.javazone.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table
public class Transaction {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "TRANSACTION_SEQ")
    @SequenceGenerator(name = "TRANSACTION_SEQ", sequenceName = "transaction_id_sequence", allocationSize = 20)
    private Long id;



    private String creditAccount;

    private String debetAccount;

    private String currency;
    private String amount;
    private String creditorName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(String creditAccount) {
        this.creditAccount = creditAccount;
    }

    public String getDebetAccount() {
        return debetAccount;
    }

    public void setDebetAccount(String debetAccount) {
        this.debetAccount = debetAccount;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    /*
    public static TransactionFinderSpecification find() {
        return new TransactionFinderSpecification();
    }
    */

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(91, 293);
        if (id == null) {
            return super.hashCode();
        }
        return builder.append(id).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transaction)) {
            return false;
        }

        Transaction o = (Transaction) obj;
        if (id == null) {
            return false;
        }
        return id.equals(o.id);
    }
}
