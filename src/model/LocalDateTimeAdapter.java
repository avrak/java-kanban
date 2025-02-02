package model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

     @Override
     public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
         if (localDateTime == null) {
             jsonWriter.nullValue();
         } else {
             jsonWriter.value(localDateTime.format(Task.DATE_TIME_FORMATTER));
         }
     }

     @Override
     public LocalDateTime read(final JsonReader jsonReader) throws IOException {
         String value = jsonReader.nextString();

         if (value.isEmpty()) return null;
         else return LocalDateTime.parse(value, Task.DATE_TIME_FORMATTER);
     }

}
