package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

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
    @Value("${torocoin-pair-busd-address}")
    private String pairBusdAddress;
    @Value("${torocoin-busd-address}")
    private String busdAddress;
    @Value("${torocoin-query-wallet-private-key}")
    private String queryWalletPrivateKey;

    @GetMapping("sync-trans")
    public void syncTransByTxHash(
            @RequestParam("txHash") String txHash
    ) throws Exception {
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

        BigInteger amountBusd = getBusdFromToroCoin(Numeric.toBigInt(amountString));

        BigDecimal bigDecimal = BigDecimal.valueOf(amountBusd.doubleValue());
        double amount = bigDecimal.divide(BigDecimal.valueOf(1E18)).doubleValue();

        if (!toroCoinSwapAddress.toLowerCase().equals(to.toLowerCase())) return;

        //TODO Save history: from, to, amount, originData, txHash
        log.info("ToroCoin Transfer event from: {}, to: {}, amount: {}, txHash: {}", from, to, amount, txHash);
        // double toroPoint = amount * swapRate;
        //TODO Process Swap, Send point for "from" account and noti to Toro App

        web3j.shutdown();
    }


//    @GetMapping("get-amount-out")
//    public void getAmounOutInfo() throws Exception {
//        getSwapRateToroCoinToBusd("111");
//    }

    BigInteger getBusdFromToroCoin(BigInteger toroCointAmount) throws Exception {
        Web3j web3j = Web3j.build(new HttpService(blockchainNetworkUrl));
        Credentials credentials = Credentials.create(queryWalletPrivateKey);
        RouterV1 router = new RouterV1(pairBusdAddress, web3j, credentials, new DefaultGasProvider());
        RemoteFunctionCall<List> functionCall = router.getAmountsOut(toroCointAmount, Arrays.asList(toroCoinSmartContractAddress, busdAddress));
        List<BigInteger> result = functionCall.send();
        web3j.shutdown();
        return result.get(1);

    }
}