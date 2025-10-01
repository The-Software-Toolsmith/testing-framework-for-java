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

import static education.the_software_toolsmith.testing.framework.TestData.datasetToString ;

import java.lang.reflect.Constructor ;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Type ;


/**
 * Reflective access to reference types (classes, etc.)
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-07-19 Initial implementation - extracted from framework's {@link Reflection}
 * @version 1.0.1 2025-09-30 undo reversing '==' and '!=' comparisons
 */
public class ReflectReferenceTypes
    {


    /*
     * constructors
     */


    /**
     * prevent instantiation
     */
    private ReflectReferenceTypes()
        {

        // noop

        }   // end no-arg constructor


    /*
     * object instantiation
     */


    /**
     * Utility/convenience method to instantiate an object of a specified class using its no-arg
     * constructor
     *
     * @param theClass
     *     the class of object to instantiate
     *
     * @return the new instance
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     * @throws Throwable
     *     anything thrown by the named method
     */
    public static Object instantiate( final Class<?> theClass )
        throws TestingException, Throwable
        {   // DMR NEW

        return instantiate( theClass, new Class<?>[] {}, (Object[]) null ) ;

        }   // end no-arg constructor instantiate()


    /**
     * Utility method to instantiate an object of a specified class
     *
     * @param theClass
     *     the class of object to instantiate
     * @param parameterTypes
     *     the types of the constructor's parameters
     * @param arguments
     *     any parameters to pass to the constructor
     *
     * @return the new instance
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     * @throws Throwable
     *     anything thrown by the named method
     */
    public static Object instantiate( final Class<?> theClass,
                                      final Class<?>[] parameterTypes,
                                      final Object... arguments )
        throws TestingException, Throwable
        {

        try
            {
            Constructor<?> theConstructor = null ;

            for ( final Constructor<?> aConstructor : theClass.getDeclaredConstructors() )
                {
                // check the parameters
                final Type[] definedParameterTypes = aConstructor.getParameterTypes() ;
                final Type[] definedGenericParameterTypes = aConstructor.getGenericParameterTypes() ;

                // no parameters defined
                if ( definedParameterTypes.length == 0 )
                    {

                    if ( ( parameterTypes == null ) ||
                         ( parameterTypes.length == 0 ) )
                        {
                        // no parameters supplied - found it
                        theConstructor = aConstructor ;

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

                    for ( int i = 0 ; i < definedParameterTypes.length ; i++ )
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
                    theConstructor = aConstructor ;

                    break ;
                    }

                }

            if ( theConstructor == null )
                {
                // didn't find a matching method
                throw new NoSuchMethodException() ;
                }

            theConstructor.setAccessible( true ) ;

            // for static methods, anInstance is typically null
            return theConstructor.newInstance( arguments ) ;
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

            final String errorMessage = String.format( "Failed to invoke %s-arg constructor %s(%s) in class %s with argument(s): %s:%n\t%s%s",
                                                       ( ( arguments == null ) ||
                                                         ( arguments.length == 0 )
                                                               ? "no"
                                                               : String.format( "%,d",
                                                                                arguments.length ) ),
                                                       theClass.getSimpleName(),
                                                       displayParameterTypes,
                                                       theClass.getSimpleName(),
                                                       ( ( arguments == null ) ||
                                                         ( arguments.length == 0 )
                                                               ? "n/a"
                                                               : datasetToString( arguments ) ),
                                                       e.getClass()
                                                        .getSimpleName(),
                                                       ( e.getMessage() == null
                                                           ? ""
                                                           : ": " +
                                                             e.getMessage() ) ) ;

            throw new TestingException( errorMessage, e ) ;
            }

        }   // end general-purpose instantiate()

    }   // end class ReflectReferenceTypes