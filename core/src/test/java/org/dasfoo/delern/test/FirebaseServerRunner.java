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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FirebaseServerRunner {

    private Process mFirebaseServer;
    private List<String> mArgv = new LinkedList<>();

    public FirebaseServerRunner(final String node, final String server) {
        mArgv.add(node);
        mArgv.add(server);
    }

    public FirebaseServerRunner setPort(final String port) {
        mArgv.add("-p");
        mArgv.add(port);
        return this;
    }

    public FirebaseServerRunner setVerbose() {
        mArgv.add("-v");
        return this;
    }

    public FirebaseServerRunner setRules(final String rules) {
        mArgv.add("-r");
        mArgv.add(rules);
        return this;
    }

    public FirebaseServerRunner setHost(final String host) {
        mArgv.add("-n");
        mArgv.add(host);
        return this;
    }

    public FirebaseServerRunner start() throws IOException {
        mFirebaseServer = Runtime.getRuntime().exec(
                mArgv.toArray(new String[mArgv.size()]));

        new LineBufferedStreamCopier(mFirebaseServer.getInputStream(), System.out).start();
        new LineBufferedStreamCopier(mFirebaseServer.getErrorStream(), System.err).start();

        return this;
    }

    public void stop() throws IOException {
        if (mFirebaseServer == null) {
            return;
        }

        mFirebaseServer.destroy();

        Integer exitCode = null;
        while (exitCode == null) {
            try {
                exitCode = mFirebaseServer.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mFirebaseServer = null;

        if (exitCode != 143) {
            throw new IOException("firebase-server exited with code " + exitCode);
        }
    }
}
