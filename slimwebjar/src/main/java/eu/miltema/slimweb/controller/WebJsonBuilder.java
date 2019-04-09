package eu.miltema.slimweb.controller;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

import com.google.gson.*;
import com.google.gson.stream.*;

public class WebJsonBuilder {

	class LocalDateAdapter extends TypeAdapter<LocalDate> {
		@Override
		public LocalDate read(JsonReader jr) throws IOException {
			if (jr.peek() == JsonToken.NULL) {
				jr.nextNull();
				return null;
			}
			return LocalDate.parse(jr.nextString(), DateTimeFormatter.ISO_DATE);
		}
		@Override
		public void write(JsonWriter jw, LocalDate date) throws IOException {
			jw.jsonValue(date == null ? null : '"' + date.format(DateTimeFormatter.ISO_DATE) + '"');
		}
	}

	class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
		@Override
		public LocalDateTime read(JsonReader jr) throws IOException {
			if (jr.peek() == JsonToken.NULL) {
				jr.nextNull();
				return null;
			}
			return LocalDateTime.parse(jr.nextString(), DateTimeFormatter.ISO_DATE_TIME);
		}
		@Override
		public void write(JsonWriter jw, LocalDateTime datetime) throws IOException {
			jw.jsonValue(datetime == null ? null : '"' + datetime.format(DateTimeFormatter.ISO_DATE_TIME) + '"');
		}
	}

	public Gson build() {
		return new GsonBuilder().
				serializeNulls().
				setLenient().
				registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).
				registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
				create();
	}
}
