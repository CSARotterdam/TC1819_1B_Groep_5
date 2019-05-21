package com.hr.techlabapp.Networking;

public class Exceptions {
    /**
     * Thrown when the client attempts to add an object, but the server says that such an object already exists.
     */
    static class AlreadyExistsException extends Exception{
        public AlreadyExistsException(){
            super();
        }
        public AlreadyExistsException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client attempts to access a nonexistent product category.
     */
    static class NoSuchProductCategoryException extends Exception{
        public NoSuchProductCategoryException(){
            super();
        }
        public NoSuchProductCategoryException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client attempts to access a nonexistent product.
     */
    static class NoSuchProductException extends Exception{
        public NoSuchProductException(){
            super();
        }
        public NoSuchProductException(String message) {
            super(message);
        }
    }

     /**
     * Thrown when the client receives an unexpected message from the server. Examples include responses
     * that the client doesn't know how to process.
     */
     static class UnexpectedServerResponseException extends Exception{
        public UnexpectedServerResponseException(){
            super();
        }
        public UnexpectedServerResponseException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client attempts to use a certain API call when it doesn't have permission to do so.
     * Should never be thrown, because it implies that the client gave users access to menus or buttons that they don't have permission to access.
     */
    static class AccessDeniedException extends Exception{
        public AccessDeniedException(){
            super();
        }
        public AccessDeniedException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when the client sends an incomplete request to the server.
     * Only gets thrown due to client bugs.
     * NOTE: This will get thrown if you try to add a value to a request JObject by using `.put("whatever", null)`, because this function apparently just doesn't do anything.
     */
    static class MissingArgumentException extends Exception{
        public MissingArgumentException(){
            super();
        }
        public MissingArgumentException(String message) {
            super(message);
        }
    }
}
