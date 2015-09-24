package org.apache.flume.interceptor;

import com.google.common.base.Charsets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;


/**
 * Created by cto on 9/24/15.
 */
public class EventFolderNameInterceptorTest {

  @Test
  public void testBasic() throws IOException {

    final String fileKey = "pathKey";
    final String folderKey = "testKey";

    Context context = new Context();
    context.put(EventFolderNameInterceptor.Constants.FILEHEADERKEY, fileKey);
    context.put(EventFolderNameInterceptor.Constants.FOLDERNAMEKEY, folderKey);

    Interceptor.Builder builder = new EventFolderNameInterceptor.Builder();
    builder.configure(context);
    Interceptor interceptor = builder.build();


    Event event = EventBuilder.withBody(
        "###[1979-07-21 00:00:00]|test event", Charsets.UTF_8);

    event.getHeaders().put(fileKey,"/home/cto/Downloads/flume-sample/flumeInterceptor/pom.xml");
    assertNull(folderKey + " does not exist", event.getHeaders().get(folderKey));
    event = interceptor.intercept(event);
    assertEquals(folderKey + " is set", event.getHeaders().get(folderKey),"flumeInterceptor" );

    event = EventBuilder.withBody(
        "###[1979-07-21 00:00:00]|test event", Charsets.UTF_8);

    event.getHeaders().put(fileKey,"/flumeInterceptor/pom.xml");
    assertNull(folderKey + " does not exist", event.getHeaders().get(folderKey));
    event = interceptor.intercept(event);
    assertEquals(folderKey + " is set", event.getHeaders().get(folderKey),"flumeInterceptor" );

  }

  @Test
  public void testNegative() throws IOException {
    //test no file key expect default
    final String fileKey = "pathKey";
    final String folderKey = "testKey";

    Context context = new Context();
    context.put(EventFolderNameInterceptor.Constants.FILEHEADERKEY, fileKey);
    context.put(EventFolderNameInterceptor.Constants.FOLDERNAMEKEY, folderKey);

    Interceptor.Builder builder = new EventFolderNameInterceptor.Builder();
    builder.configure(context);
    Interceptor interceptor = builder.build();


    Event event = EventBuilder.withBody(
        "###[1979-07-21 00:00:00]|test event", Charsets.UTF_8);
    assertNull(folderKey + " does not exist", event.getHeaders().get(folderKey));
    // Call intercept with no fileKey set
    event = interceptor.intercept(event);
    // expect default folder name
    assertEquals("folder name should be default",EventFolderNameInterceptor.Constants.FOLDERNAME_DFLT,event.getHeaders().get(folderKey));

    // Test as root expect default
    event = EventBuilder.withBody(
        "###[1979-07-21 00:00:00]|test event", Charsets.UTF_8);
    event.getHeaders().put(fileKey,"/pom.xml");
    assertNull(folderKey + " does not exist", event.getHeaders().get(folderKey));
    event = interceptor.intercept(event);
    assertEquals("folder name should be default",EventFolderNameInterceptor.Constants.FOLDERNAME_DFLT,event.getHeaders().get(folderKey));

  }


}
