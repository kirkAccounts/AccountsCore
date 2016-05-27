/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 27, 2016
 */
package com.ianmann.kirkAccounts.errors;

import java.io.IOException;

/**
 * @TODO: TODO
 *
 * @author Ian
 * Created: May 27, 2016
 *
 */
public class WrongFileTypeException extends IOException {

	public WrongFileTypeException() {
		super("This file type cannot be read by this editor.");
	}
}
