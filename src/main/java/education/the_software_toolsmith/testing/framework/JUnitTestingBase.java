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

import org.junit.jupiter.api.AfterAll ;
import org.junit.jupiter.api.AfterEach ;
import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.BeforeEach ;
import org.junit.jupiter.api.DisplayName ;
import org.junit.jupiter.api.TestInfo ;
import org.junit.jupiter.api.TestInstance ;
import org.junit.jupiter.api.TestInstance.Lifecycle ;

/**
 * Base class for JUnit testing
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2018-05-25 initial set of tests<br>
 * @version 1.1 2018-06-09 revise structure to use TestInfo instead of certain hard-coded text
 * @version 1.2 2018-09-02 add timeouts
 * @version 1.3 2019-01-14 more implementation
 * @version 1.3.1 2019-01-17 cosmetic changes
 * @version 2.0 2019-05-12
 *     <ul>
 *     <li>restructure tests
 *     <li>disable System.exit() during testing
 *     <li>start making each subtest independent so they'll all run even if one fails
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
 * @version 4.0 2019-07-04 split general purpose utilities methods into separate class
 * @version 5.0 2019-10-07 revise for Stack ADT
 * @version 5.1 2020-01-26 cleanup toward DRCo coding standard compliance
 * @version 5.2 2020-05-14
 *     <ul>
 *     <li>cleanup comments
 *     <li>enhance null argument handling
 *     <li>in parseArguments(): correct numeric range bounds parsing; add support for data-supplied
 *     step, repeating group count, duplicate count
 *     <li>output formatting adjustments to improve alignment and readability
 *     <li>add PlaceholderException to support specific Exception detection
 *     <li>add support for detection and display of boolean/Boolean and char/Character types
 *     </ul>
 * @version 5.3 2020-06-03 add startTest() pass-through for backward compatibility
 * @version 5.4 2020-07-22
 *     <ul>
 *     <li>add instance field retrieval and modification methods
 *     <li>add method invocation support
 *     </ul>
 * @version 5.5 2020-08-07
 *     <ul>
 *     <li>consolidate/simplify field access methods
 *     <li>add collection retrieval methods
 *     </ul>
 * @version 6.0 2020-09-13 Split into general unit testing support and JUnit-specific support - this
 *     class implements the former.
 * @version 6.0.1 2021-06-19
 *     <ul>
 *     <li>reflect move of other classes from {@code ...testing.junit} package to {@code ...testing}
 *     package
 *     <li>switch to fully object-oriented (eliminate static variables)
 *     <li>move per-class setup to {@code TestingBase} constructor
 *     </ul>
 * @version 6.1 2021-06-21 overload {@code TestingBase}'s {@code startTest} methods to handle first
 *     argument ({@code TestInfo testInfo} for backward compatibility
 * @version 6.2 2021-11-18 enhance stub behavior determination
 * @version 6.3 2023-10-19 switch direct access to {@code SecurityManager}, which is deprecated as
 *     of Java 17, to use utility methods in {@code TestingBase}
 * @version 7.0 2025-07-18
 *     <ul>
 *     <li>swap operands to '==' and '!=' when comparing against a constant so the constant is the
 *     left operand
 *     <li>rename {@code xxxEachTest()} to {@code xxxTest()} for greater consistency with class
 *     methods
 *     <li>keep {@code xxxEachTest()} as deprecated, temporary pass-throughs
 *     </ul>
 * @version 7.0.1 2025-07-29 remove deprecated methods - they were executing twice
 * @version 7.0.2 2025-09-30
 *     <ul>
 *     <li>>undo reversing '==' and '!=' comparisons
 *     <li>cosmetic changes
 *     </ul>
 */
@DisplayName( "JUnit Testing Base" )
@TestInstance( Lifecycle.PER_CLASS )
public class JUnitTestingBase extends TestingBase
    {

    /**
     * @param specifiedTestClassPackageName
     *     the name of the package in which {@code specifiedTestClassSimpleName} exists
     * @param specifiedTestClassSimpleName
     *     the simple name of the class under test
     */
    protected JUnitTestingBase( final String specifiedTestClassPackageName,
                                final String specifiedTestClassSimpleName )
        {

        super( specifiedTestClassPackageName, specifiedTestClassSimpleName ) ;

        }   // end 2-arg constructor


    /**
     * @param testInfo
     *     the current test environment
     */
    @BeforeAll
    protected void setUpBeforeClass( final TestInfo testInfo )
        {
        
//        enableDebugging() ;	// DEBUG

        }	// end setUpBeforeClass()


    /**
     * @param testInfo
     *     the current test environment
     */
    @BeforeEach
    protected void setUpBeforeTest( final TestInfo testInfo )
        {

        String baseName = testInfo.getDisplayName() ;
        final int colonColonIndex = baseName.indexOf( "::" ) ;

        if ( colonColonIndex != -1 )
            {
            baseName = baseName.substring( 0, colonColonIndex ).trim() ;
            }

        super.stubBehaviorTag = "" ;	// assume not a stub behavior

        if ( !super.currentTestGroupName.equals( baseName ) )
            {
            // count this test group
            super.currentTestGroup++ ;
            super.currentTestGroupName = baseName ;

            // reset current test counters
            super.currentTestsAttempted = 0 ;
            super.currentTestsSucceeded = 0 ;

            // assume single test (not repeating nor parameterized)
            super.lastTestInGroupIsRunning = true ;

            // there are no non-stub values seen yet
            super.nonStubTestsPassed = 0 ;

            // there are no stub values seen yet
            super.stubBehaviorSeenCount = 0 ;

            // display start of testing (method or category/group of operations)
            writeConsole( "%n[%,2d] Starting tests of %s%n%n",
                          super.currentTestGroup,
                          super.currentTestGroupName ) ;
            }

        // reset test passed flag
        super.currentTestPassed = false ;

        }	// end setUpBeforeEachTest()


    /**
     * @param testInfo
     *     the current test environment
     */
    @AfterAll
    protected void tearDownAfterClass( final TestInfo testInfo )
        {

        // display summary results
        if ( super.totalTestsAttempted > 0 )
            {
            writeConsole( "%n\tSummary Test Results%n%n" ) ;

            for ( final String testResult : super.summaryTestResults )
                {
                writeConsole( "%s%n", testResult ) ;
                }

            writeConsole( "%n\tSuccessfully completed %,3d of %,3d tests (%3d%%) attempted for class %s%n",
                          super.totalTestsSucceeded,
                          super.totalTestsAttempted,
                          ( super.totalTestsSucceeded * 100 ) /
                                                     super.totalTestsAttempted,
                          testInfo.getDisplayName() ) ;
            }
        else
            {
            writeConsole( "%n\tNo tests attempted for class %s%n",
                          testInfo.getDisplayName() ) ;
            }

        // close the detailed log
        super.detailedLogStream.close() ;

        /*
         * re-enable System.exit()
         */
        // restore the saved security manager
        enableExit() ;

        }	// end tearDownAfterClass()


    /**
     * @param testInfo
     *     the current test environment
     */
    @AfterEach
    protected void tearDownAfterTest( final TestInfo testInfo )
        {

        if ( super.currentTestPassed )
            {
            testPassed() ;
            }
        else
            {
            testFailed() ;
            }

        if ( super.lastTestInGroupIsRunning )
            {
            // display stats for this test group

            // filter for stubbed return values
            if ( ( super.stubBehaviorSeenCount > 0 ) &&
                 ( super.nonStubTestsPassed == 0 ) )
                {
                // only saw correct responses which matched the stub values
                // consider this a total failure rather than a (misleading)
                // correct percentage
                writeConsole( "[%,2d] The only tests which passed matched stub behaviors - ignoring them (%,2d)%n",
                              super.currentTestGroup,
                              super.stubBehaviorSeenCount ) ;

                super.currentTestsSucceeded = 0 ;	// clear the success count
                }

            final String testSummary = String.format( "[%,2d] Successfully completed %,3d of %,3d tests (%3d%%) of %s",
                                                      super.currentTestGroup,
                                                      super.currentTestsSucceeded,
                                                      super.currentTestsAttempted,
                                                      ( 0 ==
                                                        super.currentTestsAttempted
                                                            ? 0
                                                            : ( super.currentTestsSucceeded *
                                                                100 ) /
                                                              super.currentTestsAttempted ),
                                                      super.currentTestGroupName ) ;
            super.summaryTestResults.add( testSummary ) ;
            writeConsole( "%s%n%n----------%n", testSummary ) ;

            // accumulate this test group's results
            super.totalTestsAttempted += super.currentTestsAttempted ;
            super.totalTestsSucceeded += super.currentTestsSucceeded ;

            // reset current test counters
            super.currentTestsAttempted = 0 ;
            super.currentTestsSucceeded = 0 ;
            }

        }	// end tearDownAfterEachTest()


    /**
     * Utility to pre-process test parameters - pass-through to method that takes argument labels -
     * for backward compatibility.
     *
     * @param testInfo
     *     info about the test - ignored
     * @param isLastTest
     *     flag to indicate that this is the last dataset for this test
     * @param isStubBehavior
     *     flag to indicate that the result of testing this dataset matches the stubbed behavior
     * @param collectionContentsArguments
     *     contents of one or more collections to populate
     *
     * @return the parsed collectionContentsArguments in order of appearance in the argument list
     */
    protected Object[][] startTest( final TestInfo testInfo,
                                    final boolean isLastTest,
                                    final boolean isStubBehavior,
                                    final String... collectionContentsArguments )
        {

        return super.startTest( isLastTest,
                                isStubBehavior,
                                null,
                                collectionContentsArguments ) ;

        }   // end startTest() pass-through


    /**
     * Utility to pre-process test parameters
     *
     * @param testInfo
     *     info about the test - ignored
     * @param isLastTest
     *     flag to indicate that this is the last dataset for this test
     * @param isStubBehavior
     *     flag to indicate that the result of testing this dataset matches the stubbed behavior
     * @param argumentLabels
     *     descriptive text for each of the collectionContentArguments elements
     * @param collectionContentsArguments
     *     contents of one or more collections to populate
     *
     * @return the parsed collectionContentsArguments in order of appearance in the argument list
     */
    protected Object[][] startTest( final TestInfo testInfo,
                                    final boolean isLastTest,
                                    final boolean isStubBehavior,
                                    final String[] argumentLabels,
                                    final String... collectionContentsArguments )
        {

        return super.startTest( isLastTest,
                                isStubBehavior,
                                argumentLabels,
                                collectionContentsArguments ) ;

        }   // end startTest() pass-through

    }	// end class JUnitTestingBase