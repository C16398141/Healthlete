package fyp.c16398141.healthlete;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class post_notification extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        Log.i("HERE","CALLED");
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra("notification");
        Integer notificationid = (int) SystemClock.uptimeMillis();
        notificationManager.notify(notificationid, notification);
    }
}