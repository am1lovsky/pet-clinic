/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

/**
 * Holds the booking rules for {@link Visit}s that don't belong on the {@link Visit}
 * entity itself.
 *
 * @author Anatoliy Milovsky
 */
@Service
public class VisitService {

	/**
	 * A visit must be booked for a future date; today or earlier is not allowed.
	 * @param date the date to check, may be {@code null}
	 * @return {@code true} if the date is not after today
	 */
	public boolean isPastDate(LocalDate date) {
		return date != null && !date.isAfter(LocalDate.now());
	}

	/**
	 * A pet cannot have more than one visit booked on the same day.
	 * @param pet the pet the visit is being booked for
	 * @param visit the visit being validated; excluded from the comparison so a visit
	 * does not conflict with itself
	 * @return {@code true} if the pet already has another visit on the same date
	 */
	public boolean hasVisitOnSameDay(Pet pet, Visit visit) {
		LocalDate date = visit.getDate();
		return pet.getVisits()
			.stream()
			.filter(existingVisit -> existingVisit != visit)
			.anyMatch(existingVisit -> date.equals(existingVisit.getDate()));
	}

}
