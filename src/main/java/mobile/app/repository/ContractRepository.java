package mobile.app.repository;

import mobile.app.base.constant.Code;
import mobile.app.base.exceptions.DeployException;
import mobile.app.base.exceptions.InvokeException;
import mobile.app.base.exceptions.SignException;
import mobile.app.util.ContractUtil;
import cn.hyperchain.sdk.rpc.HyperchainAPI;
import cn.hyperchain.sdk.rpc.Transaction.Transaction;
import cn.hyperchain.sdk.rpc.returns.ReceiptReturn;
import cn.hyperchain.sdk.rpc.utils.ByteUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContractRepository {

    private Logger logger = Logger.getLogger(ContractRepository.class);

    private HyperchainAPI hyperchain = null;

    private ContractUtil contractUtil;

    @Autowired public ContractRepository(ContractUtil contractUtil) {
        try {
            hyperchain = new HyperchainAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.contractUtil = contractUtil;
        contractUtil.init();
    }

    /**
     *
     * @param tx
     * @param contractName
     * @param persist
     * @return
     * @throws SignException
     * @throws InterruptedException
     * @throws DeployException
     */
    public String deploy(Transaction tx,String contractName , boolean persist) throws SignException,
            InterruptedException, DeployException {
        if (tx.getSignature().equals("")) {
            throw new SignException(Code.TX_UNSIGNED, Code.TX_UNSIGNED.getMsg());
        }
        ReceiptReturn deployReceipt = hyperchain.deployContract(tx);
        if (deployReceipt.isSuccess()) {
            logger.info("deploy Success");
            String addr = deployReceipt.getContractAddress();
            contractUtil.setContractAddress(contractName, addr, persist);
            return addr;
        } else {
            throw new DeployException(Code.DEPLOY_FAILED, Code.DEPLOY_FAILED.getMsg());
        }
    }

    /**
     *
     * @param tx
     * @return
     * @throws SignException
     * @throws InterruptedException
     * @throws InvokeException
     */
    public String invoke(Transaction tx) throws SignException, InterruptedException, InvokeException {
        if (tx.getSignature().equals("")) {
            throw new SignException(Code.TX_UNSIGNED, Code.TX_UNSIGNED.getMsg());
        }

        ReceiptReturn receipt = hyperchain.invokeContract(tx);
        if (receipt.isSuccess()) {
            logger.info("invoke Success");
            // TODO change here no need to decode
            return new String(ByteUtil.hexStringToBytes(receipt.getRet()));
        } else {
            throw new InvokeException(Code.CONTRACT_INVOKE_ERROR, Code.CONTRACT_INVOKE_ERROR.getMsg());
        }
    }
     /**
     *
     * @param contractName
     * @param persist_first
     * @return
     */
    public String queryAddress(String contractName, boolean persist_first){
       return contractUtil.getContractAddress(contractName, persist_first);
    }

    /**
     *
     * @param accountName
     * @return
     */
    public String queryAccountJson(String accountName){
        return contractUtil.getDefaultAccountJson();
    }

    public String queryAccountJsonPwd(String accountName){
        return contractUtil.getDefaultAccountJsonPwd();
    }

    public String queryContractName(){
        return contractUtil.getContractName();
    }

    public String queryContractJarName() {
        return contractUtil.getContractJarName();
    }

}
