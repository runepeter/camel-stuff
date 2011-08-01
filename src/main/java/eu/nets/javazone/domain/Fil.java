package eu.nets.javazone.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table
public class Fil {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "INTERCHANGE_SEQ")
    @SequenceGenerator(name = "INTERCHANGE_SEQ", sequenceName = "interchange_id_sequence", allocationSize = 1)
    private Long id;

    @Type(type = "org.hibernate.type.MaterializedClobType")
    private String content;

   // private Date receivedDateTime;

    private String originalFilename;


    private Integer numberOfTransactions;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    /*
    public Date getReceivedDateTime() {
        return receivedDateTime;
    }

    public void setReceivedDateTime(Date receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
    }
    */

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }


    public Integer getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(Integer numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }


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
        if (!(obj instanceof Fil)) {
            return false;
        }

        Fil o = (Fil) obj;
        if (id == null) {
            return false;
        }
        return id.equals(o.id);
    }

    /*
    public static FilFinderSpecification find() {
        return new FilFinderSpecification();
    }
    */


}
