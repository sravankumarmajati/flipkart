package mobile.app.service.impl;

import mobile.app.base.constant.Code;
import mobile.app.base.exceptions.DeployException;
import mobile.app.repository.ContractRepository;
import mobile.app.service.ContractService;
import cn.hyperchain.sdk.crypto.ECPriv;
import cn.hyperchain.sdk.exception.TxException;
import cn.hyperchain.sdk.rpc.HyperchainAPI;
import cn.hyperchain.sdk.rpc.Transaction.Transaction;
import cn.hyperchain.sdk.rpc.base.VMType;
import cn.hyperchain.sdk.rpc.utils.Utils;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: chenquan
 * Date: 2018/11/12
 * Package: mobile.app.service.impl
 * Description:
 */
@Service
public class ContractServiceImpl implements ContractService {
    @Autowired
    ContractRepository contractRepository;

    @Override public String delpoy(String contractName) {
        String payload = null;
        try {
            payload = Utils.encodeDeployJar("contract/" + contractRepository.queryContractJarName());
//            System.out.println(payload);
            ECPriv priv = HyperchainAPI.decryptAccount(contractRepository.queryAccountJson(""),contractRepository.queryAccountJsonPwd(""));
            Transaction deployTx = new Transaction(priv.address(),payload,false, VMType.HVM);
            deployTx.sign(priv);
//            String address = contractRepository.deploy(deployTx, contractRepository.queryContractName(), true);
            return contractRepository.deploy(deployTx, contractName, false);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DeployException(Code.DEPLOY_FAILED, e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new DeployException(Code.DEPLOY_FAILED, e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new DeployException(Code.DEPLOY_FAILED, e.getMessage());
        } catch (TxException e) {
            e.printStackTrace();
            throw new DeployException(Code.DEPLOY_FAILED, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeployException(Code.DEPLOY_FAILED, e.getMessage());
        }
    }
}
