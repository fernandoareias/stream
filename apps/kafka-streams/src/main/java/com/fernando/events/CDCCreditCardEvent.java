package com.fernando.events;

import java.io.Serializable;

public class CDCCreditCardEvent  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private String cardnumber;
    private String portadordocument;
    private String status;

    @Override
    public String toString() {
        return "CDCCreditCardEvent{" +
                "key='" + key + '\'' +
                ", cardnumber='" + cardnumber + '\'' +
                ", portadordocument='" + portadordocument + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public String getPortadordocument() {
        return portadordocument;
    }

    public void setPortadordocument(String portadordocument) {
        this.portadordocument = portadordocument;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
