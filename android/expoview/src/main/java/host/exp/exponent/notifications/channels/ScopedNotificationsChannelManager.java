package host.exp.exponent.notifications.channels;

import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;

import org.unimodules.core.arguments.ReadableArguments;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import expo.modules.notifications.notifications.channels.managers.AndroidXNotificationsChannelManager;
import expo.modules.notifications.notifications.channels.managers.NotificationsChannelGroupManager;
import host.exp.exponent.kernel.ExperienceId;

public class ScopedNotificationsChannelManager extends AndroidXNotificationsChannelManager {

  private ExperienceId mExperienceId;

  public ScopedNotificationsChannelManager(Context context, ExperienceId experienceId, NotificationsChannelGroupManager groupManager) {
    super(context, groupManager);
    mExperienceId = experienceId;
  }

  @Nullable
  @Override
  @RequiresApi(api = Build.VERSION_CODES.O)
  public NotificationChannel getNotificationChannel(@NonNull String channelId) {
    NotificationChannel scopedChannel = super.getNotificationChannel(ScopedNotificationsChannelUtils.getScopedChannelId(mExperienceId, channelId));
    if (scopedChannel != null) {
      return scopedChannel;
    }

    // In SDK 38 channels weren't scoped, so we want to return unscoped channel if the scoped one wasn't found.
    return super.getNotificationChannel(channelId);
  }

  @NonNull
  @Override
  @RequiresApi(api = Build.VERSION_CODES.O)
  public List<NotificationChannel> getNotificationChannels() {
    ArrayList<NotificationChannel> result = new ArrayList<>();
    List<NotificationChannel> notificationChannels = super.getNotificationChannels();
    for (NotificationChannel channel : notificationChannels) {
      if (ScopedNotificationsChannelUtils.checkIfChannelBelongsToExperience(mExperienceId, channel)) {
        result.add(channel);
      }
    }

    return result;
  }

  @Override
  @RequiresApi(api = Build.VERSION_CODES.O)
  public void deleteNotificationChannel(@NonNull String channelId) {
    NotificationChannel channelToRemove = getNotificationChannel(channelId);
    if (channelToRemove != null) {
      super.deleteNotificationChannel(channelToRemove.getId());
    }
  }

  @Override
  @RequiresApi(api = Build.VERSION_CODES.O)
  public NotificationChannel createNotificationChannel(@NonNull String channelId, CharSequence name, int importance, ReadableArguments channelOptions) {
    return super.createNotificationChannel(ScopedNotificationsChannelUtils.getScopedChannelId(mExperienceId, channelId), name, importance, channelOptions);
  }
}
