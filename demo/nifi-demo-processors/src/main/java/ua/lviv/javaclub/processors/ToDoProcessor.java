
package ua.lviv.javaclub.processors;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Tags({"example", "javaclub", "nifi"})
@CapabilityDescription("custom processor")
public class ToDoProcessor extends AbstractProcessor {
//
//    private static final PropertyDescriptor CONNECTION_POOL = new PropertyDescriptor.Builder()
//            .name("JDBC Connection Pool")
//            .description("Specifies the JDBC Connection Pool to use in order to acquire the device data.")
//            .identifiesControllerService(DBCPService.class)
//            .required(true)
//            .build();

    private static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Success")
            .build();

    private static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("A FlowFile is routed to this relationship if it cannot be processed due to some issues. The issue will be addressed in the network.map.failure.reason attribute")
            .build();


    @Override
    public Set<Relationship> getRelationships() {
        return new HashSet<>(Arrays.asList(REL_SUCCESS, REL_FAILURE));
    }

//    @Override
//    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
//        return Collections.singletonList(CONNECTION_POOL);
//    }

//    @OnScheduled
//    public void onScheduled(final ProcessContext context) {
//
//    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        FlowFile flowFile = session.get();
        if ( flowFile == null ) {
            return;
        }
        JsonObject todo = mapMessage(flowFile, session);

        if (todo == null) {
            getLogger().error("Failed to process networkMap flow file: {};", new Object[]{flowFile});
            session.transfer(flowFile, REL_FAILURE);
            return;
        }
        session.transfer(createFlowFileWithPayload(getTodoQuery(todo), flowFile, session), REL_SUCCESS);
        session.remove(flowFile);
    }

    private static JsonObject mapMessage(FlowFile flowFile, ProcessSession session) {
        Gson gson = new Gson();
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        session.exportTo(flowFile, bytes);
        return gson.fromJson(bytes.toString(), JsonObject.class);
    }

    private static FlowFile createFlowFileWithPayload(String payload, FlowFile parent, ProcessSession session) {
        FlowFile flowFile = session.create(parent);
        session.write(flowFile, out -> out.write(payload.getBytes(StandardCharsets.UTF_8)));
        return flowFile;
    }

    private static String getTodoQuery(JsonObject data) {
        return String.format("INSERT INTO todos(userId, id, title, completed) VALUES('%s','%s', '%s', '%s');",
                data.get("userId").getAsString(),  data.get("id").getAsString(),
                data.get("title").getAsString(), data.get("completed").getAsBoolean() ? "YES" : "NO");
    }

}
