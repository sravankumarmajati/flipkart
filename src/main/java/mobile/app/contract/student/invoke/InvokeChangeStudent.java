package mobile.app.contract.student.invoke;

import mobile.app.contract.student.logic.IStudent;
import mobile.app.contract.student.logic.entity.Student;
import cn.hyperchain.contract.BaseInvoke;

public class InvokeChangeStudent implements BaseInvoke<Boolean, IStudent> {

    private Student student;

    public InvokeChangeStudent(Student student) {
        this.student = student;
    }

    public InvokeChangeStudent() {}

    @Override public Boolean invoke(IStudent iStudent) {
        return iStudent.changeStudent(student);
    }
}
