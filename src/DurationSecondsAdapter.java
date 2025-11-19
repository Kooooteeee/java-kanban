import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationSecondsAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getSeconds());
    }
    @Override
    public Duration read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return Duration.ofSeconds(in.nextLong());
    }
}
