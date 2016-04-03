/*******************************************************************************
 * Copyright (c) 2013 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.trimatek.mozo.ui;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.exception.MozoException;
import org.trimatek.mozo.service.MozoService;

public class MozoServiceComponent {

	// Called by DS upon ITimeService discovery
	void bindTimeService(MozoService mozoService) {
		System.out.println("Discovered ITimeService via DS.  Instance=" + mozoService);
		// Call the service and print out result!
		try {
			System.out.println("Current time on remote is: " + mozoService.loadJarProxy(new Version()));
		} catch (MozoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Called by DS upon ITimeService undiscovery
	void unbindTimeService(MozoService mozoService) {
		System.out.println("Undiscovered ITimeService via DS.  Instance=" + mozoService);
	}
}
