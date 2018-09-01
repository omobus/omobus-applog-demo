# OMOBUS AppLogService client

Данный проект демонстрирует возможность организации процедуры передачи информации о 
выполнение пользователем каких либо операций (логирование) из сторонних приложений 
в мобильное приложение OMOBUS работающего под управлением операционной системы Android.

Для подключение данной возможности, в приложение необходимо внести следующие изменения:

1. В AndroidManifest.xml добавить права доступа на запись в мобильное приложение OMOBUS:

```xml
    <uses-permission android:name="omobus.permission.WRITE_ACCESS" />
```

2. Скопировать из данного проекта файл [AppLogListener.java](https://github.com/omobus/omobus-applog-demo/blob/master/java/com/omobus/AppLogManager.java)
разместив его в папке с исходными текстами приложения.

3. В функцию создания активности создать экземпляр AppLogListener:

```java
    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	/* private AppLogManager appLog = null; */
	appLog = AppLogManager.createInstance(this);
    }
```

4. В функцию запуска активности добавить вызов функции связывания с сервисом OMOBUS:

```java
    @Override protected void onStart() {
	super.onStart();
	// Bind to the service
        appLog.bindService();
    }
```

5. В функцию остановки активности добавить вызов функции отключения от сервиса OMOBUS:

```java
    @Override protected void onStop() {
	super.onStop();
        // Unbind from the service
        appLog.unbindService();
    }
```

6. При выполнение какого либо действия пользователя произвести его логирование в сервисе OMOBUS:

```java
    appLog.post(
	"task1", /* уникальное название объекта или операции */
	"start", /* состояние объекта или операции: start - начало операции, stop - завершение операции или любое другое название состояния */
	cookie, /* уникальная метка операции используемая для связывамния начала и окнчания (см. демонстрационный пример) */
	null /* дополнительна информация*/
    );
```

Пример реализации описанных выше действий приводится в [MainActivity.java](https://github.com/omobus/omobus-applog-demo/blob/master/java/com/omobus/demo/applog/MainActivity.java).


# COPYRIGHT

Copyright (c) 2006 - 2018 ak-obs, Ltd. <info@omobus.net>
All rights reserved.

This program is a free software. Redistribution and use in source
and binary forms, with or without modification, are permitted provided
that the following conditions are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. The origin of this software must not be misrepresented; you must
   not claim that you wrote the original software.

3. Altered source versions must be plainly marked as such, and must
   not be misrepresented as being the original software.

4. The name of the author may not be used to endorse or promote
   products derived from this software without specific prior written
   permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS
OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
