package org.apache.flume.interceptor;

/**
 * Created by cto on 9/24/15.
 */

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flume.interceptor.EventFolderNameInterceptor.Constants.*;

/**
 *
 * Simple Interceptor class that extract the last folder name from the given fileHeaderKey .
 * and set it to the given target folder key
 * @author Eran Witkon (eranwitkon@gmail.com)
 *
 */

public class EventFolderNameInterceptor implements Interceptor {
  private static final Logger logger = LoggerFactory.getLogger(EventFolderNameInterceptor.class);
  private final String folderNameKey;
  private final String fileHeaderKey;

  // private constructor - initilized only by the builder
  private EventFolderNameInterceptor(String fileHeaderKey,String targetFolderName) {
    this.fileHeaderKey = fileHeaderKey;
    this.folderNameKey = targetFolderName;
  }

  public void initialize() {
    // no-op
  }

  public Event intercept(Event event) {
    Map<String, String> headers = event.getHeaders();
    headers.put(folderNameKey,FOLDERNAME_DFLT);
    if (headers.containsKey(fileHeaderKey)) {
      // extract the last folder name from the given key
      File file = new File(headers.get(fileHeaderKey));
      String parent = file.getParent();
      if (parent != null) {
        String dir = parent.substring(parent.lastIndexOf(File.separator) + 1);
        if (!dir.isEmpty()) {
          headers.put(folderNameKey, dir);
        }
      }
    }
    return event;
  }

  /**
   * Delegates to {@link #intercept(Event)} in a loop.
   *
   * @param events
   * @return
   */
  public List<Event> intercept(List<Event> events) {
    for (Event event : events) {
      intercept(event);
    }
    return events;
  }

  public void close() {
    // no-op
  }

  /**
   * Builder which builds new instances of the EventFolderNameInterceptor.
   */
  public static class Builder implements Interceptor.Builder {

    private String folderNameKey = FOLDERNAMEKEY_DFLT;
    private String fileHeaderKey = FILEHEADERKEY_DFLT;

    public Interceptor build() {
      return new EventFolderNameInterceptor(fileHeaderKey, folderNameKey);
    }

    public void configure(Context context) {
      folderNameKey = context.getString(FOLDERNAMEKEY, FOLDERNAMEKEY_DFLT);
      fileHeaderKey = context.getString(FILEHEADERKEY, FILEHEADERKEY_DFLT);
    }
  }

  public static class Constants {
    // this is the given path name key
    public static String FILEHEADERKEY = "file";
    // this is the default path name key
    public static String FILEHEADERKEY_DFLT = "file";
    // this is the folder key name
    public static String FOLDERNAMEKEY = "targetFolderKey";
    // this is the default folder key
    public static String FOLDERNAMEKEY_DFLT = "defaultFolderKey";
    // this is the name of the folder that will be returned  if not found
    public static String FOLDERNAME_DFLT = "dfltFolder";
  }

}
