package mobile.app.base.listener;

import cn.hyperchain.sdk.crypto.ECPriv;
import cn.hyperchain.sdk.exception.TxException;
import cn.hyperchain.sdk.rpc.HyperchainAPI;
import cn.hyperchain.sdk.rpc.Transaction.Transaction;
import cn.hyperchain.sdk.rpc.returns.ReceiptReturn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractIniter {
    private static final Logger logger = LoggerFactory.getLogger(ContractIniter.class);
    public static HyperchainAPI api = null;
    public static ECPriv ecPriv = null;
    public static HashMap<String, String> solidityAddress = new HashMap<>();
    public static HashMap<String, String> abi = new HashMap<>();

    private String solidityPropPath;

    private String solidityOutputPrefix;

    public void init() throws Exception {
        File solidityPropFile = new File(solidityPropPath);
        Properties prop = new Properties();
        prop.load(new FileReader(solidityPropFile));
        String[] contractNames = prop.getProperty("name").split(",");
        api = new HyperchainAPI();
        ecPriv = api.newAccount();
        if (contractNames.length != 0) {
            logger.info("Target contract list is not empty. Start deployment...");

            boolean propModified = false;

            //遍历所有传入的和合约名称，并判断是否需要编译或部署
            for (String contractName : contractNames) {
                String address;
                String abiStr;

                InputStream binInputStream = new FileInputStream(
                        new File(solidityOutputPrefix + contractName + ".bin"));
                InputStream abiInputStream = new FileInputStream(
                        new File(solidityOutputPrefix + contractName + ".abi"));

                //abi文件读取abi(无论是否编译，是否部署，都要加载 abi)
                BufferedReader br = new BufferedReader(new InputStreamReader(abiInputStream));
                abiStr = br.readLine();

                if (prop.getProperty(contractName) != null && !prop.getProperty(contractName).equals("")) {
                    //合约地址已经都存在，不需要编译或部署；
                    address = prop.getProperty(contractName);
                } else {
                    //合约地址不存在，判断abi和bin文件是否存在，决定是否需要编译
                    propModified = true;
                    //bin文件读取及部署合约
                    br = new BufferedReader(new InputStreamReader(binInputStream));
                    String binStr = br.readLine();
                    address = deployContractWithBin(contractName, binStr, ecPriv);

                    //文件流关闭
                    br.close();

                    //将合约地址写入properties文件
                    prop.setProperty(contractName, address);
                }
                //无论是否经过编译部署，都需要将合约地址写入Map对象
                solidityAddress.put(contractName, address);
                //将abi对应写入Map
                abi.put(contractName, abiStr);
            }
            if (propModified) {
                //根据prop对象是否修改过的标识位，来决定是否需要将prop重新写入文件
                OutputStream fos = new FileOutputStream(solidityPropFile);
                prop.store(fos, "");
            }

            //备注：因为Properties类在读写文件时，内部已经实现了文件加锁的操作，因此不需要考虑文件内容的同步问题。

        } else {
            logger.info("Target contract list is empty.");
        }
    }

    private String deployContractWithBin(String contractName, String bin, ECPriv ecPriv) throws TxException {

        logger.info("deploy contract with bin----" + contractName);
        String contractAddress = "";
        try {
            Transaction tx = new Transaction(ecPriv.address(), bin, false);
            tx.sign(ecPriv);
            ReceiptReturn singleValueReturn = api.deployContract(tx);
            String txHash = singleValueReturn.getResult();

            ReceiptReturn receiptReturn;
            int code;
            do {
                Thread.sleep(1000);
                receiptReturn = api.getTransactionReceipt(txHash);
                code = receiptReturn.getCode();
            } while (code != 200);

            contractAddress = receiptReturn.getContractAddress();
            logger.info("contract address----:" + contractAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contractAddress;
    }

}
