package com.joslittho.sunshine;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *  Sunshine
 *
 * A simple weather app
 *
 * Copyright (C) 2016 Kairu Joshua Wambugu
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

// begin class FullTestSuite
public class FullTestSuite extends TestSuite {

    /** CONSTANTS */

    /** Integers */

    /** Strings */

    /** VARIABLES */

    /** CONSTRUCTOR */

    public FullTestSuite() { super(); }

    /** METHODS */

    /** Getters and Setters */

    /** Overrides */

    /** Other Methods */

    /* Get the whole test suite package */
    // begin method suite
    public static Test suite() {

        // TestSuiteBuilder - builds tests
        // includeAllPackagesUnderHere - Include all junit tests that satisfy the requirements
        // in the calling class' package and all sub-package
        // build - called once you've configured your builder as desired.
        // gives a suite containing the requested tests.
        return new TestSuiteBuilder( FullTestSuite.class )
                .includeAllPackagesUnderHere().build();

    } // end method suite

    /** INNER CLASSES */

} // end class FullTestSuite