package eu.miltema.slimweb.testcomponents;

import java.time.*;

import eu.miltema.slimweb.annot.*;

@Component
@SessionNotRequired
public class ComponentTypes {

	String name;
	LocalDate date;
	LocalDateTime datetime;

	public ComponentTypes get() {
		if (date != null) date = date.plusDays(1);
		if (datetime != null) datetime = datetime.plusMinutes(5);
		return this;
	}
}
