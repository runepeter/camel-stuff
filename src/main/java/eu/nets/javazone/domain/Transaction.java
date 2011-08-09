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
    private long amount;


    private Transaction() {
        // hibernate
    }

    public Transaction(String creditAccount, String debetAccount, long amount) {
        this.creditAccount = creditAccount;
        this.debetAccount = debetAccount;
        this.amount = amount;
    }

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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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

    public static Transaction parse(final String transaction) {

        String[] parts = transaction.trim().split(";");

        return new Transaction(parts[0], parts[1], Long.parseLong(parts[2]));
    }
}
