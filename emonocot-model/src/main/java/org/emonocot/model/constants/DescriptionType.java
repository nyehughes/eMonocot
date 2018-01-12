/*
 * This is eMonocot, a global online biodiversity information resource.
 *
 * Copyright © 2011–2015 The Board of Trustees of the Royal Botanic Gardens, Kew and The University of Oxford
 *
 * eMonocot is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * eMonocot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * The complete text of the GNU Affero General Public License is in the source repository as the file
 * ‘COPYING’.  It is also available from <http://www.gnu.org/licenses/>.
 */
package org.emonocot.model.constants;

/**
 *
 * @author ben
 *
 */
public enum DescriptionType {
	/**
	 *
	 */
	general("http://rs.gbif.org/vocabulary/gbif/descriptionType/general",
			"general"),

			/**
			 *
			 */
			diagnostic("http://rs.gbif.org/vocabulary/gbif/descriptionType/diagnostic",
					"diagnostic"),

					/**
					 *
					 */
					morphology("http://rs.gbif.org/vocabulary/gbif/descriptionType/morphology",
							"morphology"),
							/**
							 *
							 */
							habit("http://rs.gbif.org/vocabulary/gbif/descriptionType/habit", "habit"),
							/**
							 *
							 */
							cytology("http://rs.gbif.org/vocabulary/gbif/descriptionType/cytology",
									"cytology"),
									/**
									 *
									 */
									physiology("http://rs.gbif.org/vocabulary/gbif/descriptionType/physiology",
											"physiology"),
											/**
											 *
											 */
											size("http://rs.gbif.org/vocabulary/gbif/descriptionType/size", "size"),

											/**
											 *
											 */
											weight("http://rs.gbif.org/vocabulary/gbif/descriptionType/weight",
													"weight"),

													/**
													 *
													 */
													lifespan("http://rs.gbif.org/vocabulary/gbif/descriptionType/lifespan",
															"lifespan"),

															/**
															 *
															 */
															lifetime("http://rs.gbif.org/vocabulary/gbif/descriptionType/lifetime",
																	"lifetime"),
																	/**
																	 *
																	 */
																	biology("http://rs.gbif.org/vocabulary/gbif/descriptionType/biology", "biology"),

																	/**
																	 *
																	 */
																	ecology("http://rs.gbif.org/vocabulary/gbif/descriptionType/ecology",
																			"ecology"),

																			/**
																			 *
																			 */
																			habitat("http://rs.gbif.org/vocabulary/gbif/descriptionType/habitat",
																					"habitat"),

																					/**
																					 *
																					 */
																					distribution("http://rs.gbif.org/vocabulary/gbif/descriptionType/distribution",
																							"distribution"),
																							/**
																							 *
																							 */
																							reproduction("http://rs.gbif.org/vocabulary/gbif/descriptionType/reproduction",
																									"reproduction"),

																									/**
																									 *
																									 */
																									conservation("http://rs.gbif.org/vocabulary/gbif/descriptionType/conservation",
																											"conservation"),

																											/**
																											 *
																											 */
																											use("http://rs.gbif.org/vocabulary/gbif/descriptionType/use",
																													"use"),

																													/**
																													 *
																													 */
																													dispersal(
																															"http://rs.gbif.org/vocabulary/gbif/descriptionType/dispersal",
																															"dispersal"),
																															/**
																															 *
																															 */
																															cyclicity("http://rs.gbif.org/vocabulary/gbif/descriptionType/cyclicity",
																																	"cyclicity"),

																																	/**
																																	 *
																																	 */
																																	lifecycle("http://rs.gbif.org/vocabulary/gbif/descriptionType/lifecycle",
																																			"lifecycle"),

																																			/**
																																			 *
																																			 */
																																			migration("http://rs.gbif.org/vocabulary/gbif/descriptionType/migration",
																																					"migration"),

																																					/**
																																					 *
																																					 */
																																					growth(
																																							"http://rs.gbif.org/vocabulary/gbif/descriptionType/growth",
																																							"growth"),
																																							/**
																																							 *
																																							 */
																																							genetics("http://rs.gbif.org/vocabulary/gbif/descriptionType/genetics",
																																									"genetics"),

																																									/**
																																									 *
																																									 */
																																									chemistry("http://rs.gbif.org/vocabulary/gbif/descriptionType/chemistry",
																																											"chemistry"),

																																											/**
																																											 *
																																											 */
																																											diseases("http://rs.gbif.org/vocabulary/gbif/descriptionType/diseases",
																																													"diseases"),

																																													/**
																																													 *
																																													 */
																																													associations("http://rs.gbif.org/vocabulary/gbif/descriptionType/associations",
																																															"associations"),
																																															/**
																																															 *
																																															 */
																																															behaviour("http://rs.gbif.org/vocabulary/gbif/descriptionType/behaviour",
																																																	"behaviour"),

																																																	/**
																																																	 *
																																																	 */
																																																	population("http://rs.gbif.org/vocabulary/gbif/descriptionType/population",
																																																			"population"),

																																																			/**
																																																			 *
																																																			 */
																																																			management("http://rs.gbif.org/vocabulary/gbif/descriptionType/management",
																																																					"management"),

																																																					/**
																																																					 *
																																																					 */
																																																					legislation(
																																																							"http://rs.gbif.org/vocabulary/gbif/descriptionType/legislation",
																																																							"legislation"),
																																																							/**
																																																							 *
																																																							 */
																																																							threats("http://rs.gbif.org/vocabulary/gbif/descriptionType/threats",
																																																									"threats"),

																																																									/**
																																																									 *
																																																									 */
																																																									typematerial("http://rs.gbif.org/vocabulary/gbif/descriptionType/typematerial",
																																																											"typematerial"),

																																																											/**
																																																											 *
																																																											 */
																																																											typelocality("http://rs.gbif.org/vocabulary/gbif/descriptionType/typelocality",
																																																													"typelocality"),

																																																													/**
																																																													 *
																																																													 */
																																																													phylogeny(
																																																															"http://rs.gbif.org/vocabulary/gbif/descriptionType/phylogeny",
																																																															"phylogeny"),
																																																															/**
																																																															 *
																																																															 */
																																																															hybrids("http://rs.gbif.org/vocabulary/gbif/descriptionType/hybrids",
																																																																	"hybrids"),

																																																																	/**
																																																																	 *
																																																																	 */
																																																																	literature(
																																																																			"http://rs.gbif.org/vocabulary/gbif/descriptionType/literature",
																																																																			"literature"),

																																																																			/**
																																																																			 *
																																																																			 */
																																																																			culture(
																																																																					"http://rs.gbif.org/vocabulary/gbif/descriptionType/culture",
																																																																					"culture"),

																																																																					/**
																																																																					 *
																																																																					 */
																																																																					vernacular("http://rs.gbif.org/vocabulary/gbif/descriptionType/vernacular",
																																																																							"vernacular");
	/**
	 *
	 */
	private String uri;

	/**
	 *
	 */
	private String term;

	/**
	 *
	 * @param newUri The uri of this feature
	 * @param newTerm The short name of this feature
	 */
	private DescriptionType(final String newUri, String newTerm) {
		this.uri = newUri;
		this.term = newTerm;
	}

	/**
	 *
	 * @param uri The uri being converted into a Feature
	 * @return the matching feature or throw an IllegalArgumentException if no
	 *         feature matches
	 */
	public static DescriptionType fromString(final String string) {
		for (DescriptionType f : DescriptionType.values()) {
			if (f.uri.equals(string) || f.term.equals(string)) {
				return f;
			}
		}
		throw new IllegalArgumentException(string
				+ " is not an acceptable value for Feature");
	}

}
