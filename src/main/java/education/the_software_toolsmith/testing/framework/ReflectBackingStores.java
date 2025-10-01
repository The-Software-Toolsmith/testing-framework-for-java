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

import static education.the_software_toolsmith.testing.framework.ReflectDataFields.getIntField ;
import static education.the_software_toolsmith.testing.framework.ReflectDataFields.getReferenceField ;

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.HashSet ;
import java.util.LinkedList ;
import java.util.List ;

/**
 * Reflective access to backing stores
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-07-19
 *     <ul>
 *     <li>Initial implementation - extracted from framework's
 *     {@link Reflection}
 *     <li>add {@code xxxDataset()} equivalents of {@code xxxCollect()} to ease
 *     transition to new names with other classes
 *     </ul>
 * @version 1.1 2025-09-29
 *     <ul>
 *     <li>restore {@code xxxCollection()} named methods as pass-throughs so
 *     existing tests don't have to be modified immediately     IN_PROCESS
 *     <li>undo reversing '==' and '!=' comparisons
 *     </ul>
 */
public class ReflectBackingStores
    {


    /*
     * constructors
     */


    /**
     * prevent instantiation
     */
    private ReflectBackingStores()
        {

        // noop

        }   // end no-arg constructor


    /**
     * retrieve the contents of a chain, retaining the order of the entries
     * <p>
     * convenience method for all-arg {@code getChainAsList()} with default values for the data and
     * next field names and circular chain and skip nulls flags
     *
     * @param startNode
     *     reference to the beginning of the chain
     *
     * @return contents the data from the chain
     *
     * @since 1.7
     */
    public static Object[] getChainAsArray( final Object startNode )
        {

        return getChainAsList( startNode ).toArray() ;

        }   // end 1-arg getChainAsArray()


    /**
     * retrieve the contents of a chain, retaining the order of the entries
     * <p>
     * convenience method for all-arg {@code getChainAsList()} with default values for the circular
     * chain and skip nulls flags
     *
     * @param startNode
     *     reference to the beginning of the chain
     * @param dataFieldNameArg
     *     name of the data reference
     * @param nextFieldNameArg
     *     name of the next reference
     *
     * @return contents the data from the chain
     *
     * @since 1.7
     */
    public static Object[] getChainAsArray( final Object startNode,
                                            final String dataFieldNameArg,
                                            final String nextFieldNameArg )
        {

        return getChainAsList( startNode,
                               dataFieldNameArg,
                               nextFieldNameArg ).toArray() ;

        }   // end 3-arg getChainAsArray()


    /**
     * retrieve the contents of a chain, retaining the order of the entries
     *
     * @param startNode
     *     reference to the beginning of the chain
     * @param dataFieldNameArg
     *     name of the data reference
     * @param nextFieldNameArg
     *     name of the next reference
     * @param circularChainArg
     *     {@code true} indicates that the chain is expected to loop back on itself; {@code false}
     *     indicates that a loop is a corrupted chain
     * @param skipNullsArg
     *     {@code true} indicates that {@code null} data in the chain should be skipped;
     *     {@code false} indicates that {@code null} data should be included in the returned
     *     contents
     *
     * @return contents the data from the chain
     *
     * @since 1.7
     */
    public static Object[] getChainAsArray( final Object startNode,
                                            final String dataFieldNameArg,
                                            final String nextFieldNameArg,
                                            final Boolean circularChainArg,
                                            final Boolean skipNullsArg )
        {

        return getChainAsList( startNode,
                               dataFieldNameArg,
                               nextFieldNameArg,
                               circularChainArg,
                               skipNullsArg ).toArray() ;

        }   // end all-arg getChainAsArray()


    /**
     * retrieve the contents of a chain, retaining the order of the entries
     * <p>
     * convenience method for all-arg {@code getChainAsList()} with default values for the data and
     * next field names and circular chain and skip nulls flags
     *
     * @param startNode
     *     reference to the beginning of the chain
     *
     * @return contents the data from the chain
     *
     * @since 1.7
     */
    public static List<?> getChainAsList( final Object startNode )
        {

        return getChainAsList( startNode, null, null, null, null ) ;

        }   // end 1-arg getChainAsList()


    /**
     * retrieve the contents of a chain, retaining the order of the entries
     * <p>
     * convenience method for all-arg {@code getChainAsList()} with default values for the circular
     * chain and skip nulls flags
     *
     * @param startNode
     *     reference to the beginning of the chain
     * @param dataFieldNameArg
     *     name of the data reference
     * @param nextFieldNameArg
     *     name of the next reference
     *
     * @return contents the data from the chain
     *
     * @since 1.7
     */

    public static List<?> getChainAsList( final Object startNode,
                                          final String dataFieldNameArg,
                                          final String nextFieldNameArg )
        {

        return getChainAsList( startNode,
                               dataFieldNameArg,
                               nextFieldNameArg,
                               null,
                               null ) ;

        }   // end 3-arg getChainAsList()


    /**
     * retrieve the contents of a chain, retaining the order of the entries
     *
     * @param startNode
     *     reference to the beginning of the chain
     * @param dataFieldNameArg
     *     name of the data reference
     * @param nextFieldNameArg
     *     name of the next reference
     * @param circularChainArg
     *     {@code true} indicates that the chain is expected to loop back on itself; {@code false}
     *     indicates that a loop is a corrupted chain
     * @param skipNullsArg
     *     {@code true} indicates that {@code null} data in the chain should be skipped;
     *     {@code false} indicates that {@code null} data should be included in the returned
     *     contents
     *
     * @return contents the data from the chain
     *
     * @since 1.7
     */
    public static List<?> getChainAsList( final Object startNode,
                                          final String dataFieldNameArg,
                                          final String nextFieldNameArg,
                                          final Boolean circularChainArg,
                                          final Boolean skipNullsArg )
        {

        // LinkedList avoids resizing array
        final List<Object> contentsList = new LinkedList<>() ;

        // if no starting node, return the empty list
        if ( startNode == null )
            {
            // ArrayList provides best space complexity and fastest positional retrieval
            return new ArrayList<>( contentsList ) ;
            }

        // assertion: we have a chain to process

        // set up - handle omitted (null) arguments

        final String dataFieldName = ( dataFieldNameArg != null )
            ? dataFieldNameArg
            : "data" ;

        final String nextFieldName = ( nextFieldNameArg != null )
            ? nextFieldNameArg
            : "next" ;


        final boolean circularChain = ( circularChainArg == null )
            ? false
            : circularChainArg ;

        final boolean skipNulls = ( skipNullsArg == null )
            ? false
            : skipNullsArg ;


        // for loop detection
        final HashSet<Object> nodesVisited = new HashSet<>() ;

        // for chain traversal
        Object currentNode = startNode ;

        // traverse the chain
        while ( currentNode != null )
            {

            // loop detection
            if ( !nodesVisited.add( currentNode ) )
                {

                // terminal condition - expected if circular
                if ( circularChain )
                    {
                    break ;
                    }

                // corrupted chain - has a loop but is not circular
                throw new LoopDetectedException() ;
                }

            // no loop (yet) - save the data from the current node
            final Object contentsItem = getReferenceField( currentNode,
                                                           dataFieldName ) ;

            // either have non-null contents or include null as valid data
            if ( ( contentsItem != null ) || !skipNulls )
                {
                contentsList.add( contentsItem ) ;
                }

            // move to the next node
            currentNode = getReferenceField( currentNode, nextFieldName ) ;
            }

        // ArrayList provides best space complexity and fastest positional retrieval
        return new ArrayList<>( contentsList ) ;

        }   // end all-arg getChainAsList()


    /**
     * Utility to copy an array-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null}
     *     if {@code collectionToCopy} is {@code null}
     *
     * @deprecated Use {@link #getContentsOfArrayBackedDataset(Object,String)}
     *     instead
     */
    @Deprecated( forRemoval = true, since = "1.0" )
    public static Object[] getContentsOfArrayBackedCollection( final Object collectionToCopy,
                                                               final String backingStoreFieldName )
        {

        return getContentsOfArrayBackedDataset( collectionToCopy, backingStoreFieldName ) ;

        }   // end getContentsOfArrayBackedCollection() pass-through


    /**
     * Utility to copy an array-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     */
    public static Object[] getContentsOfArrayBackedDataset( final Object collectionToCopy,
                                                            final String backingStoreFieldName )
        {

        return getContentsOfArrayBackedDataset( collectionToCopy,
                                                backingStoreFieldName,
                                                null,
                                                -1 ) ;

        }   // end getContentsOfArrayBackedDataset() pass-through


    /**
     * Utility to copy an array-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     * @param entryCountFieldName
     *     field name of the collection's entry count (optional - defaults to "numberOfEntries")
     * @param entryCount
     *     expected number of entries in the collection - only used if non-negative
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     *
     * @deprecated Use {@link #getContentsOfArrayBackedDataset(Object,String,String,int)} instead
     */
    @Deprecated( forRemoval = true, since = "1.0" )
    public static Object[] getContentsOfArrayBackedCollection( final Object collectionToCopy,
                                                               final String backingStoreFieldName,
                                                               final String entryCountFieldName,
                                                               final int entryCount )
            throws TestingException
        {

        return getContentsOfArrayBackedDataset( collectionToCopy,
                                                backingStoreFieldName,
                                                entryCountFieldName,
                                                entryCount ) ;

        }   // end getContentsOfArrayBackedCollection()


    /**
     * Utility to copy an array-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     * @param entryCountFieldName
     *     field name of the collection's entry count (optional - defaults to "numberOfEntries")
     * @param entryCount
     *     expected number of entries in the collection - only used if non-negative
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static Object[] getContentsOfArrayBackedDataset(
                                                            final Object collectionToCopy,
                                                            final String backingStoreFieldName,
                                                            String entryCountFieldName,
                                                            int entryCount )
        throws TestingException
        {

        Object[] collectionContents = null ;

        if ( null != collectionToCopy )
            {

            // handle optional parameters
            if ( entryCountFieldName == null )
                {
                entryCountFieldName = "numberOfEntries" ;
                }

            // get current entry count from the collection if not provided
            if ( entryCount < 0 )
                {

                try
                    {
                    entryCount = getIntField( collectionToCopy,
                                              entryCountFieldName ) ;
                    }
                catch ( IllegalArgumentException | SecurityException e )
                    {
                    final String exceptionClassName = e.getClass()
                                                       .getSimpleName() ;

                    final String errorMessage = String.format( "Failed to retrieve entry count from class %s, field %s, instance %s: %s%s%s",
                                                               collectionToCopy.getClass()
                                                                               .getSimpleName(),
                                                               entryCountFieldName,
                                                               collectionToCopy.toString(),
                                                               exceptionClassName,
                                                               ( e.getMessage() == null
                                                                     ? ""
                                                                     : ": " ),
                                                               ( e.getMessage() == null
                                                                     ? ""
                                                                     : e.getMessage() ) ) ;

                    throw new TestingException( errorMessage, e ) ;
                    }

                }

            // collect the contents of the collection
            try
                {
                collectionContents = Arrays.copyOf( (Object[]) getReferenceField( collectionToCopy,
                                                                                  backingStoreFieldName ),
                                                    entryCount ) ;
                }
            catch ( IllegalArgumentException | SecurityException ex )
                {
                final String exceptionClassName = ex.getClass()
                                                    .getSimpleName() ;

                final String errorMessage = String.format( "Failed to retrieve backing array from class %s, field %s, instance %s: %s%s%s",
                                                           collectionToCopy.getClass()
                                                                           .getSimpleName(),
                                                           backingStoreFieldName,
                                                           collectionToCopy.toString(),
                                                           exceptionClassName,
                                                           ( ex.getMessage() == null
                                                                 ? ""
                                                                 : ": " ),
                                                           ( ex.getMessage() == null
                                                                 ? ""
                                                                 : ex.getMessage() ) ) ;

                throw new TestingException( errorMessage, ex ) ;
                }

            }

        return collectionContents ;

        }   // end getContentsOfArrayBackedDataset()


    /**
     * Utility to copy a circular-array-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     *
     * @deprecated Use {@link #getContentsOfCircularArrayBackedDataset(Object,String)} instead
     */
    @Deprecated( forRemoval = true, since = "1.0" )
    public static Object[] getContentsOfCircularArrayBackedCollection(
                                                                       final Object collectionToCopy,
                                                                       final String backingStoreFieldName )
        {

        return getContentsOfCircularArrayBackedDataset( collectionToCopy,
                                                        backingStoreFieldName ) ;

        }   // end getContentsOfCircularArrayBackedCollection() pass-through


    /**
     * Utility to copy a circular-array-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     */
    public static Object[] getContentsOfCircularArrayBackedDataset(
                                                                    final Object collectionToCopy,
                                                                    final String backingStoreFieldName )
        {

        return getContentsOfCircularArrayBackedDataset( collectionToCopy,
                                                        backingStoreFieldName,
                                                        null,
                                                        null,
                                                        null,
                                                        -1 ) ;

        }   // end getContentsOfCircularArrayBackedDataset() pass-through


    /**
     * Utility to copy a circular-array-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     * @param frontIndexFieldName
     *     field name of the collection's front index (optional - defaults to "frontIndex"
     * @param backIndexFieldName
     *     field name of the collection's back index (optional - defaults to "backIndex"
     * @param entryCountFieldName
     *     field name of the collection's entry count (optional - defaults to "numberOfEntries")
     * @param entryCount
     *     expected number of entries in the collection - only used if non-negative
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     *
     * @deprecated Use
     *     {@link #getContentsOfCircularArrayBackedDataset(Object,String,String,String,String,int)}
     *     instead
     */
    @Deprecated( forRemoval = true, since = "1.0" )
    public static Object[] getContentsOfCircularArrayBackedCollection(
                                                                       final Object collectionToCopy,
                                                                       final String backingStoreFieldName,
                                                                       final String frontIndexFieldName,
                                                                       final String backIndexFieldName,
                                                                       final String entryCountFieldName,
                                                                       final int entryCount )
        throws TestingException
        {

        return getContentsOfCircularArrayBackedDataset( collectionToCopy,
                                                        backingStoreFieldName,
                                                        frontIndexFieldName,
                                                        backIndexFieldName,
                                                        entryCountFieldName,
                                                        entryCount ) ;

        }   // end getContentsOfCircularArrayBackedCollection()


    /**
     * Utility to copy a circular-array-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     * @param frontIndexFieldName
     *     field name of the collection's front index (optional - defaults to "frontIndex"
     * @param backIndexFieldName
     *     field name of the collection's back index (optional - defaults to "backIndex"
     * @param entryCountFieldName
     *     field name of the collection's entry count (optional - defaults to "numberOfEntries")
     * @param entryCount
     *     expected number of entries in the collection - only used if non-negative
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static Object[] getContentsOfCircularArrayBackedDataset(
                                                                    final Object collectionToCopy,
                                                                    final String backingStoreFieldName,
                                                                    String frontIndexFieldName,
                                                                    String backIndexFieldName,
                                                                    String entryCountFieldName,
                                                                    int entryCount )
        throws TestingException
        {

        Object[] collectionContents = null ;

        if ( collectionToCopy != null )
            {

            // handle optional parameters
            if ( entryCountFieldName == null )
                {
                entryCountFieldName = "numberOfEntries" ;
                }

            if ( frontIndexFieldName == null )
                {
                frontIndexFieldName = "frontIndex" ;
                }

            if ( null == backIndexFieldName )
                {
                backIndexFieldName = "backIndex" ;
                }

            // get current entry count from the collection if not provided
            if ( entryCount < 0 )
                {

                try
                    {
                    entryCount = getIntField( collectionToCopy,
                                              entryCountFieldName ) ;
                    }
                catch ( IllegalArgumentException | SecurityException e )
                    {
                    final String exceptionClassName = e.getClass()
                                                       .getSimpleName() ;

                    final String errorMessage = String.format( "Failed to retrieve entry count from class %s, field %s, instance %s: %s%s%s",
                                                               collectionToCopy.getClass()
                                                                               .getSimpleName(),
                                                               entryCountFieldName,
                                                               collectionToCopy.toString(),
                                                               exceptionClassName,
                                                               ( e.getMessage() == null
                                                                     ? ""
                                                                     : ": " ),
                                                               ( e.getMessage() == null
                                                                     ? ""
                                                                     : e.getMessage() ) ) ;

                    throw new TestingException( errorMessage, e ) ;
                    }

                }

            int frontIndex ;

            try
                {
                frontIndex = getIntField( collectionToCopy,
                                          frontIndexFieldName ) ;
                }
            catch ( IllegalArgumentException | SecurityException e )
                {
                final String exceptionClassName = e.getClass().getSimpleName() ;

                final String errorMessage = String.format( "Failed to retrieve front index from class %s, field %s, instance %s: %s%s%s",
                                                           collectionToCopy.getClass()
                                                                           .getSimpleName(),
                                                           frontIndexFieldName,
                                                           collectionToCopy.toString(),
                                                           exceptionClassName,
                                                           ( e.getMessage() == null
                                                                 ? ""
                                                                 : ": " ),
                                                           ( e.getMessage() == null
                                                                 ? ""
                                                                 : e.getMessage() ) ) ;

                throw new TestingException( errorMessage, e ) ;
                }

            @SuppressWarnings( "unused" )
            int backIndex ;     // FUTURE

            try
                {
                backIndex = getIntField( collectionToCopy,
                                         backIndexFieldName ) ;
                }
            catch ( IllegalArgumentException | SecurityException e )
                {
                final String exceptionClassName = e.getClass().getSimpleName() ;

                final String errorMessage = String.format( "Failed to retrieve back index from class %s, field %s, instance %s: %s%s%s",
                                                           collectionToCopy.getClass()
                                                                           .getSimpleName(),
                                                           backIndexFieldName,
                                                           collectionToCopy.toString(),
                                                           exceptionClassName,
                                                           ( e.getMessage() == null
                                                                 ? ""
                                                                 : ": " ),
                                                           ( e.getMessage() == null
                                                                 ? ""
                                                                 : e.getMessage() ) ) ;

                throw new TestingException( errorMessage, e ) ;
                }

            Object[] backingStoreArray ;

            try
                {
                backingStoreArray = (Object[]) getReferenceField( collectionToCopy,
                                                                  backingStoreFieldName ) ;
                }
            catch ( IllegalArgumentException | SecurityException e )
                {
                final String exceptionClassName = e.getClass().getSimpleName() ;

                final String errorMessage = String.format( "Failed to retrieve backing store array from class %s, field %s, instance %s: %s%s%s",
                                                           collectionToCopy.getClass()
                                                                           .getSimpleName(),
                                                           backingStoreFieldName,
                                                           collectionToCopy.toString(),
                                                           exceptionClassName,
                                                           ( e.getMessage() == null
                                                                 ? ""
                                                                 : ": " ),
                                                           ( e.getMessage() == null
                                                                 ? ""
                                                                 : e.getMessage() ) ) ;

                throw new TestingException( errorMessage, e ) ;
                }

            // collect the contents of the collection
            try
                {
                // instantiate an array sized according to the entry count
                collectionContents = new Object[ entryCount ] ;

                if ( entryCount > 0 )
                    {
                    // copy the front elements
                    System.arraycopy( backingStoreArray,            // from
                                      frontIndex,
                                      collectionContents,           // to
                                      0,
                                      backingStoreArray.length - frontIndex ) ;
                    // how many elements

                    // copy the back elements
                    System.arraycopy( backingStoreArray,            // from
                                      0,
                                      collectionContents,           // to
                                      backingStoreArray.length - frontIndex,
                                      frontIndex ) ;                // how many elements
                    }

                }
            catch ( IllegalArgumentException | SecurityException ex )
                {
                final String exceptionClassName = ex.getClass()
                                                    .getSimpleName() ;

                final String errorMessage = String.format( "Failed to retrieve backing array from class %s, field %s, instance %s: %s%s%s",
                                                           collectionToCopy.getClass()
                                                                           .getSimpleName(),
                                                           backingStoreFieldName,
                                                           collectionToCopy.toString(),
                                                           exceptionClassName,
                                                           ( ex.getMessage() == null
                                                                 ? ""
                                                                 : ": " ),
                                                           ( ex.getMessage() == null
                                                                 ? ""
                                                                 : ex.getMessage() ) ) ;

                throw new TestingException( errorMessage, ex ) ;
                }
            catch ( final ArrayIndexOutOfBoundsException ex )
                {
                final String exceptionClassName = ex.getClass()
                                                    .getSimpleName() ;

                final String errorMessage = String.format( "Failed to retrieve backing array from class %s, field %s, instance %s: inconsistent instance state: %s%s%s",
                                                           collectionToCopy.getClass()
                                                                           .getSimpleName(),
                                                           backingStoreFieldName,
                                                           collectionToCopy.toString(),
                                                           exceptionClassName,
                                                           ( ex.getMessage() == null
                                                                 ? ""
                                                                 : ": " ),
                                                           ( ex.getMessage() == null
                                                                 ? ""
                                                                 : ex.getMessage() ) ) ;

                throw new TestingException( errorMessage, ex ) ;
                }

            }

        return collectionContents ;

        }   // end getContentsOfCircularArrayBackedDataset()


    /**
     * Utility to copy a chain-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     */
    @Deprecated( forRemoval = true, since = "1.7" )
    public static Object[] getContentsOfChainBackedCollection(
                                                               final Object collectionToCopy )
        {

        return getContentsOfChainBackedCollection( collectionToCopy,
                                                   "firstNode" ) ;

        }   // end getContentsOfChainBackedCollection()


    /**
     * Utility to copy a chain-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     */
    @Deprecated( forRemoval = true, since = "1.7" )
    public static Object[] getContentsOfChainBackedCollection(
                                                               final Object collectionToCopy,
                                                               final String backingStoreFieldName )
        {

        return getContentsOfChainBackedDataset( collectionToCopy,
                                                backingStoreFieldName,
                                                null,
                                                -1,
                                                null,
                                                null ) ;

        }   // end getContentsOfChainBackedCollection()


    /**
     * Utility to copy a chain-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     * @param entryCountFieldName
     *     field name of the collection's entry count (optional - defaults to "numberOfEntries")
     * @param entryCount
     *     expected number of entries in the collection - only used if non-negative
     * @param nodeDataFieldName
     *     field name of the collection's node's data reference (optional - defaults to "data")
     * @param nodeNextFieldName
     *     field name of the collection's node's next reference (optional - defaults to "next")
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null} if
     *     {@code collectionToCopy} is {@code null}
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     *
     * @deprecated Use
     *     {@link #getContentsOfChainBackedDataset(Object,String,String,int,String,String)} instead
     */
    @Deprecated( forRemoval = true, since = "1.0" )
    public static Object[] getContentsOfChainBackedCollection(
                                                               final Object collectionToCopy,
                                                               final String backingStoreFieldName,
                                                               final String entryCountFieldName,
                                                               final int entryCount,
                                                               final String nodeDataFieldName,
                                                               final String nodeNextFieldName )
        throws TestingException
        {

        return getContentsOfChainBackedDataset( collectionToCopy,
                                                backingStoreFieldName,
                                                entryCountFieldName,
                                                entryCount,
                                                nodeDataFieldName,
                                                nodeNextFieldName ) ;

        }   // end getContentsOfChainBackedCollection()


    /**
     * Utility to copy a chain-backed collection's contents into an array
     *
     * @param collectionToCopy
     *     the collection to copy
     * @param backingStoreFieldName
     *     field name of the collection's backing store
     * @param entryCountFieldName
     *     field name of the collection's entry count (optional - defaults to
     *     "numberOfEntries")
     * @param entryCount
     *     expected number of entries in the collection - only used if
     *     non-negative
     * @param nodeDataFieldName
     *     field name of the collection's node's data reference (optional -
     *     defaults to "data")
     * @param nodeNextFieldName
     *     field name of the collection's node's next reference (optional -
     *     defaults to "next")
     *
     * @return array of the contents of {@code collectionToCopy} or {@code null}
     *     if {@code collectionToCopy} is {@code null}
     *
     * @throws TestingException
     *     any wrapped exceptions which may be thrown by reflection
     */
    public static Object[] getContentsOfChainBackedDataset( final Object collectionToCopy,
                                                            final String backingStoreFieldName,
                                                            String entryCountFieldName,
                                                            int entryCount,
                                                            String nodeDataFieldName,
                                                            String nodeNextFieldName )
            throws TestingException
        {

        Object[] collectionContents = null ;

        if ( collectionToCopy != null )
            {

            // handle optional parameters
            if ( entryCountFieldName == null )
                {
                entryCountFieldName = "numberOfEntries" ;
                }

            if ( nodeDataFieldName == null )
                {
                nodeDataFieldName = "data" ;
                }

            if ( nodeNextFieldName == null )
                {
                nodeNextFieldName = "next" ;
                }

            // get current entry count from the collection if not provided
            if ( entryCount < 0 )
                {

                try
                    {
                    entryCount = getIntField( collectionToCopy,
                                              entryCountFieldName ) ;
                    }
                catch ( IllegalArgumentException | SecurityException e )
                    {
                    final String exceptionClassName = e.getClass()
                                                       .getSimpleName() ;

                    final String errorMessage = String.format( "Failed to retrieve entry count from class %s, field %s, instance %s: %s%s%s",
                                                               collectionToCopy.getClass()
                                                                               .getSimpleName(),
                                                               entryCountFieldName,
                                                               collectionToCopy.toString(),
                                                               exceptionClassName,
                                                               ( e.getMessage() == null
                                                                     ? ""
                                                                     : ": " ),
                                                               ( e.getMessage() == null
                                                                     ? ""
                                                                     : e.getMessage() ) ) ;

                    throw new TestingException( errorMessage, e ) ;
                    }

                }

            // instantiate an array to hold the collection's contents
            collectionContents = new Object[ entryCount ] ;

            // collect the contents of the chain
            try
                {
                Object currentNode = getReferenceField( collectionToCopy,
                                                        backingStoreFieldName ) ;
                int i = 0 ;

                while ( currentNode != null )
                    {

                    if ( i == entryCount )
                        {
                        throw new TestingException( "too many Nodes or a loop detected" ) ;
                        }

                    collectionContents[ i++ ] = getReferenceField( currentNode,
                                                                   nodeDataFieldName ) ;
                    currentNode = getReferenceField( currentNode,
                                                     nodeNextFieldName ) ;
                    }

                }
            catch ( IllegalArgumentException | SecurityException ex )
                {
                final String exceptionClassName = ex.getClass()
                                                    .getSimpleName() ;

                final String errorMessage = String.format( "Failed to retrieve backing chain from class %s, field %s, instance %s: %s%s%s",
                                                           collectionToCopy.getClass()
                                                                           .getSimpleName(),
                                                           backingStoreFieldName,
                                                           collectionToCopy.toString(),
                                                           exceptionClassName,
                                                           ( ex.getMessage() == null
                                                                 ? ""
                                                                 : ": " ),
                                                           ( ex.getMessage() == null
                                                                 ? ""
                                                                 : ex.getMessage() ) ) ;

                throw new TestingException( errorMessage, ex ) ;
                }

            }

        return collectionContents ;

        }   // end getContentsOfChainBackedDataset()

    }   // end class ReflectBackingStores