package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/api/v1/torocoin")
public class ToroCoinSyncTransController {

    @Value("${blockchain-network-url}")
    private String blockchainNetworkUrl;
    @Value("${torocoin-smart-contract-address}")
    private String toroCoinSmartContractAddress;
    @Value("${torocoin-swap-wallet-address}")
    private String toroCoinSwapAddress;

    @GetMapping("sync-trans")
    public void syncTransByTxHash(
            @RequestParam("txHash") String txHash
    ) throws IOException {
        Web3j web3j = Web3j.build(new HttpService(blockchainNetworkUrl));
        int walletLen = 40;
        int amountInputLength = 64;

        Request<?, EthTransaction> transactionRequest = web3j.ethGetTransactionByHash(txHash);
        EthTransaction transaction = transactionRequest.send();

        String input = transaction.getResult().getInput();
        String from = transaction.getResult().getFrom();

        // input: 0x[4byte: MethodID] [32byte: From Add] [32 byte: Amount]
        String to = "0x" + input.substring(10, 64 + 10).substring(64 - walletLen);
        String amountString = input.substring(input.length() - amountInputLength);
        BigDecimal bigDecimal = BigDecimal.valueOf(Long.parseLong(Numeric.toBigInt(amountString).toString()));
        double amount = bigDecimal.divide(BigDecimal.valueOf(1E18)).doubleValue();

        if (!toroCoinSwapAddress.toLowerCase().equals(to.toLowerCase())) return;

        //TODO Save history: from, to, amount, originData, txHash
        log.info("ToroCoin Transfer event from: {}, to: {}, amount: {}, txHash: {}", from, to, amount, txHash);

        //TODO Process Swap, Send point for "from" account and noti to Toro App

        web3j.shutdown();
    }
}
