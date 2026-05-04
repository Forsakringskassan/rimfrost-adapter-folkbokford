package se.fk.rimfrost.adapter.folkbokford;

public class FolkbokfordException extends Exception
{
   private final ErrorType errorType;

   public FolkbokfordException(ErrorType errorType, String message)
   {
      super(message);

      this.errorType = errorType;
   }

   public FolkbokfordException(ErrorType errorType, String message, Throwable cause)
   {
      super(message, cause);

      this.errorType = errorType;
   }

   public ErrorType getErrorType()
   {
      return errorType;
   }

   public enum ErrorType
   {
      NOT_FOUND, BAD_REQUEST, SERVICE_UNAVAILABLE, UNEXPECTED_ERROR,
   }
}
