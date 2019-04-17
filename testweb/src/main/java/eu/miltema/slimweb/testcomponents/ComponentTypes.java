package eu.miltema.slimweb.testcomponents;

import java.time.*;

import eu.miltema.slimweb.annot.*;

@Component(requireSession = false)
public class ComponentTypes {

	String name;
	LocalDate date;
	LocalTime time;
	LocalDateTime datetime;

	public ComponentTypes get() {
		if (date != null) date = date.plusDays(1);
		if (time != null) time = time.plusMinutes(5);
		if (datetime != null) datetime = datetime.plusMinutes(5);
		return this;
	}
}
