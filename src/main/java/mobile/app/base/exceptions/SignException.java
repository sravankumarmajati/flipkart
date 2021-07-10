package mobile.app.base.exceptions;

import mobile.app.base.constant.Code;

public class SignException extends BaseException {
    public SignException(Code code, String msg) {
        super(code, msg);
    }
}
