package edu.gatech.chai.gtfhir2.provider;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import edu.gatech.chai.gtfhir2.mapping.OmopDevice;
import edu.gatech.chai.gtfhir2.model.MyDevice;
import edu.gatech.chai.omopv5.jpa.service.ParameterWrapper;

public class DeviceResourceProvider implements IResourceProvider {

	private WebApplicationContext myAppCtx;
	private OmopDevice myMapper;
	private String myDbType;
	private int preferredPageSize = 30;

	public DeviceResourceProvider() {
		myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		myDbType = myAppCtx.getServletContext().getInitParameter("backendDbType");
		if (myDbType.equalsIgnoreCase("omopv5") == true) {
			myMapper = new OmopDevice(myAppCtx);
		} else {
			myMapper = new OmopDevice(myAppCtx);
		}
		
		String pageSizeStr = myAppCtx.getServletContext().getInitParameter("preferredPageSize");
		if (pageSizeStr != null && pageSizeStr.isEmpty() == false) {
			int pageSize = Integer.parseInt(pageSizeStr);
			if (pageSize > 0) {
				preferredPageSize = pageSize;
			} 
		}
	}
	
	public static String getType() {
		return "Device";
	}
	
    public OmopDevice getMyMapper() {
    	return myMapper;
    }

	private Integer getTotalSize(List<ParameterWrapper> paramList) {
		final Long totalSize;
		if (paramList.size() == 0) {
			totalSize = getMyMapper().getSize();
		} else {
			totalSize = getMyMapper().getSize(paramList);
		}
		
		return totalSize.intValue();
	}

	/**
	 * The "@Create" annotation indicates that this method implements "create=type", which adds a 
	 * new instance of a resource to the server.
	 * 
	 * OMOP DeviceExposure is more like device usage info. So, the device resource
	 * does not have enough information to create DeviceExposure in OMOP. Device
	 * needs to be created from DeviceUseStatement resource.
	 */
//	@Create()
//	public MethodOutcome createDevice(@ResourceParam Device theDevice) {
//		validateResource(theDevice);
//		
//		Long id=null;
//		try {
//			id = getMyMapper().toDbase(theDevice, null);
//		} catch (FHIRException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		return new MethodOutcome(new IdDt(id));
//	}

	@Delete()
	public void deleteDevice(@IdParam IdType theId) {
		if (getMyMapper().removeByFhirId(theId) <= 0) {
			throw new ResourceNotFoundException(theId);
		}
	}

	@Search()
	public IBundleProvider findDevicesById(
			@RequiredParam(name=Device.SP_RES_ID) TokenParam theDeviceId) {
		List<ParameterWrapper> paramList = new ArrayList<ParameterWrapper> ();

		if (theDeviceId != null) {
			paramList.addAll(getMyMapper().mapParameter (Device.SP_RES_ID, theDeviceId, false));
		}
		
		MyBundleProvider myBundleProvider = new MyBundleProvider(paramList);
		myBundleProvider.setTotalSize(getTotalSize(paramList));
		myBundleProvider.setPreferredPageSize(preferredPageSize);
		
		return myBundleProvider;
	}

	@Search()
	public IBundleProvider findDevicesByParams(
			@OptionalParam(name=Device.SP_TYPE) TokenOrListParam theOrTypes
			) {
		List<ParameterWrapper> paramList = new ArrayList<ParameterWrapper> ();

		if (theOrTypes != null) {
			List<TokenParam> types = theOrTypes.getValuesAsQueryTokens();
			boolean orValue = true;
			if (types.size() <= 1) {
				orValue = false;
			}
			for (TokenParam type: types) {
				paramList.addAll(getMyMapper().mapParameter (Device.SP_TYPE, type, orValue));
			}
		}
		MyBundleProvider myBundleProvider = new MyBundleProvider(paramList);
		myBundleProvider.setTotalSize(getTotalSize(paramList));
		myBundleProvider.setPreferredPageSize(preferredPageSize);
		
		return myBundleProvider;
	}
	
	@Read()
	public MyDevice readPatient(@IdParam IdType theId) {
		MyDevice retval = (MyDevice) getMyMapper().toFHIR(theId);
		if (retval == null) {
			throw new ResourceNotFoundException(theId);
		}
			
		return retval;
	}
	
	@Update()
	public MethodOutcome updateDevice(@IdParam IdType theId, @ResourceParam MyDevice theDevice) {
		validateResource(theDevice);

		Long fhirId = null;
		try {
			fhirId = getMyMapper().toDbase(theDevice, theId);
		} catch (FHIRException e) {
			e.printStackTrace();
		}
		if (fhirId == null) {
			throw new ResourceNotFoundException(theId);
		}
		
		return new MethodOutcome();
	}

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Device.class;
	}

	// TODO: Add more validation code here.
	private void validateResource(Device theDevice) {
	}

	class MyBundleProvider extends OmopFhirBundleProvider implements IBundleProvider {
		public MyBundleProvider(List<ParameterWrapper> paramList) {
			super(paramList);
			setPreferredPageSize (preferredPageSize);
		}

		@Override
		public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
			List<IBaseResource> retv = new ArrayList<IBaseResource>();
			
			// _Include
			List<String> includes = new ArrayList<String>();

			if (paramList.size() == 0) {
				getMyMapper().searchWithoutParams(theFromIndex, theToIndex, retv, includes);
			} else {
				getMyMapper().searchWithParams(theFromIndex, theToIndex, paramList, retv, includes);
			}

			return retv;
		}

	}

}
