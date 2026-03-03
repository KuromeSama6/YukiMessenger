package moe.protasis.yukimessenger.rpc.exception;

public class RPCHandlerRegistrationException extends Exception {
    public  RPCHandlerRegistrationException(String message) {
        super(message);
    }

    public RPCHandlerRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
