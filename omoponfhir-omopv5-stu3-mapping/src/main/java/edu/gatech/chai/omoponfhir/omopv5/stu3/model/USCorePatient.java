/*******************************************************************************
 * Copyright (c) 2019 Georgia Tech Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package edu.gatech.chai.omoponfhir.omopv5.stu3.model;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeType;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.fhir.model.api.BaseIdentifiableElement;
import ca.uhn.fhir.model.api.IElement;
import ca.uhn.fhir.model.api.IExtension;
import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;

@ResourceDef(name = "Patient", profile = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient")
public class USCorePatient extends Patient {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Description(shortDefinition = "Concepts classifying the person into a named category of humans sharing common history, traits, geographical origin or nationality")
	@Extension(url = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race", isModifier = false, definedLocally = true)
	@Child(name = "race", min = 0, max = 1)
	private Race myRace;

	public Race getRace() {
		if (myRace == null) {
			return new Race();
		} 
		
		return this.myRace;
	}
	
	public void setRace(Race myRace) {
		this.myRace = myRace;
	}
	
	/**
	 * This "block definition" defines an extension type with multiple child
	 * extensions. It is referenced by the field myRace above.
	 */
	@Block
	public static class Race extends BaseIdentifiableElement implements IExtension {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * This is a primitive datatype extension
		 */
		@Description(shortDefinition = "The 5 race category codes according to the OMB Standards")
		@Extension(url = "ombCategory", isModifier = false, definedLocally = true)
		@Child(name = "category", min = 0, max = 5)
		private List<Coding> myCategory;

		public List<Coding> getCategory() {
			if (myCategory.size() == 0) {
				return new ArrayList<Coding>();
			}
			
			return myCategory;
		}

		public void setCategory(List<Coding> myCategory) {
			this.myCategory = myCategory;
		}

		/*
		 * ***************************** Boilerplate methods- Hopefully these will be
		 * removed or made optional in a future version of HAPI but for now they need to
		 * be added to all block types. These two methods follow a simple pattern where
		 * a utility method from ElementUtil is called and all fields are passed in.
		 *****************************/

		@Override
		public <T extends IElement> List<T> getAllPopulatedChildElementsOfType(Class<T> theType) {
			return ElementUtil.allPopulatedChildElements(theType, myCategory);
		}

		@Override
		public boolean isEmpty() {
			return ElementUtil.isEmpty(myCategory);
		}

	}
}
