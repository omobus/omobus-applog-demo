/* This file is a part of the omobus-applog-demo project.
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

package com.omobus.demo.applog;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.omobus.AppLogManager;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private AppLogManager appLog = null;
    private UUID cookie = null;
    private long xxx = 0;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                try {
                    if( !appLog.isBound() ) {
                        Toast.makeText(getApplicationContext(), "OMOBUS AppLogService doesn't bound yet.",
                            Toast.LENGTH_LONG).show();
                        appLog.bindService();
                    } else if( cookie == null ) {
                        cookie = UUID.randomUUID();
                        appLog.post("task1", "begin", cookie, null);
                        xxx = System.currentTimeMillis();
                        ((Button) v).setText(" >>> STOP <<< ");
                    } else {
                        appLog.post("task1", "end", cookie,
                            String.format("L = %1$.2f secs", (System.currentTimeMillis()-xxx)/1000.0));
                        cookie = null;
                        ((Button)v).setText(" >>> START <<< ");
                    }
                } catch( Exception ex ) {
                    ex.printStackTrace();
                }
            }
        });
        appLog = AppLogManager.createInstance(this);
    }

    @Override protected void onStart() {
        super.onStart();
        if( ContextCompat.checkSelfPermission(this, "omobus.permission.WRITE_ACCESS")
                != PackageManager.PERMISSION_GRANTED) {
            //if( ActivityCompat.shouldShowRequestPermissionRationale(this,
            //"omobus.permission.WRITE_ACCESS")) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            //    Toast.makeText(getApplicationContext(), "[omobus.permission.WRITE_ACCESS] permission needed.",
            //        Toast.LENGTH_LONG).show();
            //} else {
                ActivityCompat.requestPermissions(this,
                    new String[]{"omobus.permission.WRITE_ACCESS"},
                    0);
            //}
        } else {
            // Bind to the service
            appLog.bindService();
        }
    }

    @Override protected void onStop() {
        super.onStop();
        // Unbind from the service
        appLog.unbindService();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted.
            appLog.bindService();
        } else {
            // permission denied, disable the functionality that depends on this permission.
            finish();
        }
    }
}
