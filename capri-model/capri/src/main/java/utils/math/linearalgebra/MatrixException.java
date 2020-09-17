package utils.math.linearalgebra;

/*
* Created on May 11, 2004
*/

/**
* Matrix exceptions
* <p>
* @author anonymous
*/
public class MatrixException extends Exception
{
 
 /**
  * 
  */
 private static final long serialVersionUID = -6594098884363604553L;
 
 private boolean fIsFatal = false;

 /**
  * Constructor
  */
 public MatrixException()
 {
     super();
 }

 /**
  * Constructor
  * @param message
  */
 public MatrixException(String message)
 {
     super(message);
 }

 /**
  * Constructor
  * @param message
  * @param isFatal 
  */
 public MatrixException(String message, boolean isFatal)
 {
     super(message);
     fIsFatal = isFatal;
 }

 /**
  * Constructor
  * @param message
  * @param cause
  */
 public MatrixException(String message, Throwable cause)
 {
     super(message, cause);
 }

 /**
  * Constructor
  * @param cause
  */
 public MatrixException(Throwable cause)
 {
     super(cause);
 }

 /**
  * @return true if exception is fatal
  */
 public boolean isFatal()
 {
     return fIsFatal;
 }
}

