package mobile.app.base.listener;

import mobile.app.repository.ContractRepository;
import cn.hyperchain.sdk.crypto.ECPriv;
import cn.hyperchain.sdk.rpc.HyperchainAPI;
import cn.hyperchain.sdk.rpc.Transaction.Transaction;
import cn.hyperchain.sdk.rpc.base.VMType;
import cn.hyperchain.sdk.rpc.utils.Utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContractInitializer {

    Logger logger = Logger.getLogger(ContractInitializer.class);


    @Autowired
    ContractRepository contractRepository;

    public void init(){
        try {
            logger.info(contractRepository.queryContractName());
            logger.info(contractRepository.queryContractName());
            logger.info(contractRepository.queryAccountJson(""));
            logger.info(contractRepository.queryAccountJsonPwd(""));

            String payload = Utils.encodeDeployJar("contract/" + contractRepository.queryContractJarName());
            ECPriv priv = HyperchainAPI.decryptAccount(contractRepository.queryAccountJson(""),contractRepository.queryAccountJsonPwd(""));
            Transaction deployTx = new Transaction(priv.address(),payload,false, VMType.HVM);
            deployTx.sign(priv);
            String address = contractRepository.deploy(deployTx, contractRepository.queryContractName(), true);
            logger.info("contract address is " +  address);
           } catch (Exception e) {// TODO specific exception type
            e.printStackTrace();
        }

    }
}
