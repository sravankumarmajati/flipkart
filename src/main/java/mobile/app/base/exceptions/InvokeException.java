package mobile.app.base.exceptions;

import mobile.app.base.constant.Code;

public class InvokeException extends BaseException {
    public InvokeException(Code code, String msg) {
        super(code, msg);
    }
}
