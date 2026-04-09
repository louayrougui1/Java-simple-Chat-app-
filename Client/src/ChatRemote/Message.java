package ChatRemote;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    String idSender;
    String messageBody;
    String destinationId;
    Date sendingDate;

    Message() {
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public Message(String idSender, String messageBody, Date sendingDate, String destinationId) {
        this.idSender = idSender;
        this.messageBody = messageBody;
        this.sendingDate = sendingDate;
        this.destinationId=destinationId;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Date getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }

    @Override
    public String toString() {
        return "SenderID: " + idSender + "msg: " + messageBody + " date: " + sendingDate;
    }
}
