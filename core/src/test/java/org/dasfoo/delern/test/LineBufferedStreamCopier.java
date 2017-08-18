/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

class LineBufferedStreamCopier extends Thread {
    private static final String ENCODING = "UTF-8";

    private InputStream mInput;
    private PrintStream mOutput;

    LineBufferedStreamCopier(InputStream input, PrintStream output) {
        mInput = input;
        mOutput = output;
    }

    @Override
    public void run() {
        InputStreamReader r;
        try {
            r = new InputStreamReader(mInput, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(mOutput);
            return;
        }

        BufferedReader br = new BufferedReader(r);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                mOutput.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace(mOutput);
        }
    }
}
