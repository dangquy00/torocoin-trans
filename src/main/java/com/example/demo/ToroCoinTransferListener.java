package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

@Service
@Slf4j
//@Getter
//@Setter
public class ToroCoinTransferListener {

    @Value("${blockchain-network-url}")
    private String blockchainNetworkUrl;
    @Value("${torocoin-smart-contract-address}")
    private String toroCoinSmartContractAddress;
    @Value("${torocoin-swap-wallet-address}")
    private String toroCoinSwapAddress;

    @PostConstruct
    public void run() {
        Web3j web3j = Web3j.build(new HttpService(blockchainNetworkUrl));
        Event TRANSFER_EVENT = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Uint256>(false) {
                        }));

        EthFilter filter = new EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, toroCoinSmartContractAddress);
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        web3j.ethLogFlowable(filter).subscribe(data -> {
            try {
                int walletLen = 40;
                if (data.getTopics().size() < 3) {
                    log.info("Topic not enough data");
                    return;
                }
                /*** TOPIC
                 * 0: 0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef
                 * 1: 0x000000000000000000000000e2fa58a623ce425cd19bcf25dc48f1a1f3ea2e3d
                 * 2: 0x000000000000000000000000978a775e694e3f81dc427a0b7cdb1f1d7d334d58
                 **/
                String from = "0x" + data.getTopics().get(1).substring(data.getTopics().get(1).length() - walletLen);
                String to = "0x" + data.getTopics().get(2).substring(data.getTopics().get(2).length() - walletLen);

                String originData = data.getData();
                String txHash = data.getTransactionHash();
                if (!toroCoinSwapAddress.toLowerCase().equals(to.toLowerCase())) return;

                BigDecimal bigDecimal = BigDecimal.valueOf(Long.parseLong(Numeric.toBigInt(data.getData()).toString()));
                double amount = bigDecimal.divide(BigDecimal.valueOf(1E18)).doubleValue();

                //TODO Save history: from, to, amount, originData, txHash
                log.info("ToroCoin Transfer event from: {}, to: {}, amount: {}, txHash: {}", from, to, amount, txHash);

                //TODO Process Swap, Send point for "from" account and noti to Toro App

            } catch (Throwable e) {
                log.error("Subscribe Transfer erorr", e);
            }

        });
        log.info("ToroCoinTransferListener started");
    }
}
