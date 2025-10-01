/* @formatter:off
 *
 * © David M Rosenberg, The Software Toolsmith (education)
 *
 * This file is part of the Testing Framework for Java.
 * Repository: https://github.com/The-Software-Toolsmith/testing-framework-for-java
 *
 * Licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may obtain a copy of the license at:
 *     https://creativecommons.org/licenses/by-nc/4.0/
 *
 * You may use, share, and adapt this file for non-commercial purposes,
 * provided you give appropriate credit.
 *
 * @formatter:on
 */


package education.the_software_toolsmith.testing.framework ;

import java.lang.reflect.Field ;

/**
 * Reflective access to individual data fields
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-07-19 Initial implementation - extracted from framework's {@link Reflection}
 * @version 1.0.1 2025-09-29 undo reversing '==' and '!=' comparisons
 */
public class ReflectDataFields
    {


    /*
     * constructors
     */


    /**
     * prevent instantiation
     *
     * @since 1.0
     */
    private ReflectDataFields()
        {

        // noop

        }   // end no-arg constructor


    /*
     * getter/setter methods for data fields
     */


    /**
     * Retrieve the value of a named reference field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     * @param fieldType
     *     the type of {@code fieldName}
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static Field getField( final Object anInstance,
                                  final String fieldName,
                                  final String fieldType )
        throws TestingException
        {

        return getField( anInstance.getClass(),
                         anInstance,
                         fieldName,
                         fieldType ) ;

        }


    /**
     * Retrieve the value of a named reference field from an instance
     *
     * @param theClass
     *     the class to search for this field
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     * @param fieldType
     *     the type of {@code fieldName}
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static Field getField( final Class<?> theClass,
                                  final Object anInstance,
                                  final String fieldName,
                                  final String fieldType )
        throws TestingException
        {
//        Class<?> theClass = anInstance.getClass() ;

//        Object superInstance = superClass.cast( anInstance ) ;
//        System.out.printf( "superClass: %s%nsuperInstance: %s%n",
//                           superClass.getSimpleName(),
//                           superInstance.getClass().getSimpleName() ) ;

        // attempt to get the field from the given class
        try
            {
            final Field theField = theClass.getDeclaredField( fieldName ) ;
//            Field theField = theClass.getField( fieldName ) ;   // IN_PROCESS 8/6/2021 214a
            theField.setAccessible( true ) ;

            return theField ;
            }
        catch ( NoSuchFieldException | SecurityException ex )
            {
            // ignore NoSuchFieldException if we have a superclass

            final Class<?> superClass = theClass.getSuperclass() ;

            if ( superClass != null )
                {
                return getField( superClass,
                                 anInstance,
                                 fieldName,
                                 fieldType ) ;
                }

            reportFieldAccessFailure( ex,
                                      "retrieve",
                                      "from",
                                      theClass,
                                      anInstance,
                                      fieldName,
                                      fieldType ) ;

//            final String exceptionClassName = ex.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve %s value from class %s, field %s, instance %s: %s%s%s",
//                                                       fieldType,
//                                                       theClass.getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       exceptionClassName,
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " ),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return null ;  // can't execute - reportFieldAccessFailure() always throws
                           // TestingException
            }

        }   // end getField()


    /**
     * utility to report a field access failure via Reflection
     *
     * @param thrown
     *     the caught throwable
     * @param accessType
     *     typically "retrieve" or "retrieve or set"
     * @param fromTo
     *     typically "from" or "from/to"
     * @param theClass
     *     the class to search for this field
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     * @param fieldType
     *     the type of {@code fieldName}
     *
     * @throws TestingException
     *     wraps the caught throwable
     *
     * @since 1.0
     */
    private static void reportFieldAccessFailure( final Throwable thrown,
                                                  final String accessType,
                                                  final String fromTo,
                                                  Class<?> theClass,
                                                  final Object anInstance,
                                                  final String fieldName,
                                                  final String fieldType )
        throws TestingException
        {

        final String thrownClassName = thrown.getClass().getSimpleName() ;

        if ( theClass == null )
            {
            theClass = anInstance.getClass() ;
            }

        final String errorMessage = String.format( "Failed to %s %s value %s class %s, field %s, instance %s: %s%s%s",
                                                   accessType,
                                                   fieldType,
                                                   fromTo,
                                                   theClass.getSimpleName(),
                                                   fieldName,
                                                   anInstance.toString(),
                                                   thrownClassName,
                                                   ( thrown.getMessage() == null
                                                       ? ""
                                                       : ": " ),
                                                   ( thrown.getMessage() == null
                                                       ? ""
                                                       : thrown.getMessage() ) ) ;

        throw new TestingException( errorMessage, thrown ) ;

        }   // end reportFieldAccessFailure()


    /**
     * Retrieve the value of a named boolean field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static boolean getBooleanField( final Object anInstance,
                                           final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "boolean" ).getBoolean( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "boolean" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve boolean value from class %s, field %s, instance %s: %s%s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       exceptionClassName,
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " ),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return false ;  // can't execute - reportFieldAccessFailure() always throws
                            // TestingException
            }

        }   // end getBooleanField()


    /**
     * Retrieve the value of a named byte field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static byte getByteField( final Object anInstance,
                                     final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "byte" ).getByte( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "byte" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve byte value from class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       e.getClass()
//                                                        .getSimpleName(),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return 0 ;  // can't execute - reportFieldAccessFailure() always throws TestingException
            }

        }   // end getByteField()


    /**
     * Retrieve the value of a named char field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static char getCharField( final Object anInstance,
                                     final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "char" ).getChar( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "char" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve char value from class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       e.getClass()
//                                                        .getSimpleName(),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return 0 ;  // can't execute - reportFieldAccessFailure() always throws TestingException
            }

        }   // end getCharField()


    /**
     * Retrieve the value of a named double field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static double getDoubleField( final Object anInstance,
                                         final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "double" ).getDouble( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "double" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve double value from class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       e.getClass()
//                                                        .getSimpleName(),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return 0.0 ;  // can't execute - reportFieldAccessFailure() always throws
                          // TestingException
            }

        }   // end getDoubleField()


    /**
     * Retrieve the value of a named short field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static float getFloatField( final Object anInstance,
                                       final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "float" ).getFloat( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "float" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve float value from class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       e.getClass()
//                                                        .getSimpleName(),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return 0.0F ;  // can't execute - reportFieldAccessFailure() always throws
                           // TestingException
            }

        }   // end getFloatField()


    /**
     * Retrieve the value of a named int field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static int getIntField( final Object anInstance,
                                   final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "int" ).getInt( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "int" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve int value from class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       e.getClass()
//                                                        .getSimpleName(),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return 0 ;  // can't execute - reportFieldAccessFailure() always throws TestingException
            }

        }   // end getIntField()


    /**
     * Retrieve the value of a named long field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static long getLongField( final Object anInstance,
                                     final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "long" ).getLong( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "long" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve long value from class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       e.getClass()
//                                                        .getSimpleName(),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return 0L ;  // can't execute - reportFieldAccessFailure() always throws
                         // TestingException
            }

        }   // end getLongField()


    /**
     * Retrieve the value of a named reference field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static Object getReferenceField( final Object anInstance,
                                            final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "reference" ).get( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "reference" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve reference value from class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       e.getClass()
//                                                        .getSimpleName(),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return null ;  // can't execute - reportFieldAccessFailure() always throws
                           // TestingException
            }

        }   // end getReferenceField()


    /**
     * Retrieve the value of a named short field from an instance
     *
     * @param anInstance
     *     the instance to interrogate
     * @param fieldName
     *     the name of the field in {@code anInstance} to retrieve
     *
     * @return the value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static short getShortField( final Object anInstance,
                                       final String fieldName )
        throws TestingException
        {

        try
            {
            return getField( anInstance,
                             fieldName,
                             "short" ).getShort( anInstance ) ;
            }
        catch ( IllegalArgumentException | IllegalAccessException e )
            {
            reportFieldAccessFailure( e,
                                      "retrieve",
                                      "from",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "short" ) ;

//            final String exceptionClassName = e.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve short value from class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       e.getClass()
//                                                        .getSimpleName(),
//                                                       ( e.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             e.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, e ) ;

            return 0 ;  // can't execute - reportFieldAccessFailure() always throws TestingException
            }

        }   // end getShortField()


    /**
     * Set the value of a named boolean field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static boolean setBooleanField( final Object anInstance,
                                           final String fieldName,
                                           final boolean newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance,
                                             fieldName,
                                             "boolean" ) ;
            final boolean oldValue = theField.getBoolean( anInstance ) ;

            theField.setBoolean( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "boolean" ) ;

//            final String exceptionClassName = ex.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve or set boolean value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return false ;  // can't execute - reportFieldAccessFailure() always throws
                            // TestingException
            }

        }   // end setBooleanField()


    /**
     * Set the value of a named byte field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static byte setByteField( final Object anInstance,
                                     final String fieldName,
                                     final byte newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance, fieldName, "byte" ) ;
            final byte oldValue = theField.getByte( anInstance ) ;

            theField.setByte( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "byte" ) ;

//            final String exceptionClassName = ex.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve or set byte value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return 0 ;  // can't execute - reportFieldAccessFailure() always throws TestingException
            }

        }   // end setByteField()


    /**
     * Set the value of a named char field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static char setCharField( final Object anInstance,
                                     final String fieldName,
                                     final char newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance, fieldName, "char" ) ;
            final char oldValue = theField.getChar( anInstance ) ;

            theField.setChar( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "char" ) ;

//            final String exceptionClassName = ex.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve or set char value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return 0 ;  // can't execute - reportFieldAccessFailure() always throws TestingException
            }

        }   // end setCharField()


    /**
     * Set the value of a named double field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static double setDoubleField( final Object anInstance,
                                         final String fieldName,
                                         final double newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance, fieldName, "double" ) ;
            final double oldValue = theField.getDouble( anInstance ) ;

            theField.setDouble( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "double" ) ;

//            final String exceptionClassName = ex.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve or set double value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return 0.0 ;  // can't execute - reportFieldAccessFailure() always throws
                          // TestingException
            }

        }   // end setDoubleField()


    /**
     * Set the value of a named float field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static float setFloatField( final Object anInstance,
                                       final String fieldName,
                                       final float newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance, fieldName, "float" ) ;
            final float oldValue = theField.getFloat( anInstance ) ;

            theField.setFloat( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "float" ) ;

//            final String exceptionClassName = ex.getClass().getSimpleName() ;
//
//            final String errorMessage = String.format( "Failed to retrieve or set float value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return 0.0F ;  // can't execute - reportFieldAccessFailure() always throws
                           // TestingException
            }

        }   // end setFloatField()


    /**
     * Set the value of a named int field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static int setIntField( final Object anInstance,
                                   final String fieldName,
                                   final int newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance, fieldName, "int" ) ;
            final int oldValue = theField.getInt( anInstance ) ;

            theField.setInt( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "int" ) ;

//            final String errorMessage = String.format( "Failed to retrieve or set int value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return 0 ;  // can't execute - reportFieldAccessFailure() always throws TestingException
            }

        }   // end setIntField()


    /**
     * Set the value of a named long field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static long setLongField( final Object anInstance,
                                     final String fieldName,
                                     final long newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance, fieldName, "long" ) ;
            final long oldValue = theField.getLong( anInstance ) ;

            theField.setLong( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "long" ) ;

//            final String errorMessage = String.format( "Failed to retrieve or set long value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return 0L ;  // can't execute - reportFieldAccessFailure() always throws
                         // TestingException
            }

        }   // end setLongField()


    /**
     * Set the value of a named reference field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static Object setReferenceField( final Object anInstance,
                                            final String fieldName,
                                            final Object newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance,
                                             fieldName,
                                             "reference" ) ;
            final Object oldValue = theField.get( anInstance ) ;

            theField.set( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "reference" ) ;

//            final String errorMessage = String.format( "Failed to retrieve or set reference value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return null ;  // can't execute - reportFieldAccessFailure() always throws
                           // TestingException
            }

        }   // end setReferenceField()


    /**
     * Set the value of a named short field in a given instance
     *
     * @param anInstance
     *     the instance to update
     * @param fieldName
     *     the name of the field in {@code anInstance} to set
     * @param newValue
     *     the value to store
     *
     * @return the previous value stored in the named field
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static short setShortField( final Object anInstance,
                                       final String fieldName,
                                       final short newValue )
        throws TestingException
        {

        try
            {
            final Field theField = getField( anInstance, fieldName, "short" ) ;
            final short oldValue = theField.getShort( anInstance ) ;

            theField.setShort( anInstance, newValue ) ;

            return oldValue ;
            }
        catch ( IllegalAccessException | SecurityException ex )
            {
            reportFieldAccessFailure( ex,
                                      "retrieve or set",
                                      "from/to",
                                      null,
                                      anInstance,
                                      fieldName,
                                      "short" ) ;

//            final String errorMessage = String.format( "Failed to retrieve or set short value from/to class %s, field %s, instance %s: %s%s",
//                                                       anInstance.getClass()
//                                                                 .getSimpleName(),
//                                                       fieldName,
//                                                       anInstance.toString(),
//                                                       ex.getClass()
//                                                         .getSimpleName(),
//                                                       ( ex.getMessage() == null
//                                                           ? ""
//                                                           : ": " +
//                                                             ex.getMessage() ) ) ;
//
//            throw new TestingException( errorMessage, ex ) ;

            return 0 ;  // can't execute - reportFieldAccessFailure() always throws TestingException
            }

        }   // end setShortField()

    }   // end class ReflectDataFields