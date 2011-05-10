package eu.nets.camel.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "receipt")
public class ShipmentReceipt implements Serializable
{
    private String filename;
    private String message;

    public ShipmentReceipt()
    {
    }

    public ShipmentReceipt(final String filename, final String message)
    {
        this.filename = filename;
        this.message = message;
    }

    @Id
    @Column(name = "filename")
    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    @Column(name = "message", updatable = false, nullable = false)
    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return getMessage();
    }
}
