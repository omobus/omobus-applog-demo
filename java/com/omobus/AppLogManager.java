/* This file is a part of the omobus project.
 * Copyright (c) 2006 - 2018 ak-obs, Ltd. <info@omobus.net>.
 * All rights reserved.
 *
 * This program is a free software. Redistribution and use in source
 * and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The origin of this software must not be misrepresented; you must
 *    not claim that you wrote the original software.
 *
 * 3. Altered source versions must be plainly marked as such, and must
 *    not be misrepresented as being the original software.
 *
 * 4. The name of the author may not be used to endorse or promote
 *    products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.omobus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.UUID;

/* @formatter:off */

public class AppLogManager {
    private final Context context;
    private final String applicationName, packageName;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName className, IBinder service) {
            serviceMessanger = new Messenger(service);
            isBound = true;
        }
        @Override public void onServiceDisconnected(ComponentName className) {
            serviceMessanger = null;
            isBound = false;
        }
    };
    private boolean isBound = false;
    private Messenger serviceMessanger = null;

    private AppLogManager(Context context, String packageName, String applicationName) {
        this.context = context;
        this.packageName = packageName;
        this.applicationName = applicationName;
    }

    public boolean bindService() {
        if( isBound ) {
            return true;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.omobus.mobile","com.omobus.mobile.AppLogService"));
        intent.putExtra("packageName", packageName);
        intent.putExtra("applicationName", applicationName);
        return context.bindService(intent, serviceConnection, /*Context.BIND_EXTERNAL_SERVICE*/Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        if( isBound ) {
            context.unbindService(serviceConnection);
            isBound = false;
        }
    }

    public boolean isBound() {
        return isBound;
    }

    public void post(String facility, String state, UUID cookie, String extra) throws NotYetBoundException {
        if( !isBound )  {
            throw new NotYetBoundException("AppLogListener not yet bound");
        }
        if( facility == null || facility.isEmpty() ) {
            return;
        }
        final Bundle b = new Bundle();
        final Message msg = Message.obtain(null, 0, 0, 0);
        b.putString("packageName", packageName);
        b.putString("applicationName", applicationName);
        b.putString("facility", facility);
        if( state != null && !state.isEmpty() ) { b.putString("state", state); }
        if( cookie != null ) { b.putString("cookie", cookie.toString().replaceAll("-", "")); }
        if( extra != null && !extra.isEmpty() ) { b.putString("extra", extra); }
        try {
            if( msg != null ) {
                msg.setData(b);
                serviceMessanger.send(msg);
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public static AppLogManager createInstance(Context context) {
        final ApplicationInfo ai = context.getApplicationInfo();
        final PackageManager pm = context.getPackageManager();
        String packageVersion = null;
        try {
            packageVersion = pm.getPackageInfo(ai.packageName, 0).versionName;
        } catch( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
        return new AppLogManager(context,
            packageVersion != null ? String.format("%1$s#%2$s", ai.packageName, packageVersion) : ai.packageName,
            pm.getApplicationLabel(ai).toString());
    }

    public static class NotYetBoundException extends Exception {
        NotYetBoundException(String e) {
            super(e);
        }
    }
}
