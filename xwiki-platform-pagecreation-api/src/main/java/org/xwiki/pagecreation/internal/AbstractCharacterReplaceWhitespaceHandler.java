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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xwiki.pagecreation.WhitespaceHandler;

/**
 * Abstract whitespace handler that replaces whitespaces with a single char. Note that leading or trailing whitespaces
 * are ignored (they should be handled by {@link InputTransformer}). Note also that several consecutive whitespaces will
 * be replaced by a single character.
 * 
 * @version $Id$
 */
public abstract class AbstractCharacterReplaceWhitespaceHandler implements WhitespaceHandler
{
    /**
     * Implementations must implement this to specify which character to replace the whitespaces with.
     * 
     * @return the replacement character
     */
    protected abstract String getCharacter();

    @Override
    public String handle(String input)
    {
        // Replace all non-leading, non-trailing whitespaces with the defined char
        Pattern pattern = Pattern.compile("([^\\s]+)(\\s+)([^\\s]+)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.replaceFirst("$1" + this.getCharacter() + "$3");
        }
        return input;
    }
}
