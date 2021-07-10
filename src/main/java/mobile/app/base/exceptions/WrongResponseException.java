package mobile.app.base.exceptions;

import mobile.app.base.constant.Code;

public class WrongResponseException extends BaseException {
    public WrongResponseException(Code code, String msg) {
        super(code, msg);
    }
}
