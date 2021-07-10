package mobile.app.dao.impl;

import mobile.app.contract.student.logic.entity.Student;
import mobile.app.contract.student.logic.entity.StudentResult;
import mobile.app.dao.StudentContractDao;
import mobile.app.repository.ContractRepository;
import cn.hyperchain.contract.BaseInvoke;
import cn.hyperchain.sdk.crypto.ECPriv;
import cn.hyperchain.sdk.rpc.HyperchainAPI;
import cn.hyperchain.sdk.rpc.Transaction.Transaction;
import cn.hyperchain.sdk.rpc.base.VMType;

import java.util.List;

import mobile.app.contract.student.invoke.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class StudentContractDaoImpl implements StudentContractDao {

    Logger logger = Logger.getLogger(StudentContractDaoImpl.class);
    final ContractRepository repository;

    ECPriv account;
    @Autowired public StudentContractDaoImpl(ContractRepository repository) {
        this.repository = repository;
        String accountJson = repository.queryAccountJson("default");
        String accountJsonPwd = repository.queryAccountJsonPwd("default");
        try {
            this.account = HyperchainAPI.decryptAccount(accountJson,accountJsonPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override public boolean registerStudent(List<Student> students) {
        // decrypt account
        String contractAddress = repository.queryAddress(repository.queryContractName(), true);
        try {
            InvokeRegisterStudent invokeRegisterStudent = new InvokeRegisterStudent(students);
            return newTx(contractAddress, invokeRegisterStudent, Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override public boolean changeStudent(Student student) {
        String contractAddress = repository.queryAddress(repository.queryContractName(), true);
        try {
            InvokeChangeStudent invokeChangeStudent = new InvokeChangeStudent(student);
            return newTx(contractAddress, invokeChangeStudent, Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override public Student getStudent(String id) {
        String contractAddress = repository.queryAddress(repository.queryContractName(), true);
        try {
            InvokeGetStudent invokeGetStudent = new InvokeGetStudent(id);
            return newTx(contractAddress, invokeGetStudent, Student.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public List<Student> getStudents(List<String> ids) {
        String contractAddress = repository.queryAddress(repository.queryContractName(), true);
        try {
            InvokeGetStudents invokeGetStudents = new InvokeGetStudents(ids);
            // TODO check list generic type
            return newTx(contractAddress, invokeGetStudents, List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public StudentResult isContains(Student student) {
        String contractAddress = repository.queryAddress(repository.queryContractName(), true);
        try {
            InvokeIsContains invokeIsContains = new InvokeIsContains(student);
            return newTx(contractAddress, invokeIsContains, StudentResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private <T> T newTx(String contractAddress, BaseInvoke invokeBench01, Class<T> klass) throws Exception {
        Transaction invokeTx = new Transaction(
                account.address(),
                contractAddress,
                invokeBench01,
                false,
                VMType.HVM);
        invokeTx.sign(account);

        String ret = repository.invoke(invokeTx);
        return new Gson().fromJson(ret, klass);
    }
}
