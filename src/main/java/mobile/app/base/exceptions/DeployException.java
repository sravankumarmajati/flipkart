package mobile.app.base.exceptions;

import mobile.app.base.constant.Code;

public class DeployException extends BaseException {
    public DeployException(Code code, String msg) {
        super(code, msg);
    }
}
