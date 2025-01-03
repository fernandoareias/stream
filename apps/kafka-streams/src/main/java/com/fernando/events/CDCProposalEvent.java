package com.fernando.events;

import java.io.Serializable;

public class CDCProposalEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private String proposalnumber;
    private String proponentdocument;
    private String product;
    private String status;



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProposalnumber() {
        return proposalnumber;
    }

    public void setProposalnumber(String proposalnumber) {
        this.proposalnumber = proposalnumber;
    }

    public String getProponentdocument() {
        return proponentdocument;
    }

    public void setProponentdocument(String proponentdocument) {
        this.proponentdocument = proponentdocument;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProposalMessage{" +
                "key='" + key + '\'' +
                ", proposalnumber='" + proposalnumber + '\'' +
                ", proponentdocument='" + proponentdocument + '\'' +
                ", product='" + product + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
