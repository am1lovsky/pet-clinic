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
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Holds the booking rules for {@link Visit}s that don't belong on the {@link Visit}
 * entity itself.
 *
 * @author Anatoliy Milovsky
 */
@Service
public class VisitService {

	private static final int FREQUENT_VISITOR_THRESHOLD = 3;

	private static final int SENIOR_PET_AGE_YEARS = 8;

	private static final String EMERGENCY_KEYWORD = "emergency";

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
		return existsOtherVisitOnSameDate(pet, visit);
	}

	/**
	 * Checks whether the pet already has a visit on the same date as the given visit,
	 * ignoring the given visit itself so it does not conflict with itself.
	 * @param pet the pet the visit is being booked for
	 * @param visit the visit being validated
	 * @return {@code true} if another visit exists for the pet on the same date
	 */
	private boolean existsOtherVisitOnSameDate(Pet pet, Visit visit) {
		LocalDate date = visit.getDate();
		return pet.getVisits()
			.stream()
			.filter(existingVisit -> existingVisit != visit)
			.anyMatch(existingVisit -> date.equals(existingVisit.getDate()));
	}

	private Optional<LocalDate> lastVisitDate(Pet pet) {
		return pet.getVisits().stream().map(Visit::getDate).max(LocalDate::compareTo);
	}

	public boolean isNeedVisit(Pet pet) {
		LocalDate yearAgo = LocalDate.now().minusYears(1);
		return lastVisitDate(pet).map(date -> date.isBefore(yearAgo)).orElse(true);
	}

	/**
	 * A pet is a frequent visitor if it has had 3 or more visits in the last 12 months.
	 * @param pet the pet to check
	 * @return {@code true} if the pet has 3 or more visits within the last year
	 */
	public boolean isFrequentVisitor(Pet pet) {
		LocalDate yearAgo = LocalDate.now().minusYears(1);
		return pet.getVisits()
			.stream()
			.map(Visit::getDate)
			.filter(date -> !date.isBefore(yearAgo))
			.count() >= FREQUENT_VISITOR_THRESHOLD;
	}

	/**
	 * A pet is considered senior once it is older than {@value #SENIOR_PET_AGE_YEARS}
	 * years.
	 * @param pet the pet to check
	 * @return {@code true} if the pet's age exceeds the senior threshold
	 */
	public boolean isSenior(Pet pet) {
		LocalDate birthDate = pet.getBirthDate();
		return birthDate != null && Period.between(birthDate, LocalDate.now()).getYears() > SENIOR_PET_AGE_YEARS;
	}

	/**
	 * A pet has an emergency history if any of its visit descriptions mention
	 * "{@value #EMERGENCY_KEYWORD}".
	 * @param pet the pet to check
	 * @return {@code true} if at least one visit description contains the emergency
	 * keyword
	 */
	public boolean hasEmergencyHistory(Pet pet) {
		return pet.getVisits()
			.stream()
			.map(Visit::getDescription)
			.filter(Objects::nonNull)
			.anyMatch(description -> description.toLowerCase().contains(EMERGENCY_KEYWORD));
	}

	/**
	 * Evaluates all priority rules for the given pet and collects the reasons for every
	 * rule that matched.
	 * @param pet the pet to evaluate
	 * @return a {@link PetPriority} describing whether the pet is a priority patient and
	 * why
	 */
	public PetPriority getPriority(Pet pet) {
		List<String> reasons = new ArrayList<>();

		if (isFrequentVisitor(pet)) {
			reasons.add("The pet has had 3 or more visits in the last 12 months (frequent client)");
		}
		if (isSenior(pet)) {
			reasons.add("The pet is older than 8 years (senior pet, needs more attentive care)");
		}
		if (hasEmergencyHistory(pet)) {
			reasons.add(
					"The pet has a visit with a description containing \"emergency\" (had an emergency case in its history)");
		}

		return new PetPriority(!reasons.isEmpty(), reasons);
	}

}
