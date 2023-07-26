package com.zl.blockchain.dto;

public class EsGoBlockchainData {
    private EstoIotdataResponse estoIotdataResponse;

    private EstoIotdatarequest estoIotdatarequest;

    public EstoIotdatarequest getEstoIotdatarequest() {
        return estoIotdatarequest;
    }

    public EstoIotdataResponse getEstoIotdataResponse() {
        return estoIotdataResponse;
    }

    public void setEstoIotdatarequest(EstoIotdatarequest estoIotdatarequest) {
        this.estoIotdatarequest = estoIotdatarequest;
    }

    public void setEstoIotdataResponse(EstoIotdataResponse estoIotdataResponse) {
        this.estoIotdataResponse = estoIotdataResponse;
    }

}
