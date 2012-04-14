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
package org.xwiki.pagecreation.internal;

import org.xwiki.component.annotation.Component;
import org.xwiki.pagecreation.WhitespaceHandler;

/**
 * Whitespace handler that creates a camelCase representation of the passed input.
 * 
 * @version $Id$
 */
@Component("camelCase")
public class CamelCaseWhitespaceHandler implements WhitespaceHandler
{

    @Override
    public String handle(String input)
    {
        // First replace multiple whitespaces with a single space
        String cleanedInput = input.replaceAll("\\s+", " ");
        StringBuffer result = new StringBuffer(cleanedInput.length());
        boolean mustCapitalize = false;

        for (int i = 0; i < cleanedInput.length(); i++) {
            char c = cleanedInput.charAt(i);
            if (c != ' ') {
                if (mustCapitalize) {
                    result.append(cleanedInput.substring(i, i + 1).toUpperCase());
                    mustCapitalize = false;
                } else {
                    result.append(c);
                }
            } else {
                mustCapitalize = true;
            }
        }

        return result.toString();
    }

}
