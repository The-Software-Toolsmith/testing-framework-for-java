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

import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Method ;
import java.lang.reflect.Type ;
import java.util.Arrays ;
import java.util.LinkedList ;
import java.util.List ;


/**
 * Reflective access to methods
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-07-19 Initial implementation - extracted from framework's {@link Reflection}
 * @version 1.0.1 2025-09-30 undo reversing '==' and '!=' comparisons
 */
public class ReflectMethods
    {


    /*
     * constructors
     */


    /**
     * prevent instantiation
     *
     * @since 1.0
     */
    private ReflectMethods()
        {

        // noop

        }   // end no-arg constructor


    /*
     * methods for method invocation
     */


    /**
     * Utility/convenience method to invoke an instance or static method with no arguments
     *
     * @param theClass
     *     the class for the static method invocation
     * @param anInstance
     *     the instance context for the method invocation - will be null for static methods
     * @param methodName
     *     the name of the method to execute
     *
     * @return any value or object returned by the method - if the named method returns a primitive
     *     type, the value will be wrapped - if the method is void, then null
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     * @throws Throwable
     *     anything thrown by the named method
     */
    public static Object invoke( final Class<?> theClass,
                                 final Object anInstance,
                                 final String methodName )
        throws TestingException, Throwable
        {   // DMR NEW

        return invoke( theClass,
                       anInstance,
                       methodName,
                       new Class<?>[] {},
                       (Object[]) null ) ;

        }   // end invoke() with no arguments


    /**
     * Utility method to invoke an instance or static method
     *
     * @param theClass
     *     the class for the static method invocation
     * @param anInstance
     *     the instance context for the method invocation - will be null for static methods
     * @param methodName
     *     the name of the method to execute
     * @param parameterTypes
     *     the types of the method's parameters
     * @param arguments
     *     any parameters to pass to the method
     *
     * @return any value or object returned by the method - if the named method returns a primitive
     *     type, the value will be wrapped - if the method is void, then null
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     * @throws Throwable
     *     anything thrown by the named method
     */
    public static Object invoke( final Class<?> theClass,         // FUTURE validate return value
                                 final Object anInstance,
                                 final String methodName,
                                 final Class<?>[] parameterTypes,
                                 final Object... arguments )
        throws TestingException, Throwable
        {

        Method theMethod = null ;

        try
            {
            final List<Method> methodsOfInterest = new LinkedList<>( Arrays.asList( theClass.getDeclaredMethods() ) ) ;
            methodsOfInterest.addAll( Arrays.asList( theClass.getMethods() ) ) ;

            for ( final Method aMethod : methodsOfInterest )
//            for ( Method aMethod : theClass.getDeclaredMethods() )    // IN_PROCESS
                {

                if ( aMethod.getName().equals( methodName ) )
                    {
                    // check the parameters
                    final Type[] definedParameterTypes = aMethod.getParameterTypes() ;
                    final Type[] definedGenericParameterTypes = aMethod.getGenericParameterTypes() ;

                    // no parameters defined
                    if ( definedParameterTypes.length == 0 )
                        {

                        if ( ( parameterTypes == null ) ||
                             ( parameterTypes.length == 0 ) )
                            {
                            // no parameters supplied - found it
                            theMethod = aMethod ;

                            break ;
                            }

                        // not a match - keep looking
                        continue ;
                        }

                    // at least one parameter defined
                    if ( definedParameterTypes.length == parameterTypes.length )
                        {
                        // correct number of parameters
                        // see if they're the right types
                        boolean mismatch = false ;

                        for ( int i = 0 ;
                              i < definedParameterTypes.length ;
                              i++ )
                            {

                            if ( !definedParameterTypes[ i ].equals( parameterTypes[ i ] ) &&
                                 !definedGenericParameterTypes[ i ].equals( parameterTypes[ i ] ) )
                                {
                                // mismatch
                                mismatch = true ;

                                break ;
                                }

                            }

                        if ( mismatch )
                            {
                            continue ;
                            }

                        // found a match
                        theMethod = aMethod ;

                        break ;
                        }

                    }

                }

            if ( theMethod == null )
                {
                // didn't find a matching method
                throw new NoSuchMethodException() ;
                }

            theMethod.setAccessible( true ) ;

//            System.out.printf( "theMethod: %s: %s%n",   // DBG
//                               theMethod.getName(),
//                               theMethod.getDeclaringClass().getSimpleName() ) ;

            // for static methods, anInstance is typically null
            return theMethod.invoke( anInstance, arguments ) ;
            }

        catch ( final InvocationTargetException e )
            {
            // simply propagate any exception from the called method to our caller
            throw e.getCause() ;
            }

        catch ( NoSuchMethodException
                | SecurityException
                | IllegalAccessException
                | IllegalArgumentException e )
            {
            // build a description of the expected parameter list
            final StringBuilder displayParameterTypes = new StringBuilder() ;

            if ( parameterTypes != null )
                {

                for ( int i = 0 ; i < parameterTypes.length ; i++ )
                    {

                    if ( i != 0 )
                        {
                        displayParameterTypes.append( ", " ) ;
                        }

                    displayParameterTypes.append( parameterTypes[ i ].getSimpleName() ) ;
                    }

                }

            final String exceptionClassName = e.getClass().getSimpleName() ;

            final String errorMessage = String.format( "Failed to invoke method %s(%s) in class %s with argument(s): %s:%n\t%s%s%s",
                                                       methodName,
                                                       displayParameterTypes.toString(),
                                                       theClass.getSimpleName(),
                                                       Arrays.toString( arguments ),
                                                       exceptionClassName,
                                                       ( e.getMessage() == null
                                                           ? ""
                                                           : ": " ),
                                                       ( e.getMessage() == null
                                                           ? ""
                                                           : e.getMessage() ) ) ;

            throw new TestingException( errorMessage, e ) ;
            }

        }   // end invoke() with arguments

    }   // end class ReflectMethods