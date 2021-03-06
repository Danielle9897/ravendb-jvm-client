package net.ravendb.client.documents.commands.batches;

import com.fasterxml.jackson.core.JsonGenerator;
import net.ravendb.client.documents.conventions.DocumentConventions;
import net.ravendb.client.exceptions.RavenException;
import net.ravendb.client.http.RavenCommand;
import net.ravendb.client.http.ServerNode;
import net.ravendb.client.json.ContentProviderHttpEntity;
import net.ravendb.client.json.JsonArrayResult;
import net.ravendb.client.primitives.CleanCloseable;
import net.ravendb.client.primitives.Reference;
import net.ravendb.client.util.TimeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BatchCommand extends RavenCommand<JsonArrayResult> implements CleanCloseable {

    private final DocumentConventions _conventions;
    private final List<ICommandData> _commands;
    private Set<InputStream> _attachmentStreams;
    private final BatchOptions _options;

    public BatchCommand(DocumentConventions conventions, List<ICommandData> commands) {
        this(conventions, commands, null);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public BatchCommand(DocumentConventions conventions, List<ICommandData> commands, BatchOptions options) {
        super(JsonArrayResult.class);
        this._commands = commands;
        this._options = options;
        this._conventions = conventions;

        if (conventions == null) {
            throw new IllegalArgumentException("conventions cannot be null");
        }

        if (commands == null) {
            throw new IllegalArgumentException("commands cannot be null");
        }

        for (int i = 0; i < commands.size(); i++) {
            ICommandData command = commands.get(i);

            if (command instanceof PutAttachmentCommandData) {
                PutAttachmentCommandData putAttachmentCommandData = (PutAttachmentCommandData) command;

                if (_attachmentStreams == null) {
                    _attachmentStreams = new LinkedHashSet<>();
                }

                InputStream stream = putAttachmentCommandData.getStream();
                if (!_attachmentStreams.add(stream)) {
                    PutAttachmentCommandHelper.throwStreamAlready();
                }
            }

        }
    }

    @Override
    public HttpRequestBase createRequest(ServerNode node, Reference<String> url) {
        HttpPost request = new HttpPost();

        request.setEntity(new ContentProviderHttpEntity(outputStream -> {
            try (JsonGenerator generator = mapper.getFactory().createGenerator(outputStream)) {

                generator.writeStartObject();
                generator.writeFieldName("Commands");
                generator.writeStartArray();

                for (ICommandData command : _commands) {
                    command.serialize(generator, _conventions);
                }

                generator.writeEndArray();
                generator.writeEndObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, ContentType.APPLICATION_JSON));


        if (_attachmentStreams != null && _attachmentStreams.size() > 0) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

            HttpEntity entity = request.getEntity();

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                entity.writeTo(baos);

                entityBuilder.addBinaryBody("main", new ByteArrayInputStream(baos.toByteArray()));
            } catch (IOException e) {
                throw new RavenException("Unable to serialize BatchCommand", e);
            }

            int nameCounter = 1;

            for (InputStream stream : _attachmentStreams) {
                InputStreamBody inputStreamBody = new InputStreamBody(stream, (String) null);
                FormBodyPart part = FormBodyPartBuilder.create("attachment" + nameCounter++, inputStreamBody)
                        .addField("Command-Type", "AttachmentStream")
                        .build();
                entityBuilder.addPart(part);
            }
            request.setEntity(entityBuilder.build());
        }

        StringBuilder sb = new StringBuilder(node.getUrl() + "/databases/" + node.getDatabase() + "/bulk_docs");
        appendOptions(sb);

        url.value = sb.toString();
        return request;
    }

    @Override
    public void setResponse(String response, boolean fromCache) throws IOException {
        if (response == null) {
            throw new IllegalStateException("Got null response from the server after doing a batch, something is very wrong. Probably a garbled response.");
        }

        result = mapper.readValue(response, JsonArrayResult.class);
    }

    private void appendOptions(StringBuilder sb) {
        if (_options == null) {
            return;
        }

        sb.append("?");

        if (_options.isWaitForReplicas()) {
            sb.append("&waitForReplicasTimeout=")
                    .append(TimeUtils.durationToTimeSpan(_options.getWaitForReplicasTimeout()));

            if (_options.isThrowOnTimeoutInWaitForReplicas()) {
                sb.append("&throwOnTimeoutInWaitForReplicas=true");
            }

            sb.append("&numberOfReplicasToWaitFor=");
            sb.append(_options.isMajority() ? "majority" : _options.getNumberOfReplicasToWaitFor());
        }

        if (_options.isWaitForIndexes()) {
            sb.append("&waitForIndexesTimeout=")
                    .append(TimeUtils.durationToTimeSpan(_options.getWaitForIndexesTimeout()));

            if (_options.isThrowOnTimeoutInWaitForIndexes()) {
                sb.append("&waitForIndexThrow=true");
            } else {
                sb.append("&waitForIndexThrow=false");
            }

            if (_options.getWaitForSpecificIndexes() != null) {
                for (String specificIndex : _options.getWaitForSpecificIndexes()) {
                    sb.append("&waitForSpecificIndex=").append(specificIndex);
                }
            }
        }
    }


    @Override
    public boolean isReadRequest() {
        return false;
    }

    @Override
    public void close() {
        // empty
    }
}
