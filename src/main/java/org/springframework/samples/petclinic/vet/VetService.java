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
package org.springframework.samples.petclinic.vet;

import java.time.LocalDate;

import org.springframework.samples.petclinic.owner.VisitRepository;
import org.springframework.stereotype.Service;

/**
 * Holds the workload rules for {@link Vet}s that don't belong on the {@link Vet} entity
 * itself.
 *
 * @author Anatoliy Milovsky
 */
@Service
public class VetService {

	private static final int OVERLOADED_VISIT_THRESHOLD = 5;

	private static final int OVERLOADED_WINDOW_DAYS = 7;

	private final VisitRepository visits;

	public VetService(VisitRepository visits) {
		this.visits = visits;
	}

	/**
	 * A vet is overloaded if they have handled more than
	 * {@value #OVERLOADED_VISIT_THRESHOLD} visits in the last
	 * {@value #OVERLOADED_WINDOW_DAYS} days.
	 * @param vet the vet to check
	 * @return {@code true} if the vet's recent visit count exceeds the threshold
	 */
	public boolean isOverloaded(Vet vet) {
		return countRecentVisits(vet) >= OVERLOADED_VISIT_THRESHOLD;
	}

	/**
	 * Evaluates the workload rules for the given vet.
	 * @param vet the vet to evaluate
	 * @return a {@link Workload} describing the vet's recent visit count and whether they
	 * are overloaded
	 */
	public Workload getWorkload(Vet vet) {
		long recentVisits = countRecentVisits(vet);

		Workload workload = new Workload();
		workload.setVisitCount(recentVisits);
		workload.setOverloaded(isOverloaded(vet));
		return workload;
	}

	private long countRecentVisits(Vet vet) {
		LocalDate windowStart = LocalDate.now().minusDays(OVERLOADED_WINDOW_DAYS);
		return visits.countByVetIdAndDateGreaterThanEqual(vet.getId(), windowStart);
	}

}
