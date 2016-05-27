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
public class CorruptFileException extends IOException {

	public CorruptFileException() {
		super("This cannot be read. The data may have been tampered with.");
	}
}
