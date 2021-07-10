package mobile.app.contract.student.invoke;

import mobile.app.contract.student.logic.IStudent;
import mobile.app.contract.student.logic.entity.Student;
import cn.hyperchain.contract.BaseInvoke;

import java.util.List;

public class InvokeRegisterStudent implements BaseInvoke<Boolean, IStudent> {

    private List<Student> students;

    public InvokeRegisterStudent(){}

    public InvokeRegisterStudent(List<Student> students) {
        this.students = students;
    }

    @Override public Boolean invoke(IStudent iStudent) {
        return iStudent.registerStudent(this.students);
    }
}
