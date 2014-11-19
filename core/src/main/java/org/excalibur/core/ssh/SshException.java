/**
 *     Copyright (C) 2013-2014  the original author or authors.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License,
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.excalibur.core.ssh;

public class SshException extends RuntimeException {
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization.
	 */
	private static final long serialVersionUID = -8622060504129188406L;

	public SshException() {
		super();
	}

	public SshException(String message) {
		super(message);
	}

	public SshException(Throwable cause) {
		super(cause);
	}

	public SshException(String message, Throwable cause) {
		super(message, cause);
	}
}
