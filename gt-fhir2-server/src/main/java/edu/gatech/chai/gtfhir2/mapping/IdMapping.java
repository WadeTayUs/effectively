package edu.gatech.chai.gtfhir2.mapping;

/**
 * ID Mapping Class to manage the IDs between FHIR and OMOP.
 * 
 * @author mc142
 *
 */
public class IdMapping {
	
	public static Long getFHIRfromOMOP(Long omop_id, String omop_table_name) {
		// We use the same ID now.
		// TODO: Develop the ID mapping so that we do not reveal native
		//       OMOP ID.
		
		return omop_id;
	}

	/**
	 * What is OMOP ID for the long part of FHIR ID
	 * @param fhir_id
	 * @return
	 */
	public static Long getOMOPfromFHIR(Long fhir_id, String resource_name) {
		// We use the same ID now.
		// TODO: Develop the ID mapping so that we do not reveal native
		//       OMOP ID.
		
		return fhir_id;
	}
	
	public static void writeOMOPfromFHIR(Long fhir_id) {
		// Placeholder for later to use to store OMOP ID mapping info.
		// This information will be used by getFHIRfromOMOP
		// TODO: Develop mapping creation here.
	}
}
