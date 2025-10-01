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

import static education.the_software_toolsmith.testing.framework.Reflection.isArray ;

import java.lang.reflect.Array ;
import java.util.Arrays ;

/**
 * Utility class for testing: data collection manipulations.
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2018-05-25 initial set of tests
 * @version 1.1 2018-06-09 revise structure to use TestInfo instead of certain
 *     hard-coded text
 * @version 1.2 2018-09-02 add timeouts
 * @version 1.3 2019-01-14 more implementation
 * @version 1.3.1 2019-01-17 cosmetic changes
 * @version 2.0 2019-05-12
 *     <ul>
 *     <li>restructure tests
 *     <li>disable System.exit() during testing
 *     <li>start making each subtest independent so they'll all run even if one
 *     fails
 *     </ul>
 * @version 2.1 2019-05-17
 *     <ul>
 *     <li>rename class
 *     <li>remove unnecessary throws clauses from @BeforeXxx and @AfterXxx
 *     <li>more fully utilize JUnit 5.4 features
 *     <li>switch tests to data-driven
 *     </ul>
 * @version 3.0 2019-06-27
 *     <ul>
 *     <li>complete re-write with reusable testing infrastructure
 *     <li>tests are now data-driven
 *     <li>add summary test results
 *     </ul>
 * @version 3.1 2019-06-28 move detailed activity to log file
 * @version 4.0 2019-07-04 split general purpose utilities methods into separate
 *     class
 * @version 5.0 2019-10-07 revise for Stack ADT
 * @version 5.1 2020-01-26 cleanup toward DRCo coding standard compliance
 * @version 5.2 2020-05-14
 *     <ul>
 *     <li>cleanup comments
 *     <li>enhance null argument handling
 *     <li>in parseArguments(): correct numeric range bounds parsing; add
 *     support for data-supplied step, repeating group count, duplicate count
 *     <li>output formatting adjustments to improve alignment and readability
 *     <li>add PlaceholderException to support specific Exception detection
 *     <li>add support for detection and display of boolean/Boolean and
 *     char/Character types
 *     </ul>
 * @version 5.3 2020-06-03 add startTest() pass-through for backward
 *     compatibility
 * @version 5.4 2020-07-22
 *     <ul>
 *     <li>add instance field retrieval and modification methods
 *     <li>add method invocation support
 *     </ul>
 * @version 5.5 2020-08-07
 *     <ul>
 *     <li>consolidate/simplify field access methods
 *     <li>add collection retrieval methods
 *     <li>enhance itemToString()
 *     <li>add itemToStringWithType()
 *     </ul>
 * @version 5.6 2021-04-11 Repackage as canned utility testing suite
 * @version 5.7 2021-06-19
 *     <ul>
 *     <li>remove dependencies upon JUnit
 *     <li>enhance countOccurances() to count matching {@code null}s
 *     </ul>
 * @version 5.8 2021-08-05 enhance {@code itemToString()}
 * @version 5.9 2021-11-18 enhance {@code compareArray()} to test for equal or
 *     identical contents
 * @version 5.10 2025-01-26 recode if/else in {@code itemToString()} to use
 *     switch for clarity
 * @version 5.11 2025-02-28
 *     <ul>
 *     <li>change {@code default} condition in {@code itemToString()} to leave
 *     'other' types to their own devices based on their {@code toString()}
 *     <li>enable {@code main()}
 *     </ul>
 * @version 5.12 2025-03-09 minor cosmetic changes to {@code main()} and
 *     {@code itemToString()}
 * @version 5.13 2025-03-15 replace remaining {@code item.toString()} with
 *     {@code itemToString( item )} for consistency
 * @version 6.0 2025-03-17
 *     <ul>
 *     <li>replace remaining {@code toString()}s with {@code itemToString()},
 *     except for {@code StringBuilder} instances, for more consistent
 *     formatting
 *     <li>remove conditional logic to format {@code null} as "null" in all
 *     methods except itemToString()}
 *     <li>generalizations to prepare to support additional collection types
 *     <ul>
 *     <li>rename/remove {@code compareArrays()} methods to corresponding
 *     {@code compareDatasets()}
 *     <li>rename/remove {@code arrayToString()} and its variants to
 *     corresponding {@code datasetToString()} equivalents
 *     <li>rename/remove {@code arrayContains()} to {@code datasetContains()}
 *     </ul>
 *     </ul>
 * @version 6.0.1 2025-07-18 swap operands to '==' and '!=' when comparing
 *     against a constant so the constant is the left operand
 * @version 6.0.2 2025-09-05
 *     <ul>
 *     <li>add tests for some edge cases
 *     <li>some cosmetic changes wrt line wrapping
 *     </ul>
 * @version 6.0.3 2025-09-29 undo reversing '==' and '!=' comparisons
 *
 * @since 5.6
 */
public class TestData
    {
    // --------------------------------------------------
    //
    // The following utilities are primarily used by the unit test methods
    //
    // --------------------------------------------------

    /*
     * constants for use with datasetToString()
     */
    /**
     * default maximum length (in characters) a text version of a dataset can take before the text
     * is truncated in the middle
     */
    public final static int DEFAULT_DATASET_TO_STRING_LENGTH = 200 ;
    /**
     * the default number of elements datasetToString() will include in truncated text
     */
    public final static int DEFAULT_DATASET_TO_STRING_ELEMENTS = 50 ;


    /**
     * Return a limited portion of a text representation of a dataset
     *
     * @param theDataset
     *     the dataset to convert to text
     *
     * @return the result of {@code datasetToString( theDataset, DEFAULT_DATASET_TO_STRING_LENGTH )}
     *
     * @since 6.0
     */
    public static String datasetToString( final Object[] theDataset )
        {

        return datasetToString( theDataset,
                                DEFAULT_DATASET_TO_STRING_LENGTH,
                                DEFAULT_DATASET_TO_STRING_ELEMENTS ) ;

        }   // end 1-arg datasetToString()


    /**
     * Return a limited portion of a text representation of a dataset
     *
     * @param theDataset
     *     the dataset to convert to text
     * @param maximumLength
     *     the maximum number of characters to return
     * @param maximumElements
     *     the maximum number of elements to return
     *
     * @return if the full text representation has no more than maximumLength characters, the full
     *     text; otherwise the first maximumElements / 2 elements followed by " ... " then the last
     *     maximumElements / 2 elements
     * 
     * @since 6.0
     */
    public static String datasetToString( final Object[] theDataset,
                                          final int maximumLength,
                                          final int maximumElements )
        {

        final String fullString = datasetToFullString( theDataset ) ;

        if ( ( fullString == null ) ||
             ( fullString.length() <= maximumLength ) )
            {
            return fullString ;
            }

        final int halfCount = maximumElements / 2 ;
        final StringBuilder partsString = new StringBuilder( "[" ) ;

        for ( int i = 0 ; i < halfCount ; i++ )
            {
            partsString.append( itemToString( theDataset[ i ] ) ) ;
            partsString.append( ", " ) ;
            }

        partsString.append( "..." ) ;

        for ( int i = theDataset.length - halfCount ;
              i < theDataset.length ;
              i++ )
            {
            partsString.append( ", " ) ;
            partsString.append( itemToString( theDataset[ i ] ) ) ;
            }

        partsString.append( "]" ) ;

        return partsString.toString() ;

        }   // end 3-arg datasetToString()


    /**
     * Create a text representation of a dataset similar to {@code Arrays.toString()} but with items
     * appropriately formatted and delimited (e.g. Strings appear within "")
     *
     * @param theDataset
     *     the items to include in the resulting text
     *
     * @return text representing the appropriately delimited elements of {@code theDataset}
     *
     * @since 6.0
     */
    public static String datasetToFullString( final Object[] theDataset )
        {

        if ( theDataset == null )
            {
            return null ;
            }

        final StringBuilder fullString = new StringBuilder( "[" ) ;

        String delimiter = "" ;

        for ( final Object anItem : theDataset )
            {
            fullString.append( delimiter ) ;
            fullString.append( itemToString( anItem ) ) ;

            delimiter = ", " ;
            }

        fullString.append( "]" ) ;

        return fullString.toString() ;

        }   // end datasetToFullString()


    /**
     * Convert an {@code item} to its text representation, delimited appropriately prefixed with the
     * (component) type
     *
     * @param item
     *     the object to convert
     *
     * @return if {@code item} is null, "null" without delimiters, otherwise, the appropriately
     *     delimited object's String representation
     *
     * @since 5.5
     */
    public static String itemToStringWithType( final Object item )
        {

        return item == null
                ? "null"
                : ( isArray( item.getClass() )
                        ? item.getClass().getComponentType().getSimpleName()
                        : item.getClass().getSimpleName() )
                  + " " + itemToString( item ) ;

        }  // end itemToStringWithType()


    /**
     * Convert an {@code item} to its text representation, delimited appropriately
     *
     * @param item
     *     the object to convert
     *
     * @return if {@code item} is null, "null" without delimiters, otherwise, the appropriately
     *     delimited object's String representation
     *
     * @since 5.4
     */
    public static String itemToString( final Object item )
        {

        if ( item == null )
            {
            return "null" ;
            }

        if ( isArray( item.getClass() ) )
            {
            final int itemCount = Array.getLength( item ) ;
            final Object[] items = new Object[ itemCount ] ;

            for ( int i = 0 ; i < itemCount ; i++ )
                {
                items[ i ] = Array.get( item, i ) ;
                }

            return datasetToString( items ) ;
            }

        final String stringDelimiter = "\"" ;
        final String charDelimiter = "'" ;
        String useDelimiter = "" ;

        String conversionSpecifier = "%s" ;

        switch ( item )
            {
            case final String _
                -> useDelimiter = stringDelimiter ;
            case final Character _
                -> useDelimiter = charDelimiter ;
            case final Long _,
                 final Integer _,
                 final Short _,
                 final Byte _
                -> conversionSpecifier = "%,d" ;
            case final Double _,
                 final Float _
                -> conversionSpecifier = "%,f" ;
            case final Boolean _
                ->
                { /* noop - use default %s */ }
            // can't have null here
            default
                ->
                { /* all others - noop - use default %s */ }
            }

        return String.format( "%s" + conversionSpecifier + "%s",
                              useDelimiter,
                              item,
                              useDelimiter ) ;

        }   // end itemToString()


    /*
     * constants for use with compareDatasets()
     */


    /**
     * flag that dataset is ordered and may not be reordered for comparison - paired with
     * IS_UNORDERED
     */
    public final static boolean IS_ORDERED = true ;
    /**
     * flag that dataset is unordered and may be reordered for comparison - paired with IS_ORDERED
     */
    public final static boolean IS_UNORDERED = false ;
    /**
     * flag that dataset comparison should test for equal, not identical, instances - paired with
     * COMPARE_IDENTITY
     */
    public final static boolean COMPARE_EQUALITY = true ;
    /**
     * flag that dataset comparison should test for identical instances, implicitly equal - paired
     * with COMPARE_EQUALITY
     */
    public final static boolean COMPARE_IDENTITY = false ;


    /**
     * Determine if two datasets contain the same contents ({@code equals()} - convenience method
     * for backward compatibility
     *
     * @param expected
     *     the dataset of elements as they should appear
     * @param actual
     *     the dataset of elements to be verified against {@code expected}
     * @param ordered
     *     if true, elements of expected and actual must appear in the same order; if false, the
     *     contents may appear in any order
     * 
     * @throws TestingException
     *     when an operation (e.g., comparison) fails, indicates the nature of the failure
     * 
     * @since 6.0
     */
    public static void compareDatasets( final Object[] expected,
                                        final Object[] actual,
                                        final boolean ordered )
        throws TestingException
        {

        compareDatasets( expected, actual, ordered, COMPARE_EQUALITY ) ;

        }   // end 3-arg compareDatasets() pass-through


    /**
     * Determine if two datasets contain the same contents
     *
     * @param expected
     *     the dataset of elements as they should appear
     * @param actual
     *     the dataset of elements to be verified against expected
     * @param ordered
     *     if true, elements of expected and actual must appear in the same order; if false, the
     *     contents may appear in any order
     * @param compareEquality
     *     if true, elements of expected and actual will be compared using {@code equals()}; if
     *     false, the comparison will use {@code ==}
     *
     * @throws TestingException
     *     when an operation (e.g., comparison) fails, indicates the nature of the failure
     *
     * @since 6.0
     */
    public static void compareDatasets( final Object[] expected,
                                        final Object[] actual,
                                        final boolean ordered,
                                        final boolean compareEquality )
        throws TestingException
        {

        // if both dataset references are null, succeed
        if ( ( expected == null ) && ( actual == null ) )       // both null
            {
            return ;
            }

        // assertion: at least one reference (expected, actual) must be non-null

        // if one dataset reference is null and the other is empty, fail
        // REPORT_BUG the compiler isn't recognizing that the test for non-null is redundant
        // REPORT_BUG can't be here if expected and actual are both null
        if ( ( ( expected ==  null ) &&
               ( ( actual != null ) && ( actual.length == 0 ) ) ) ||  // expected is null, actual is
                                                                      // empty
             ( ( actual == null ) &&
               ( ( expected != null ) && ( expected.length == 0 ) ) ) )   // actual is null,
                                                                          // expected is empty
            {
            throw new TestingException( "comparing null to empty []" ) ;
            }

        if ( ( expected == null ) || ( actual == null ) )
            {
            // 2xCk should this be IllegalStateException? other?
            throw new TestingException( "bad test data detected: one dataset is null and the other is neither null nor empty" ) ;
            }

        // assertion: both references (expected, actual) must be non-null

        // make sure the two datasets contain the same number of elements
        if ( expected.length != actual.length )
            {
            throw new TestingException( String.format( "dataset length mismatch: expected: %,d; actual: %,d",
                                                       expected.length,
                                                       actual.length ) ) ;
            }

        // assertion: expected and actual are the same length

        // make copies of the datasets so we don't affect the contents/order of the originals
        final Object[] workingExpected = Arrays.copyOf( expected,
                                                        expected.length ) ;
        final Object[] workingActual = Arrays.copyOf( actual, actual.length ) ;

        // if the order of the contents of the datasets isn't ordered, (stable) sort them
        if ( !ordered )
            {
            Arrays.sort( workingExpected ) ;
            Arrays.sort( workingActual ) ;
            }

        // compare the contents of the datasets
        for ( int i = 0 ; i < workingExpected.length ; i++ )
            {
            final boolean matched = compareEquality
                ? workingExpected[ i ].equals( workingActual[ i ] )
                : workingExpected[ i ] == workingActual[ i ] ;

            if ( !matched )
                {
                final StringBuilder message = new StringBuilder( String.format( "element mismatch: expected: %s; actual: %s",
                                                                                itemToString( workingExpected[ i ] ),
                                                                                itemToString( workingActual[ i ] ) ) ) ;

                if ( ordered )     // include the position
                    {
                    message.append( String.format( " at index %,d", i ) ) ;
                    }

                throw new TestingException( message.toString() ) ;
                }

            }

        // datasets are the same

        }  // end compareDatasets()


    /**
     * Count the number of occurrences of a value in a dataset
     * <p>
     * Note: a {@code null} dataset element will match an argument of {@code null}
     *
     * @param values
     *     the collection of values
     * @param testValue
     *     the test value
     *
     * @return the number of times testValue occurred in values
     *
     * @since 5.7
     */
    public static int countOccurrences( final Object[] values,
                                        final Object testValue )
        {

        int occurrences = 0 ;

        for ( final Object value : values )
            {

            if ( value == null )
                {

                if ( testValue == null )
                    {
                    occurrences++ ;
                    }

                }
            else if ( value.equals( testValue ) )
                {
                occurrences++ ;
                }

            }

        return occurrences ;

        }   // end countOccurrences()


    /**
     * Determine if {@code testValue} occurs at least once in a dataset
     * <p>
     * this method will safely search for null
     *
     * @param values
     *     the collection of values
     * @param testValue
     *     the value to look for
     *
     * @return true if at least one occurrence; false if no occurrences
     *
     * @since 6.0
     */
    public static boolean datasetContains( final Object[] values,
                                           final Object testValue )
        {

        // proceed if we have a dataset to search
        if ( ( values == null ) || ( values.length == 0 ) )
            {
            return false ;
            }

        // assertion: we have a non-empty dataset

        for ( final Object value : values )
            {

            if ( ( ( value == null ) && ( testValue == null ) )
                 || ( ( value != null ) && ( value.equals( testValue ) ) ) )
                {

                // matched non-null
                return true ;

                }

            }

        // no match
        return false ;

        }   // end datasetContains()


    /**
     * Test driver
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        final String formatSpecification = "%17s: %s%n" ;

        System.out.printf( formatSpecification,
                           "String",
                           itemToString( "testing ... testing ... is this thing on?" ) ) ;
        System.out.printf( formatSpecification,
                           "Character",
                           itemToString( 'Y' ) ) ;
        System.out.printf( formatSpecification,
                           "Boolean",
                           itemToString( true ) ) ;
        System.out.printf( formatSpecification,
                           "Long",
                           itemToString( 123_456L ) ) ;
        System.out.printf( formatSpecification,
                           "Integer",
                           itemToString( 789_012 ) ) ;
        System.out.printf( formatSpecification,
                           "Short",
                           itemToString( Short.valueOf( (short) 13_579 ) ) ) ;
        System.out.printf( formatSpecification,
                           "Byte",
                           itemToString( Byte.valueOf( (byte) 123 ) ) ) ;
        System.out.printf( formatSpecification,
                           "Double",
                           itemToString( 123_456_789.012_345_678 ) ) ;
        System.out.printf( formatSpecification,
                           "Float",
                           itemToString( 901_234.567_890F ) ) ;
        System.out.printf( formatSpecification,
                           "Array of Integer",
                           itemToString( new Integer[]
                           { 1, 2, 3 } ) ) ;
        System.out.printf( formatSpecification,
                           "Array of String",
                           itemToString( new String[]
                           { "1", "2", "3" } ) ) ;
        System.out.printf( formatSpecification,
                           "Array of Float",
                           itemToString( new Float[]
                           {} ) ) ;
        System.out.printf( formatSpecification,
                           "Array of Boolean",
                           itemToString( new Boolean[]
                           { null } ) ) ;
        System.out.printf( formatSpecification,
                           "null",
                           itemToString( null ) ) ;

        System.out.printf( "%n" ) ;

        System.out.printf( formatSpecification,
                           "String",
                           itemToStringWithType( "testing ... testing ... is this thing on?" ) ) ;
        System.out.printf( formatSpecification,
                           "Character",
                           itemToStringWithType( 'Y' ) ) ;
        System.out.printf( formatSpecification,
                           "Boolean",
                           itemToStringWithType( true ) ) ;
        System.out.printf( formatSpecification,
                           "Long",
                           itemToStringWithType( 123_456L ) ) ;
        System.out.printf( formatSpecification,
                           "Integer",
                           itemToStringWithType( 789_012 ) ) ;
        System.out.printf( formatSpecification,
                           "Short",
                           itemToStringWithType( Short.valueOf( (short) 13_579 ) ) ) ;
        System.out.printf( formatSpecification,
                           "Byte",
                           itemToStringWithType( Byte.valueOf( (byte) 123 ) ) ) ;
        System.out.printf( formatSpecification,
                           "Double",
                           itemToStringWithType( 123_456_789.012_345_678 ) ) ;
        System.out.printf( formatSpecification,
                           "Float",
                           itemToStringWithType( 901_234.567_890F ) ) ;
        System.out.printf( formatSpecification,
                           "Array of Integer",
                           itemToStringWithType( new Integer[]
                           { 1, 2, 3 } ) ) ;
        System.out.printf( formatSpecification,
                           "Array of String",
                           itemToStringWithType( new String[]
                           { "1", "2", "3" } ) ) ;
        System.out.printf( formatSpecification,
                           "Array of Float",
                           itemToStringWithType( new Float[]
                           {} ) ) ;
        System.out.printf( formatSpecification,
                           "Array of Float",
                           itemToStringWithType( new Boolean[]
                           { null } ) ) ;
        System.out.printf( formatSpecification,
                           "null",
                           itemToStringWithType( null ) ) ;

        }   // end main()

    }   // end class TestData