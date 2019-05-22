package com.hr.techlabapp.Networking;

public class Exceptions {
    /**
     * Top-level exception class for networking errors.
     */
    public static class NetworkingException extends RuntimeException {
        public NetworkingException() { super(); }
        public NetworkingException(String message) { super(message); }
        public NetworkingException(Throwable e) { super(e); }
    }

    /**
     * Thrown when the client token expired and couldn't be renewed.
     */
    static class TokenRenewalException extends NetworkingException {
        public TokenRenewalException(){
            super();
        }
        public TokenRenewalException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client attempts to use a certain API call when it doesn't have permission to do so.
     * Should never be thrown, because it implies that the client gave users access to menus or buttons that they don't have permission to access.
     */
    static class AccessDenied extends NetworkingException {
        public AccessDenied(){
            super();
        }
        public AccessDenied(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client sends an incorrect username or password.
     * If thrown when attempting to renew the client's token, cancel whatever you're doing and return to the login screen.
     */
    static class InvalidLogin extends NetworkingException {
        public InvalidLogin(){
            super();
        }
        public InvalidLogin(String message) {
            super(message);
        }

    }

    /**
     * Thrown when the client attempts to access a nonexistent product.
     */
    static class NoSuchProduct extends NetworkingException {
        public NoSuchProduct(){
            super();
        }
        public NoSuchProduct(String message) {
            super(message);
        }



    }
    /**
     * Thrown when the client attempts to access a nonexistent product category.
     */
    static class NoSuchProductCategory extends NetworkingException {
        public NoSuchProductCategory(){
            super();
        }
        public NoSuchProductCategory(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client attempts to access a nonexistent user.
     */
    static class NoSuchUser extends NetworkingException {
        public NoSuchUser(){
            super();
        }
        public NoSuchUser(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client attempts to add an object, but the server says that such an object already exists.
     */
    static class AlreadyExists extends NetworkingException {

        public AlreadyExists(){
            super();
        }
        public AlreadyExists(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client sends an incomplete request to the server.
     * Only gets thrown due to client bugs.
     * NOTE: This will get thrown if you try to add a value to a request JObject by using `.put("whatever", null)`, because this function apparently just doesn't do anything.
     */
    static class MissingArgument extends NetworkingException {
        public MissingArgument(){
            super();
        }
        public MissingArgument(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the server failed to process a request due to an internal server error.
     */
    static class ServerError extends NetworkingException {
        public ServerError(){
            super();
        }
        public ServerError(String message) {
            super(message);
        }
    }

    /**
     * Thrown when a request was sent using unacceptable values.
     * Examples include out of range integers, strings with an incorrect length, etc.
     */
    static class InvalidArguments extends NetworkingException {
        public InvalidArguments(){
            super();
        }
        public InvalidArguments(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client receives an unexpected message from the server. Examples include responses
     * that the client doesn't know how to process.
     */
    static class UnexpectedServerResponse extends NetworkingException {
         public UnexpectedServerResponse(){
            super();
        }
         public UnexpectedServerResponse(String message) {
            super(message);
        }

    }
}
