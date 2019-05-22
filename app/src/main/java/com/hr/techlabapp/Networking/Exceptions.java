package com.hr.techlabapp.Networking;

public class Exceptions {
    static class AlreadyExistsException extends Exception{
        public AlreadyExistsException(){
            super();
        }
        public AlreadyExistsException(String message) {
            super(message);
        }
    }
    static class NoSuchProductCategoryException extends Exception{
        public NoSuchProductCategoryException(){
            super();
        }
        public NoSuchProductCategoryException(String message) {
            super(message);
        }
    }

     /**
     * Thrown when the client receives an unexpected exception. Examples include responses
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

    static class AccessDeniedException extends Exception{
        public AccessDeniedException(){
            super();
        }

        public AccessDeniedException(String message) {
            super(message);
        }
    }

    static class MissingArgumentException extends Exception{
        public MissingArgumentException(){
            super();
        }

        public MissingArgumentException(String message) {
            super(message);
        }
    }
}
