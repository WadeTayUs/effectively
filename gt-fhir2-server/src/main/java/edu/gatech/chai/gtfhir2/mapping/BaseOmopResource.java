package edu.gatech.chai.gtfhir2.mapping;

import java.util.List;
import java.util.Map;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.web.context.WebApplicationContext;

import edu.gatech.chai.omopv5.jpa.entity.BaseEntity;
import edu.gatech.chai.omopv5.jpa.service.IService;
import edu.gatech.chai.omopv5.jpa.service.ParameterWrapper;

public abstract class BaseOmopResource<v extends DomainResource, t extends BaseEntity, p extends IService<t>> implements IResourceMapping<v, t> {
	
	private p myOmopService;
	private Class<t> myEntityClass;
	private String myFhirResourceType;
	
	public static String MAP_EXCEPTION_FILTER = "FILTER";
	public static String MAP_EXCEPTION_EXCLUDE = "EXCLUDE";
	
	public BaseOmopResource(WebApplicationContext context, Class<t> entityClass, Class<p> serviceClass, String fhirResourceType) {
		myOmopService = context.getBean(serviceClass);
		myEntityClass = entityClass;
		myFhirResourceType = fhirResourceType;
	}
	
	public String getMyFhirResourceType() {
		return this.myFhirResourceType;
	}
	
	public p getMyOmopService() {
		return this.myOmopService;
	}
	
	public Class<t> getMyEntityClass() {
		return this.myEntityClass;
	}
	
	public Long getSize() {
		return myOmopService.getSize();
	}

	public Long getSize(Map<String, List<ParameterWrapper>> map) {
		return myOmopService.getSize(map);
	}
	
	public v constructResource(Long fhirId, t entity, List<String> includes) {
		v fhirResource = constructFHIR(fhirId, entity);
		
		return fhirResource;
	}

	// This needs to be overridden at every OMOP[x] class.
	public v constructFHIR(Long fhirId, t entity) {
		return null;
	}
	
	/***
	 * 
	 */
	public v toFHIR(IdType id) {
		Long id_long_part = id.getIdPartAsLong();
		Long myId = IdMapping.getOMOPfromFHIR(id_long_part, getMyFhirResourceType());

		t entityClass = (t) getMyOmopService().findById(myId);
		if (entityClass == null)
			return null;

		Long fhirId = IdMapping.getFHIRfromOMOP(myId, getMyFhirResourceType());

		return constructFHIR(fhirId, entityClass);
	}

	public void searchWithoutParams(int fromIndex, int toIndex, List<IBaseResource> listResources,
			List<String> includes) {
		List<t> entities = myOmopService.searchWithoutParams(fromIndex, toIndex);

		// We got the results back from OMOP database. Now, we need to construct
		// the list of
		// FHIR Patient resources to be included in the bundle.
		for (t entity : entities) {
			Long omopId = entity.getIdAsLong();
			Long fhirId = IdMapping.getFHIRfromOMOP(omopId, getMyFhirResourceType());
			v fhirResource = constructResource(fhirId, entity, includes);
			if (fhirResource != null) {
				listResources.add(fhirResource);			
				addRevIncludes(omopId, includes, listResources);
			}
		}		
	}

	public void searchWithParams(int fromIndex, int toIndex, Map<String, List<ParameterWrapper>> map,
			List<IBaseResource> listResources, List<String> includes) {
		List<t> entities = getMyOmopService().searchWithParams(fromIndex, toIndex, map);

		for (t entity : entities) {
			Long omopId = entity.getIdAsLong();
			Long fhirId = IdMapping.getFHIRfromOMOP(omopId, getMyFhirResourceType());
			v fhirResource = constructResource(fhirId, entity, includes);
			if (fhirResource != null) {
				listResources.add(fhirResource);
				// Do the rev_include and add the resource to the list.
				addRevIncludes(omopId, includes, listResources);
			}
		}
	}

	// Override the this method to provide rev_includes. 
	public void addRevIncludes(Long omopId, List<String> includes, List<IBaseResource> listResources) {
	
	}

}
