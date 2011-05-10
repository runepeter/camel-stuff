package eu.nets.camel.mottak;

import eu.nets.camel.domain.DistributionMessage;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.STAXEventReader;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Component
public class DistributionMessageSplitter
{
    public Iterator<DistributionMessage> split(InputStream is)
    {
        System.err.println("SPLIT, BABY!");
        return new MessageFileIterator(is);
    }

    public static class MessageFileIterator implements Iterator<DistributionMessage>
    {
        private final InputStream xmlStream;

        private XMLEventReader xmlEventReader;

        private STAXEventReader staxEventReader;

        private DistributionMessage currentDocument;

        public MessageFileIterator(final InputStream xmlStream)
        {

            this.xmlStream = xmlStream;
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            try
            {
                this.xmlEventReader = inputFactory.createXMLEventReader(xmlStream);
                this.staxEventReader = new STAXEventReader();
            } catch (Exception e)
            {
                throw new RuntimeException("Unable to create StAX EventReader.", e);
            }

        }

        private Document getNextTag(final String elementName)
        {
            QName qname = new QName(DistributionMessage.NAMESPACE.getURI(), elementName);

            try
            {
                while (xmlEventReader.hasNext())
                {
                    XMLEvent event = xmlEventReader.peek();

                    if (event.isStartElement() && qname.equals(event.asStartElement().getName()))
                    {
                        return DocumentHelper.createDocument(staxEventReader.readElement(xmlEventReader));
                    }
                    xmlEventReader.next();
                }
            } catch (XMLStreamException e)
            {
                throw new RuntimeException("Problem parsing element '" + qname + "'.", e);
            }

            return null;
        }

        @Override
        public boolean hasNext()
        {
            if (currentDocument != null)
            {
                return true;
            }

            this.currentDocument = getNextDistributionMessage();

            return currentDocument != null;
        }

        private DistributionMessage getNextDistributionMessage()
        {
            DistributionMessage message = null;

            Document nextTag = getNextTag("message");
            if (nextTag != null)
            {
                message = createMessage(nextTag);
            } else
            {
                finishedParsing();
            }
            return message;
        }

        @Override
        public DistributionMessage next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException("No more message elements.");
            }

            DistributionMessage temp = currentDocument;
            currentDocument = null;
            return temp;
        }

        @Override
        public void remove()
        {
            throw new RuntimeException("Not supported");
        }

        private DistributionMessage createMessage(final Document document)
        {
            return new DistributionMessage(document);
        }

        private void finishedParsing()
        {
            closeQuietly(xmlEventReader);
            IOUtils.closeQuietly(xmlStream);
        }

        private void closeQuietly(XMLEventReader file)
        {
            try
            {
                if (file != null)
                {
                    file.close();
                }
            } catch (XMLStreamException e)
            {
                //Ignore
            }
        }

    }

}
