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
 * Represents a transformation applied to user input.
 * 
 * User Input can be any text typed on a page creation form.
 * 
 * @version $Id$
 */
@Role
public interface InputTransformer
{
    /**
     * Perform the transformation.
     * 
     * @param input the original input
     * @return the transformed input
     */
    String transform(String input);
}
