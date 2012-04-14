/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.pagecreation;

import org.xwiki.component.annotation.Role;

/**
 * Represents a strategy for handling whitespaces in page creation. The idea is to offer the ability to implement
 * strategies to remove/transform white spaces in URLs, as those can be considered un-elegant, SEO unfriendly, or can
 * break in certain tools (mail client, IM client, etc.) when copy/pasted. Common strategies will be to replace them
 * with dashed or underscores, or to remove them altogether by creating a camelCase representation of the page name.
 * 
 * @version $Id$
 */
@Role
public interface WhitespaceHandler
{
    /**
     * Performs the operation to handle whitespaces for the given input.
     * 
     * @param input the input to handle whitespaces for
     * @return the transformed result
     */
    String handle(String input);
}
